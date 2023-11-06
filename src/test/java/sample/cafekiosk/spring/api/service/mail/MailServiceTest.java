package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock // MailSendClient를 목킹하여 실제 이메일은 전송 X
    private MailSendClient mailSendClient;
    @Mock // Repository를 목킹하여 실제 데이터베이스 작업은 발생 X
    private MailSendHistoryRepository mailSendHistoryRepository;
    @InjectMocks // 위에서 목킹한 객체들을 mailService에 주입. 이 서비스는 의존성에 대해 Mock을 사용
    private MailService mailService;
    @Spy // 실제 MailSendClient에 대한 스파이, 실제 객체이지만 일부 메서드에 대해서는 스터빙
    private MailSendClient spyMailSendClient;

    /**
     * 참고: 실제로는 @Spy와 @Mock을 같은 필드 타입에 사용하지 마세요, 상호 간섭이 일어날 수 있습니다.
     */

    @Test
    @DisplayName("메일 전송 테스트 - 어노테이션")
    void sendMailByMockAnnotation() {
        // given : sendEmail 메서드를 스터빙하여 호출될 때마다 true를 반환하도록 함
        when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        // when : mailService를 통해 이메일을 보내려고 시도
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue(); // 결과가 true이고 이메일이 '보내진 것'을 확인
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class)); // mailSendHistoryRepository.save()가 몇번 호출이 되었는지 검증
    }

    @Test
    @DisplayName("메일 전송 테스트 - 객체")
    void sendMailMockObject() {
        // given : 이 테스트에서는 어노테이션 대신 수동으로 목을 생성
        MailSendClient mailSendClient = mock(MailSendClient.class);
        MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class);
        MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);

        when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

    @Test
    @DisplayName("메일 전송 테스트 - spy")
    void sendMailSpy() {

        /**
         * spy 객체는 실제 객체를 활용하는 것이기 때문에 Mockito.when()으로는 할 수가 없고 Mockito.doReturn()을 이용
         * spyMailSendClient.sendEmail() 만 stubbing 된 것이고 나머지 메소드는 실제 객체 기능은 제대로 동작을 함
         *
         * 나머지 기능들은 실제 기능을 사용하고 싶고 일부 기능만 stubbing 하고 싶을 때 @Spy 사용
         */

        // given : 스파이 객체에 메서드를 스터빙하기 위해 doReturn()을 사용
        doReturn(true)
                .when(spyMailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

    @Test
    @DisplayName("메일 전송 테스트 - BDDMockito")
    void sendMailBDDMockito() {

        /**
         * give 절에 Mockito.when()이 들어가있다 뭔가 어색하다 (기능은 given인데 문법은 when이네 ?)
         * 그럴 때는 BDDMockito.given() 을 사용하면 자연스럽다
         * 이름만 다른거지 모든 기능은 동일하다
         */

        // given : BDD 스타일의 given-when-then을 맞추기 위해 BDDMockito를 사용하여 스터빙
        BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .willReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

    }
    
}