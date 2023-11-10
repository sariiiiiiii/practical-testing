package sample.cafekiosk.spring.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.docs.RestDocsSupport;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerDocsTest extends RestDocsSupport {

    // Spring 으로 진행하는것이 아니기 때문에 해당 방법 이용
    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @Test
    @DisplayName("신규 상품을 등록하는 API")
    void createProduct() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // stubbing
        given(productService.createProduct(any(ProductCreateServiceRequest.class)))
                .willReturn(
                        ProductResponse.builder()
                                .id(1L)
                                .productNumber("001")
                                .type(ProductType.HANDMADE)
                                .sellingStatus(ProductSellingStatus.SELLING)
                                .name("아메리카노")
                                .price(4000)
                                .build()
                );

        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk()) // 여기까지는 똑같다
                // MockMvcRestDocumentation.document()의 첫번 째 인자는 해당 테스트에 대한 id 값이라고 생각하면 된다 맘대로 정하면 된다
                // 두 번째 인자로는 이 요청을 했을 때 어떤 요청을 넣고 어떤 응답을 넣을 것인가에 대한 정의를 해주면 된다
                .andDo(document("product-create",
                        preprocessRequest(prettyPrint()), // json 데이터를 예쁜 형태로 만들어줌
                        preprocessResponse(prettyPrint()), // json 데이터를 예쁜 형태로 만들어줌
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING) // enum은 다 string
                                        .description("상품 타입"),
                                fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품 판매 상태").optional(),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("상품 아이디"),
                                fieldWithPath("data.productNumber").type(JsonFieldType.STRING)
                                        .description("상품 번호"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING)
                                        .description("상품 타입"),
                                fieldWithPath("data.sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품 상태"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        )
                ));
    }

    @Test
    @DisplayName("판매 목록을 가져오는 API")
    void getSellingProducts() throws Exception {
        ProductResponse productResponse1 = ProductResponse.builder()
                .id(1L)
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        ProductResponse productResponse2 = ProductResponse.builder()
                .id(2L)
                .productNumber("002")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("카페라떼")
                .price(4500)
                .build();
        List<ProductResponse> productResponses = List.of(productResponse1, productResponse2);
        // stubbing
        given(productService.getSellingProducts())
                .willReturn(productResponses);

        mockMvc.perform(
                        get("/api/v1/products/selling")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("products-selling",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("상품 아이디"),
                                fieldWithPath("data[].productNumber").type(JsonFieldType.STRING)
                                        .description("상품 번호"),
                                fieldWithPath("data[].type").type(JsonFieldType.STRING)
                                        .description("상품 타입"),
                                fieldWithPath("data[].sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품 상태"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        )
                ));

    }

}
