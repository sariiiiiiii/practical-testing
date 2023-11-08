package sample.cafekiosk.spring.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sample.cafekiosk.spring.ControllerTestSupport;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest extends ControllerTestSupport {

    /**
     * 서비스레이어 하위로는 mocking 처리
     * mocking 처리를 도와주는 테스트 프레임워크 -> MockMvc
     *
     * @WebMvcTest
     * 컨트롤러 테승트만 하기 위해서 컨트롤러 Bean 들만 올릴 수 있는 가벼운 테스트 어노테이션
     *
     *
     * @MockBean
     * 컨테이너에 mockito로 만든 객체를 넣어주는 역할을 함
     * Bean 객체인 ProductService에 @MockBean을 넣어주면 컨테이너에 ProductService mock 객체를 넣어주게 된다
     */

    /**
     * MockMvc.perfome() -> api를 쏘는 수행을 나타냄
     */

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper; // controller에서 직렬화 형태의 데이터를 받기 때문에 object를 json 직렬화로 시켜주기 위한 라이브러리 ObjectMapper 주입
//
//    @MockBean
//    private ProductService productService;

    @Test
    @DisplayName("신규 상품을 생성한다.")
    void createProduct() throws Exception {
        // given : Product 생성
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        // when // then
        // POST 메소드를 사용해 '/api/v1/products/new' 경로로 요청을 보냄
        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request)) // 요청 본문(body)을 JSON 형식으로 직렬화
                                .contentType(APPLICATION_JSON) // 요청의 Content-Type을 'application/json'으로 설정
                )
                .andDo(print()) // 요청과 응답에 대한 상세한 로그를 출력
                .andExpect(status().isOk()) // HTTP 상태 코드가 200(OK)인지 확인
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("신규 상품을 등록할 때 상품 타입은 필수값이다.")
    void createProductWithoutType() throws Exception {
        // given : Product 생성
        ProductCreateRequest request = ProductCreateRequest.builder()
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        // when // then
        // POST 메소드를 사용해 '/api/v1/products/new' 경로로 요청을 보냄
        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request)) // 요청 본문(body)을 JSON 형식으로 직렬화
                                .contentType(APPLICATION_JSON) // 요청의 Content-Type을 'application/json'으로 설정
                )
                .andDo(print()) // 요청과 응답에 대한 상세한 로그를 출력
                .andExpect(status().isBadRequest()) // HTTP 상태 코드가 400(BadRequest)인지 확인
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 타입은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("신규 상품을 등록할 때 상품 판매상태는 필수값이다.")
    void createProductWithoutSellingStatus() throws Exception {
        // given : Product 생성
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .name("아메리카노")
                .price(4000)
                .build();
        // when // then
        // POST 메소드를 사용해 '/api/v1/products/new' 경로로 요청을 보냄
        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request)) // 요청 본문(body)을 JSON 형식으로 직렬화
                                .contentType(APPLICATION_JSON) // 요청의 Content-Type을 'application/json'으로 설정
                )
                .andDo(print()) // 요청과 응답에 대한 상세한 로그를 출력
                .andExpect(status().isBadRequest()) // HTTP 상태 코드가 400(BadRequest)인지 확인
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 판매상태는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("신규 상품을 등록할 때 상품 이름은 필수값이다.")
    void createProductWithoutName() throws Exception {
        // given : Product 생성
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .price(4000)
                .build();
        // when // then
        // POST 메소드를 사용해 '/api/v1/products/new' 경로로 요청을 보냄
        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request)) // 요청 본문(body)을 JSON 형식으로 직렬화
                                .contentType(APPLICATION_JSON) // 요청의 Content-Type을 'application/json'으로 설정
                )
                .andDo(print()) // 요청과 응답에 대한 상세한 로그를 출력
                .andExpect(status().isBadRequest()) // HTTP 상태 코드가 400(BadRequest)인지 확인
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 이름은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("신규 상품을 등록할 때 상품 가격은 양수이다.")
    void createProductWithoutZeroPrice() throws Exception {
        // given : Product 생성
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(0)
                .build();
        // when // then
        // POST 메소드를 사용해 '/api/v1/products/new' 경로로 요청을 보냄
        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request)) // 요청 본문(body)을 JSON 형식으로 직렬화
                                .contentType(APPLICATION_JSON) // 요청의 Content-Type을 'application/json'으로 설정
                )
                .andDo(print()) // 요청과 응답에 대한 상세한 로그를 출력
                .andExpect(status().isBadRequest()) // HTTP\ 상태 코드가 400(BadRequest)인지 확인
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 가격은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("판매 상품을 조회한다.")
    void getSellingProducts() throws Exception {
        /**
         * 우리는 Product를 직접 생성하고 저장하는 테스트는 service, repository 테스트에서 이미 끝냈기 때문에
         * 컨트롤로 테스트에서는 반환값으로 isArray()로 받았는지만 테스트
         */
        // given
        List<ProductResponse> result = List.of();
        when(productService.getSellingProducts()).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/api/v1/products/selling")
//                                .queryParam("name", "이름") // 파라미터가 있을 시
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

}