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

    @Mock
    private MailSendClient mailSendClient;
    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;
    @InjectMocks
    private MailService mailService;
    @Spy
    private MailSendClient spyMailSendClient;

    /**
     * 현재 MailServiceTest 테스트 코드에서는 @Mock과 @Spy를 MailSendClient에 사용되고 있으며, 이 둘이 서로 간섭을 할 수 있어서 테스트에 실패 할 수 있는데
     * 강의로 공부중이기 때문에 참고할려고 @Spy, @Mock을 같이 쓴거지 실제에서는 같이 사용하지 말자
     */

    @Test
    @DisplayName("메일 전송 테스트 - 어노테이션")
    void sendMailByMockAnnotation() {
        // given
        when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

    @Test
    @DisplayName("메일 전송 테스트 - 객체")
    void sendMailMockObject() {
        // given
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

        // given
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

        // given
        BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .willReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

    }
    
}