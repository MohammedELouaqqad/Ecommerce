package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderPersistenceService;
import com.example.demo.service.OrderService;

/**
 * Une commande engage de l'argent et du stock. Ces tests visent les cas où le
 * client pourrait décider à la place du serveur, et celui où du stock resterait
 * réservé sans commande. Le reste du service n'a pas besoin de tests.
 */
class OrderCreationSafetyTest {

    private static final String PRODUCT = "http://product-service:8082";
    private static final Long CALLER_ID = 7L;
    private static final String CALLER_EMAIL = "alice@shop.local";
    private static final Long SOMEONE_ELSE_ID = 42L;

    private OrderService orderService;
    private OrderPersistenceService persistence;
    private MockRestServiceServer productService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        productService = MockRestServiceServer.bindTo(builder).build();
        persistence = mock(OrderPersistenceService.class);
        when(persistence.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));
        orderService = new OrderService(mock(OrderRepository.class), persistence, builder.build());
    }

    private void productCosts10() {
        productService.expect(requestTo(PRODUCT + "/api/customer/product/5"))
                .andRespond(withSuccess("{\"id\":5,\"price\":10.0,\"countStock\":100}", MediaType.APPLICATION_JSON));
    }

    private Order orderClaiming(Long orderId, Long userId, String email, String status) {
        OrderItem item = new OrderItem();
        item.setId(123L);          // un identifiant de ligne existante
        item.setProductId(5L);
        item.setQuantite(2);
        item.setPrice(0.01);       // prix bradé, choisi par le client
        item.setTotalPrice(0.02);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setCustomerEmail(email);
        order.setStatus(status);
        order.setTotalprice(0.02);
        order.setOrderItems(List.of(item));
        return order;
    }

    @Test
    @DisplayName("Le corps de la requête ne décide ni du propriétaire, ni du prix, ni du statut")
    void theRequestBodyCannotChooseOwnerPriceOrStatus() {
        productCosts10();
        productService.expect(requestTo(PRODUCT + "/api/internal/stock/reserve")).andRespond(withSuccess());

        Order claimed = orderClaiming(99L, SOMEONE_ELSE_ID, "bob@shop.local", "Completed");
        ResponseEntity<?> response = orderService.CreateOrder(claimed, CALLER_ID, CALLER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Order saved = (Order) response.getBody();
        assertThat(saved).isNotNull();
        // L'identité vient des en-têtes posés par la passerelle, jamais du corps.
        assertThat(saved.getUserId()).isEqualTo(CALLER_ID);
        assertThat(saved.getCustomerEmail()).isEqualTo(CALLER_EMAIL);
        // Un id envoyé par le client ferait écraser une commande existante par JPA.
        assertThat(saved.getId()).isNull();
        assertThat(saved.getOrderItems().get(0).getId()).isNull();
        // Le statut et les prix sont décidés par le serveur.
        assertThat(saved.getStatus()).isEqualTo("Processing");
        assertThat(saved.getOrderItems().get(0).getPrice()).isEqualTo(10.0);
        assertThat(saved.getTotalprice()).isEqualTo(20.0);
        productService.verify();
    }

    @Test
    @DisplayName("Si l'enregistrement échoue, le stock réservé est rendu")
    void reservedStockIsGivenBackWhenTheOrderCannotBeSaved() {
        productCosts10();
        productService.expect(requestTo(PRODUCT + "/api/internal/stock/reserve")).andRespond(withSuccess());
        productService.expect(requestTo(PRODUCT + "/api/internal/stock/release"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());
        when(persistence.save(any(Order.class))).thenThrow(new RuntimeException("base indisponible"));

        ResponseEntity<?> response = orderService.CreateOrder(
                orderClaiming(null, CALLER_ID, CALLER_EMAIL, "Processing"), CALLER_ID, CALLER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        // Sans cet appel, 2 unités disparaîtraient du stock sans commande en face.
        productService.verify();
    }

    @Test
    @DisplayName("Stock insuffisant : aucune commande n'est enregistrée")
    void noOrderIsSavedWhenTheStockIsShort() {
        productCosts10();
        productService.expect(requestTo(PRODUCT + "/api/internal/stock/reserve"))
                .andRespond(withStatus(HttpStatus.CONFLICT).body("Not enough stock for product 5"));

        ResponseEntity<?> response = orderService.CreateOrder(
                orderClaiming(null, CALLER_ID, CALLER_EMAIL, "Processing"), CALLER_ID, CALLER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        // L'ancienne version enregistrait la commande avant de réserver le stock,
        // ce qui laissait des commandes orphelines.
        verify(persistence, never()).save(any(Order.class));
        productService.verify();
    }
}
