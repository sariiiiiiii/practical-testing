package sample.cafekiosk.spring.domain.order;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("주문시간과 주문상태로 주문을 조회한다.")
    void findOrdersBy() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 11, 6, 17, 0);
        LocalDateTime startDateTime = LocalDateTime.of(2023, 11, 6, 16, 59);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 11, 6, 17, 1);
        Product product1 = createProduct();
        List<Product> products = productRepository.saveAll(List.of(product1));
        Order order = Order.create(products, registeredDateTime);
        orderRepository.save(order);

        // when
        List<Order> orders = orderRepository.findOrdersBy(startDateTime, endDateTime, OrderStatus.INIT);

        // then
        assertThat(orders).hasSize(1)
                .extracting("orderStatus", "totalPrice", "registeredDateTime")
                .contains(Tuple.tuple(
                        OrderStatus.INIT, 4000, registeredDateTime)
                );
    }

    @Test
    @DisplayName("주문시간과 주문상태로 주문을 조회할 때 주문이 하나도 없는 경우에는 null을 반환한다.")
    void findOrdersByIsEmpty() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 11, 6, 17, 0, 0);
        LocalDateTime startDateTime = LocalDateTime.of(2023, 11, 6, 17, 0, 1);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 11, 6, 17, 0, 2);
        Product product1 = createProduct();
        List<Product> products = productRepository.saveAll(List.of(product1));
        Order order = Order.create(products, registeredDateTime);
        orderRepository.save(order);

        // when
        List<Order> orders = orderRepository.findOrdersBy(startDateTime, endDateTime, OrderStatus.INIT);

        // then
        assertThat(orders).isEmpty();
    }

    private Product createProduct() {
        return Product.builder()
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
    }

}