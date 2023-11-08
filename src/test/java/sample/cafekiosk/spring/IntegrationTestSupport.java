package sample.cafekiosk.spring;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.client.mail.MailSendClient;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    /**
     * 테스트를 실행시키는 시간도 비용과 자원인데 테스트마다 환경이 다르고 그 환경이 다를 떄 springboot 서버가 다시 띄어지기 때문에
     * 최대한 환경을 맞춰주어서 시간과 비용을 아끼자
     */

    /**
     * @ActiveProfiles("test")
     * @SpringBootTest
     * 위 2개의 어노테이션이 있는 테스트 클래스에 IntegrationTestSupport 클래스를 상속받게 시켰는데도 서버가 다시 띄어지는 경우가 있다
     * 그 경우를 보면 @MockBean 으로 의존성 주입받는 클래스인데 @MockBean 어노테이션이 붙은 클래스는 원래 객체가 아닌 mock 객체를 주입을 시키는 것이기 때문에 다시 서버를 띄어야 한다
     * 이럴 때는 2가지의 경우로 해결할 수 있다
     * 하나는 @MockBean이 붙은 클래스를 들고와서 추상클래스에서 주입을 받는 경우가 있고
     * 두번째는 테스트 환경을 나눌 수 있을 거 같다
     *  - 순수 MockBean이 없는 TestSupport 클래스를 하나 구축을 해서 연결하고 Mocking 처리를 하는 애들을 모아서 한번에 하는 TestSupport 만들 수 있다
     *  - 프로젝트 환경에 맞춰서 하자
     */

    // 첫 번째 경우 : @MockBean 이 붙은 클래스를 들고와서 접근제어자를 protected로 해준다
    @MockBean
    protected MailSendClient mailSendClient;

}
