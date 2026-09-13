package com.example.demo.service;


import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.StockLine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;



@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final String PRODUCT_SERVICE_URL = "http://product-service:8082";

    private final OrderRepository orderRepository;

    private final OrderPersistenceService orderPersistenceService;

    private final RestClient restClient;

    public OrderService(OrderRepository orderRepository,
                        OrderPersistenceService orderPersistenceService,
                        RestClient restClient) {
        this.orderRepository = orderRepository;
        this.orderPersistenceService = orderPersistenceService;
        this.restClient = restClient;
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public ResponseEntity<?> CreateOrder(Order newOrder) {

        List<OrderItem> orderItems = newOrder.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Order must contain at least one item");
        }
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProductId() == null
                    || orderItem.getQuantite() == null
                    || orderItem.getQuantite() <= 0) {
                return ResponseEntity.badRequest()
                        .body("Each item needs a productId and a positive quantite");
            }
        }

        List<StockLine> stockLines = orderItems.stream()
                .map(orderItem -> new StockLine(orderItem.getProductId(), orderItem.getQuantite()))
                .toList();

        try {
            // 1. Price every item at the moment of purchase
            double total = 0;

            for (OrderItem orderItem : orderItems) {

                ProductResponse product = restClient.get()
                        .uri(PRODUCT_SERVICE_URL + "/api/customer/product/"
                                + orderItem.getProductId())
                        .retrieve()
                        .body(ProductResponse.class);
                if (product == null) {
                    return ResponseEntity.notFound().build();
                }

                orderItem.setPrice(product.getPrice());
                orderItem.setTotalPrice(
                        product.getPrice() * orderItem.getQuantite()
                );
                total += orderItem.getTotalPrice();
            }

            newOrder.setOrderDate(LocalDate.now());
            newOrder.setTotalprice(total);

            // 2. Reserve the stock of every item, all or nothing:
            //    product-service answers 409 if any product is short.
            restClient.post()
                    .uri(PRODUCT_SERVICE_URL + "/api/internal/stock/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(stockLines)
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (HttpClientErrorException.Conflict e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Call to product-service failed", e);
            return ResponseEntity.internalServerError().body("Error in the Server");
        }

        // 3. Only once the stock is reserved, save the order and its items.
        //    If that fails, give the reserved stock back (compensation).
        try {
            return ResponseEntity.ok(orderPersistenceService.save(newOrder));
        } catch (RuntimeException e) {
            log.error("Order could not be saved, releasing the reserved stock", e);
            releaseStock(stockLines);
            return ResponseEntity.internalServerError().body("Error in the Server");
        }
    }

    private void releaseStock(List<StockLine> stockLines) {
        try {
            restClient.post()
                    .uri(PRODUCT_SERVICE_URL + "/api/internal/stock/release")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(stockLines)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            // The stock stays decremented with no matching order: it needs a manual fix.
            log.error("Stock release FAILED, manual correction needed for {}", stockLines, e);
        }
    }

    public ResponseEntity<?> modifyOrder( Long id, Order order) {
        try{
            Optional<Order> editOrder = orderRepository.findById(id);
            editOrder.get().setStatus(order.getStatus());
            orderRepository.save(editOrder.get());

            return ResponseEntity.ok(editOrder.get());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Error in the Server:"+e);
        }
    }
}
