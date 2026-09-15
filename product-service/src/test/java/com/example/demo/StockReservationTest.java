package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.dto.StockLine;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.models.ProductEntity;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

/**
 * Le stock est la donnée la plus fragile du projet : elle est modifiée par
 * plusieurs clients en même temps. Ces tests visent les trois façons de la
 * corrompre, pas le CRUD du catalogue.
 */
@SpringBootTest
class StockReservationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clean() {
        productRepository.deleteAll();
    }

    private Long newProduct(String name, int stock) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setPrice(10.0);
        product.setCountStock(stock);
        return productRepository.save(product).getId();
    }

    private int stockOf(Long id) {
        return productRepository.findById(id).orElseThrow().getCountStock();
    }

    @Test
    @DisplayName("20 clients simultanés sur le dernier exemplaire : un seul est servi")
    void onlyOneCustomerGetsTheLastItem() throws Exception {
        Long productId = newProduct("dernier-exemplaire", 1);

        int customers = 20;
        ExecutorService pool = Executors.newFixedThreadPool(customers);
        CountDownLatch startTogether = new CountDownLatch(1);
        AtomicInteger served = new AtomicInteger();

        for (int i = 0; i < customers; i++) {
            pool.submit(() -> {
                startTogether.await();
                try {
                    productService.reserveStock(List.of(new StockLine(productId, 1)));
                    served.incrementAndGet();
                } catch (Exception alreadySoldOut) {
                    // stock insuffisant, ou ligne verrouillée par un autre client
                }
                return null;
            });
        }
        startTogether.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        // Avec une lecture suivie d'une écriture, plusieurs clients passaient
        // et le stock tombait à 0 en ayant vendu la même unité plusieurs fois.
        assertThat(served.get()).isEqualTo(1);
        assertThat(stockOf(productId)).isZero();
    }

    @Test
    @DisplayName("Un article manquant annule toute la réservation de la commande")
    void aMissingItemRollsBackTheWholeReservation() {
        Long available = newProduct("disponible", 5);
        Long soldOut = newProduct("epuise", 0);

        assertThatThrownBy(() -> productService.reserveStock(List.of(
                new StockLine(available, 2),
                new StockLine(soldOut, 1))))
                .isInstanceOf(InsufficientStockException.class);

        // Sans annulation, les 2 unités du premier article seraient perdues :
        // décomptées, mais rattachées à aucune commande.
        assertThat(stockOf(available)).isEqualTo(5);
        assertThat(stockOf(soldOut)).isZero();
    }

    @Test
    @DisplayName("Une quantité négative est refusée, elle augmenterait le stock")
    void negativeQuantitiesAreRejected() {
        Long productId = newProduct("quantite-invalide", 5);

        assertThatThrownBy(() -> productService.reserveStock(List.of(new StockLine(productId, -5))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productService.releaseStock(List.of(new StockLine(productId, -5))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productService.reserveStock(List.of(new StockLine(productId, null))))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(stockOf(productId)).isEqualTo(5);
    }
}
