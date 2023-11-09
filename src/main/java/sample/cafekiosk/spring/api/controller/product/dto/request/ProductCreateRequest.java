package sample.cafekiosk.spring.api.controller.product.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCreateRequest {

    @NotNull(message = "상품 타입은 필수입니다.")
    private ProductType type;

    @NotNull(message = "상품 판매상태는 필수입니다.")
    private ProductSellingStatus sellingStatus;

//    @NotNull // null 이 아니여야 한다 "", " "는 통과가 됨
//    @NotEmpty // " " 공백은 통과하고 ""는 안됨
//    @Max(20) // String name -> 상품 이름은 20자 제한
    @NotBlank(message = "상품 이름은 필수입니다.") // 문자도 있어야 하고 공백도 안됨
    private String name;

    @Positive(message = "상품 가격은 양수여야 합니다.") // 양수만 가능
    private int price;


    /**
     * controller 에서 requestBody로 받는 ProductCreateRequest는 하위계층 (service계층)으로 넘겨줄때 toServiceRequest() builder 메소드를 사용하여 새로운 객체로 넘겨준다
     * 그럼 ProductCreateRequest 객체의 builder는 테스트를 위해서만 사용하는 팩토리 메소드이다
     * 그럼 테스트를 위해 만들어진 이 팩토리메소드는 테스트에서만 사용하니까 차라리 사용하지 말고 테스트를 안하는게 답일까?
     * 결론은 "사실 만들어도 된다" 이다
     * 하지만 보수적으로 접근해야 한다
     * controller에서는 request로 데이터를 받아야 하기 때문에 생성자가 있어야 한다
     * 무엇을 테스트 하고 있는지를 명확하게 해야 한다
     * 어떤 객체가 마땅히 가져도 되는 행위가 되면서 미래에도 충분히 사용이 될법한 것들의 성격이 있는 메소드는 사용해야 된다 (getter, constructor, Collections.size()...)
     */
    @Builder
    public ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                .type(this.type)
                .sellingStatus(this.sellingStatus)
                .name(this.name)
                .price(this.price)
                .build();
    }

}
