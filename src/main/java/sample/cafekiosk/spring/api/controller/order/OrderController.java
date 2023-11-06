package sample.cafekiosk.spring.api.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.spring.api.ApiResponse;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.controller.order.response.OrderResponse;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders/new")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {

        /**
         * 컨트롤러는 서비스 레이어의 메소드를 호출할 수 있지만, 서비스 레이어가 컨트롤러의 요청 객체를 직접적으로 참조하면 레이어 간 결합도가 높아집니다.
         * 서비스 레이어의 재사용성과 유지보수성을 보장하기 위해, 컨트롤러에서 서비스 레이어로 데이터를 전달할 때는
         * 서비스 레이어 전용 DTO를 사용하는 것이 좋습니다.
         */

        LocalDateTime registeredDateTime = LocalDateTime.now();
        return ApiResponse.ok(orderService.createOrder(orderCreateRequest.toServiceRequest(), registeredDateTime));
    }

}
