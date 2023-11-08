package sample.cafekiosk.spring.api.service.order;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.controller.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

//@Transactional
class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    StockRepository stockRepository;

    /**
     * @DataJpaTest 어노테이션을 쓰게 되면 개별 테스트 메소드가 끝날 때 마다 롤백을 해주기 때문에
     * tearDown()같은 메소드로 클리어를 시킬 필요가 없다
     * 하지만 @DataJpaTest같은 경우는 data access 용 어노테이션이기 때문에 service 계층의 컴포넌트를
     * 주입받을 수 없기 때문에 @SpringBootTest를 사용했다
     * 그럼 @Transactional 어노테이션을 쓰면 롤백을 쓰게 되면 롤백이 잘 된다
     * 하지만 @Transactional 쓰게 되면 문제가 생길 수 있다
     * 어떤 문제가 생길까?
     * tearDown()을 하지 않고 @Transactional 어노테이션을 사용하게 되면 tearDown() 코드를 수동으로 작성하지 않고 자동으로 롤백이 되게 때문에
     * 아주 편리하다고 할 수 있다
     * 하지만 production code에서 @Transactioanl 코드가 없어도 예를들어 jpa 같은 경우는 변경감지가 일어나게 된다
     * 테스트를 통과했다고 해서 프로덕션 코드에서의 변경감지까지 이뤄진거 처럼 보이기 때문에
     * 항상 인지를 하고 쓰도록 하자. 강의에서는 @Transactioanl을 사용하지 않고 tearDown()을 이용하고
     * 데이터를 지워(롤백)주도록 하자.
     */

    @AfterEach
    void tearDown() {

        /**
         * 이 클래스에서의 테스트들이 서로 영향을 주고 있기 때문에 전체테스트를 돌릴경우 오류가 발생하고 있다
         * 해결하기 위해 @AfterEach 어노테이션을 써서 deleteAllInBatch()를 사용해주도록 하자
         */

        /**
         * deleteAll() 과 deleteAllInBatch()의 차이
         * deleteAllinBatch()는 delete 문을 bulk 성으로 편리하게 다 지워주는데 연관관계가 잡혀있는 엔티티에서는 순서를 신경을 써줘야 한다
         * deleteAll()는 쿼리 로그를 보면
         * 건마다 Order를 한번 조회하고 Order에 연관관계가 있는 orderproduct를 다시 조회하고 orderProduct를 지우고 그다음 order를 또 지운다 쿼리가 굉장히 많이 나가는 것을 볼 수 있다
         * 그렇다고 순서를 신경 안써도 되는 것은 아니고 설계한 엔티티 모양에 따라서 순서를 신경도 써줘야 한다
         */

        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    void createOrder() {

        /**
         * ProductRepositoryTest 에서 상품을 생성할 때 빌더패턴이기 때문에 코드가 너무 길어진다
         * 그래서 OrderServiceTest 에서는 상품을 생성해주는 메소드를 하나 추가해주자
         */

        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        // when
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        //then
        assertThat(orderResponse.getId()).isNotNull(); // 해당 값이 null인지 아닌지 = isPresent()
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 4000);

        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 1000),
                        Tuple.tuple("002", 3000)
                );
    }

    @Test
    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001")) // 중복된 상품
                .build();

        // when
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        //then
        assertThat(orderResponse.getId()).isNotNull(); // 해당 값이 null인지 아닌지 = isPresent()
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 2000);

        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 1000),
                        Tuple.tuple("001", 1000)
                );

    }

    @Test
    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    void createOrderWithStock() {

        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);

        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        //then
        assertThat(orderResponse.getId()).isNotNull(); // 해당 값이 null인지 아닌지 = isPresent()
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 10000);

        assertThat(orderResponse.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 1000),
                        Tuple.tuple("001", 1000),
                        Tuple.tuple("002", 3000),
                        Tuple.tuple("003", 5000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 0),
                        Tuple.tuple("002", 1)
                );

    }

    @Test
    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    void createOrderWithNoStock() {

        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);

        /**
         * given 절에서 stock1.deductQuantity(1) 메소드로 인해 테스트를 실패할 수가 있기 때문에 이 맥락을 한번 더 생각을 해봐야 한다
         * stock 001의 수량을 2개로 했는데 1개 감소를 했네? 라는 논리구조가 들어가게 된다
         * given 절을 구성하는데 한번더 맥락을 이해해야 한다
         * 또, 해당 기능의 문제가 생겼을 때 stock1.deductQuantity(3) 이렇게 하면 실패하게 된다
         * 그렇게 되면 given 절에서 테스트가 깨지기 때문에 테스트 주제와 맞지 않는다
         *
         * 이렇게 2가지 문제점이 생기기 때문에 환경구성을 할 떄는 최대한 생성자 기반으로 구성하는 것이 좋다
         */

        stock1.deductQuantity(1); // todo
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when // then
        assertThatThrownBy(() -> orderService.createOrder(request.toServiceRequest(), registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("재고가 부족한 상품이 있습니다.");

    }

    private Product createProduct(String productNumber, int price) {
        return Product.builder()
                .type(ProductType.HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

    private Product createProduct(ProductType productType, String productNumber, int price) {
        return Product.builder()
                .type(productType)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

}