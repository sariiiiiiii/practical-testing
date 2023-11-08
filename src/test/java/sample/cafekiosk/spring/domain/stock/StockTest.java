package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    /**
     * static 한 변수를 만들어 전역으로 사용하게 되면 이 변수를 바꾸다 보니까 다른 테스트에서 어떻게 작동할지는 모른다
     * 예를들어 재고가 증가되는 테스트를 진행한다고 했을 때 그러면 그 다음에 수행되면 테스트에서는 어떻게 될 지 모른다
     * 이말이 즉슨, 테스트간 순서에 따라 테스트의 성공과 실패가 나누어진다
     * 순서랑은 무관하게 각각 독립적으로 테스트가 진행되어야 한다
     * 공유자원은 사용하지 말자
     */
    private static final Stock stock = Stock.create("001", 1);

    @Test
    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
    void isQuantityLessThan() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        // when
        // 경곗값 테스트로 아슬아슬하게 숫자 1차이로 테스트 해보기
        boolean result = stock.isQuantityLessThan(quantity);

        //then
        assertThat(result).isTrue();

    }

    @Test
    @DisplayName("재고가 주어진 개수만큼 차감할 수 있다.")
    void deductQuantity() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

        // when
        stock.deductQuantity(quantity);

        //then
//        assertThat(stock.getQuantity()).isEqualTo(0);
        assertThat(stock.getQuantity()).isZero(); // 0을 확인해보고 싶을 때는 isEqualTo(0)보다는 isZero()로 해보기

    }

    @Test
    @DisplayName("재고보다 많은 수량으로 차감 시도하는 경우 예외가 발생한다.")
    void deductQuantity2() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        //when // then
        assertThatThrownBy(() -> stock.deductQuantity(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");

    }

}