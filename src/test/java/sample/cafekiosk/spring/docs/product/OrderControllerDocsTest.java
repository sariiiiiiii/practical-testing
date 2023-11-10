package sample.cafekiosk.spring.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.spring.api.controller.order.OrderController;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.controller.order.response.OrderResponse;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.docs.RestDocsSupport;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @Test
    @DisplayName("신규 주문을 하는 API")
    void createOrder() throws Exception {

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

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
        given(orderService.createOrder(any(OrderCreateServiceRequest.class), any(LocalDateTime.class)))
                .willReturn(
                        OrderResponse.builder()
                                .id(1L)
                                .totalPrice(8500)
                                .registeredDateTime(LocalDateTime.now())
                                .products(productResponses)
                                .build()
                );

        mockMvc.perform(
                        post("/api/v1/orders/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "order-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productNumbers").type(JsonFieldType.ARRAY)
                                        .description("상품 번호 리스트")
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
                                        .description("주문 아이디"),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                        .description("주문 가격"),
                                fieldWithPath("data.registeredDateTime").type(JsonFieldType.ARRAY)
                                        .description("주문 시간"),
                                fieldWithPath("data.products").type(JsonFieldType.ARRAY)
                                        .description("주문 상품"),
                                fieldWithPath("data.products[].id").type(JsonFieldType.NUMBER)
                                        .description("주문 상품 아이디"),
                                fieldWithPath("data.products[].productNumber").type(JsonFieldType.STRING)
                                        .description("주문 상품 아이디"),
                                fieldWithPath("data.products[].type").type(JsonFieldType.STRING)
                                        .description("주문 상품 아이디"),
                                fieldWithPath("data.products[].sellingStatus").type(JsonFieldType.STRING)
                                        .description("주문 상품 아이디"),
                                fieldWithPath("data.products[].name").type(JsonFieldType.STRING)
                                        .description("주문 상품 아이디"),
                                fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER)
                                        .description("주문 상품 아이디")
                        )
                ));
    }

}
