package sample.cafekiosk.spring.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

//@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    /**
     * RestDoc 을 하기 위한 상위 클래스
     */

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper(); // 스프링을 이용한 test가 아니기 때문에 생성자 이용

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController()) // 테스트할 controller 주입
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
    }

    // 모든 컨트롤러를 이 클래스에서 다 담기는 좀 그러니 추상 메소드 생성
    protected abstract Object initController();

//    @BeforeEach
//    void setUp(WebApplicationContext webApplicationContext,
//               RestDocumentationContextProvider restDocumentationContextProvider
//    ) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider))
//                .build();
//    }

}
