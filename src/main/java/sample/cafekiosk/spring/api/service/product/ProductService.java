package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * readOnly = true : 읽기전용
 * CRUD 에서 CUD 작업이 동작 X / only read
 * JPA : CUD 스냅샷 저장, 변경감지 X (성능 향상)
 *
 * CQRS - Command / Query 책임을 서로 분리 (서로 연관이 없게끔)
 * Command : CUD @Transactional
 * Query : Read @Transactional(readOnly = true) 로 나누어 커멘드와 쿼리용 서비스를 분리하여 사용할 수 있다
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductNumberFactory productNumberFactory;

    // 동시성 이슈
    // UUID
    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        // productNumber
        // 001 002 003 004
        // DB 에서 마지막 저장된 Product의 상품 번호를 읽어와서 +1
        // 마지막 상품번호가 009 였으면 -> 010

        // nextProductNumber
        String nextProductNumber = productNumberFactory.createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product saveProduct = productRepository.save(product);

        return ProductResponse.of(saveProduct);

        /**
         * 레드 -> 그린 -> 리팩토링에 따라
         * 빠르게 그린을 보는게 중요하기 때문에 리턴값으로 하드코딩을 한 후 빠르게 그린을 보는방법도 가능하다
         */
//        return ProductResponse.builder()
//                .productNumber("002")
//                .type(ProductType.HANDMADE)
//                .sellingStatus(ProductSellingStatus.SELLING)
//                .name("카푸치노")
//                .price(5000)
//                .build();
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());
        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * private 한 createNextProductNumber() 메소드를 테스트를 해보고 싶으면
     * product를 생성하는 로직과 productNumber를 만들어내는 로직의 책임을 분리를 함으로써
     * 테스트를 별도로 가져갈 수 있다 => ProductNumberFactory()
     *
     * - 정리
     * private 메소드를 억지로 테스트를 할 필요가 없다
     * 객체가 공개한 api 들을 테스트 하다 보면 자연스럽게 검증이 되기 때문에
     * 하지만 만약에 그런 욕망이 강해진다면 객체 분리의 신호로 봐야되기 때문에 private 메소드를 별도의 객체로 나누어서 테스트를 해볼 수 있다
     */
//    private String createNextProductNumber() {
//        String latestProductNumber = productRepository.findLatestProductNumber();
//        if (latestProductNumber == null) {
//            return "001";
//        }
//
//        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
//        int nextProductNumberInt = latestProductNumberInt + 1;
//
//        // String.format() 3 -> 003 으로 변환
//        return String.format("%03d", nextProductNumberInt);
//    }

}
