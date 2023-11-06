package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

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

    // 동시성 이슈
    // UUID
    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        // productNumber
        // 001 002 003 004
        // DB 에서 마지막 저장된 Product의 상품 번호를 읽어와서 +1
        // 마지막 상품번호가 009 였으면 -> 010

        // nextProductNumber
        String nextProductNumber = createNextProductNumber();

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

    private String createNextProductNumber() {
        String latestProductNumber = productRepository.findLatestProductNumber();
        if (latestProductNumber == null) {
            return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumberInt = latestProductNumberInt + 1;

        // String.format() 3 -> 003 으로 변환
        return String.format("%03d", nextProductNumberInt);
    }

}
