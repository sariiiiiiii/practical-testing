package sample.cafekiosk.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.beverages.Americano;
import sample.cafekiosk.unit.beverages.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("우리 카페 키오스크는")
class CafekioskTest {

    @Test
    void add_manual_test() {
        /**
         * - 수동 테스트 -
         * Cafekiosk 객체를 생성하고 그 객체에 음료를 넣어서 콘솔로 출력하는 테스트 코드를 작성하였다
         * 결국 이 테스트 코드의 마지막 확인하는 주체는 사람이 콘솔에 잘 적혔나 확인하는 것이다
         *
         * 그리고 다른사람이 봤을 때 어떤 상황에서의 코드인지 확인하기가 어렵다
         */

        Cafekiosk cafekiosk = new Cafekiosk();
        cafekiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수 : " + cafekiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료 : " + cafekiosk.getBeverages().get(0).getName());
    }

    @Test
//    @DisplayName("음료 1개 추가 테스트")
//    @DisplayName("음료 1개를 추가할 수 있다.")
    @DisplayName("음료 1개를 추가하면 주문 목록에 담긴다.")
//    void 음료_1개_추가_테스트() {
    void add() {

        Cafekiosk cafekiosk = new Cafekiosk();
        cafekiosk.add(new Americano());

        assertThat(cafekiosk.getBeverages().size()).isEqualTo(1);
        assertThat(cafekiosk.getBeverages()).hasSize(1);
        assertThat(cafekiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
        assertThat(cafekiosk.getBeverages().get(0).getPrice()).isEqualTo(4000);

    }

    @Test
    void addSeveralBeverages() {

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();
        cafekiosk.add(americano, 2);

        assertThat(cafekiosk.getBeverages()).hasSize(2);
        assertThat(cafekiosk.getBeverages().get(0)).isEqualTo(americano);
        assertThat(cafekiosk.getBeverages().get(0)).isEqualTo(americano);

    }

    @Test
    void addZeroBeverages() {

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();

        assertThatThrownBy(() -> cafekiosk.add(americano, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문하실 수 있습니다.");

    }

    @Test
    void remove() {

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();

        cafekiosk.add(americano);
        assertThat(cafekiosk.getBeverages()).hasSize(1);

        cafekiosk.remove(americano);
        assertThat(cafekiosk.getBeverages()).isEmpty();

    }

    @Test
    void clear() {

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafekiosk.add(americano);
        cafekiosk.add(latte);
        assertThat(cafekiosk.getBeverages()).hasSize(2);

        cafekiosk.clear();
        assertThat(cafekiosk.getBeverages()).isEmpty();

    }

    @Test
    @DisplayName("주문 목록에 담긴 상품들의 총 금액을 계산할 수 있다.")
    void calculateTotalPrice() {
        // given
        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafekiosk.add(americano);
        cafekiosk.add(latte);

        // when
        int totalPrice = cafekiosk.calculateTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(8500);
    }

    @Test
    void test123135345() {

    }

    @Test
    void createOrder() {

        /**
         * 해당 테스트는 주문시간을 체크할 수 있는 테스트이다
         * 그런데, 테스트를 하는 현재 시간이 주문 시간 범위에 벗어나게 되면 예외를 뱉게 되어 있다
         * 이런 경우는 어떻게 해야 할까?
         */

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();

        cafekiosk.add(americano);

        Order order = cafekiosk.createOrder();

        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");

    }

    @Test
    void createOrderWithCurrentTime() {

        /**
         * createOrder() 메소드에 현재시간이 아닌 객체 생성 시 파라미터로 시간을 정해서 넣어주는 메소드를 만들어주자
         * 그리고 주문 운영시간이 10 ~ 22시이기 때문에 경계값인 10시로 테스트를 해주자
         */

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();

        cafekiosk.add(americano);

        Order order = cafekiosk.createOrder(LocalDateTime.of(2023, 1, 17, 10, 0));

        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");

    }

    @Test
    void createOrderOutsideOpenTime() {

        /**
         * createOrder() 메소드에 현재시간이 아닌 객체 생성 시 파라미터로 시간을 정해서 넣어주는 메소드를 만들어주자
         * 그리고 예외 테스트를 해보기 위해 오픈 시간 경곗값인 10시에서 1분 뺴준 9시 59분으로 테스트
         */

        Cafekiosk cafekiosk = new Cafekiosk();
        Americano americano = new Americano();

        cafekiosk.add(americano);

        assertThatThrownBy(() -> cafekiosk.createOrder(LocalDateTime.of(2023, 1, 17, 9, 59)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의하세요.");

    }

}








