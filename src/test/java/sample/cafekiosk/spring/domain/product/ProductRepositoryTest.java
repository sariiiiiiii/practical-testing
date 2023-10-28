package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@DataJpaTest // 스프링 서버를 띄어서 테스트를 하는데 스프링부트 테스트보다 가볍고 Jpa 관련된 빈들만 주입을 해줘서 서버를 띄우기 때문에 가벼운 장점이 있음
//@SpringBootTest // 스프링 서버를 띄어서 테스트를 할 수 있음
class ProductRepositoryTest {

    /**
     * Repository의 테스트는 단위 테스트의 성격을 많이 갖는다
     * 레이어드 아키텍처에서의 Repository는 Persistence 계층이기 때문에 data access 하는 로직밖에 없기 때문에
     * 단위테스트의 성격을 많이 갖는다
     */

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다")
    void findAllBySellingStatusIn() {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();
        Product product3 = Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        //then
        assertThat(products).hasSize(2) // 목록의 사이즈 체크
                .extracting("productNumber", "name", "sellingStatus") // 검증하고자 하는 필드만 추출할 수 있음
                .containsExactlyInAnyOrder( // 검증 할 필드의 데이터를 순서 상관없이
                        tuple("001", "아메리카노", SELLING), // 검증할 데이터
                        tuple("002", "카페라떼", HOLD) // 검증할 데이터
                );


    }

    @Test
    @DisplayName("상품번호 리스트로 상품들을 조회한다")
    void findAllByProductNumberIn() {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();
        Product product3 = Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        //then
        assertThat(products).hasSize(2) // 목록의 사이즈 체크
                .extracting("productNumber", "name", "sellingStatus") // 검증하고자 하는 필드만 추출할 수 있음
                .containsExactlyInAnyOrder( // 검증 할 필드의 데이터를 순서 상관없이
                        tuple("001", "아메리카노", SELLING), // 검증할 데이터
                        tuple("002", "카페라떼", HOLD) // 검증할 데이터
                );


    }

}