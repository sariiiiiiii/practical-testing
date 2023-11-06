package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.mail.MailService;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

    private final OrderRepository orderRepository;

    private final MailService mailService;

    /**
     * 주문 통계 서비스
     */

    public boolean sendOrderStatisticsMail(LocalDate orderDate, String email) {

        /**
         * mail 이라든지 시간소요가 있는 메소드에서는 트랜잭션을 안걸어주는게 낫다
         * 예를 들어, findOrdersBy() 같이 repository에서 따로 트랜잭션이 있기 때문에 해당 repository에서 트랜잭션이 작동한다
         */

        // 해당 일자에 결제완료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                // 등록시간을 기준으로 하루치 주문
                orderDate.atStartOfDay(), // ex) 18일이라면 18일 0시
                orderDate.plusDays(1).atStartOfDay(), // ex) 18일이라면 +1 해서 19일 0시,
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일 전송
        boolean result = mailService.sendMail("no-reply@cafekiosk.com",
                email,
                String.format("[매출통계] %s", orderDate),
                String.format("총 매출 합계는 %s원입니다.", totalAmount)
        );

        if (!result) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;

    }

}
