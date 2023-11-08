package sample.cafekiosk.spring.api.service.product;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    /**
     * 테스트 목적
     * 1. 상품번호를 조회했을 때 아무것도 없으면 001로 만들어지는지
     * 2. 상품번호가 조회 됐을 때 그 번호보다 증가된 값으로 저장이 되는지
     */

    @BeforeAll // 클래스 테스트 실행 전에 한번 실행되는거
    static void beforeAll() {

    }

    @BeforeEach // 매 테스트마다 실행전에 실행되는거
    void setUp() {
        /**
         * static 한 변수 처럼 공유한 데이터로 각 테스트 마다 진행이 되게 되면 테스트에 문제가 생길 수 있다
         * 그럼 before method는 언제 사용을 해야 하는가?
         *
         * 각 테스트 입장에서 봤을 때 : 아예 몰라도 테스트 내용을 이해하는 데에 문제가 없는가?
         * 수정해도 모든 테스트에 영향을 주지 않는가?
         *
         * 이 조건이 들어가게 되면 before method를 사용된다라고 생각한다
         */
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다.")
    void createProduct() {
        // given
        Product product = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.save(product);

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build(); // request 자체도 given에서 만드는 data

        // when
        ProductResponse productResponse = productService.createProduct(request.toServiceRequest());

        //then
        assertThat(productResponse)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("002", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                        Tuple.tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );

    }

    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001이다.")
    void createProductWhenProductIsEmpty() {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build(); // request 자체도 given에서 만드는 data

        // when
        ProductResponse productResponse = productService.createProduct(request.toServiceRequest());

        //then
        assertThat(productResponse)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
                );

    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }

}