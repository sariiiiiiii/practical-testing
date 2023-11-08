package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ProductTypeTest {

    @Test
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    void containsStockType() {
        // given
        ProductType givenType = ProductType.HANDMADE;

        // when
        boolean result = ProductType.containsStockType(givenType);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    void containsStockType2() {
        // given
        ProductType givenType = ProductType.BAKERY;

        // when
        boolean result = ProductType.containsStockType(givenType);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    void containsStockType3() {

        /**
         * 모든 ProductType에 대해서 다 테스트를 해보고 싶은 경우를 예제
         * 보면 어느 ProductType에 대해서 result와 assertThat을 다 쫓아가야 한다
         */

        // given
        ProductType giveType1 = ProductType.HANDMADE;
        ProductType giveType2 = ProductType.BOTTLE;
        ProductType giveType3 = ProductType.BAKERY;

        // when
        boolean result1 = ProductType.containsStockType(giveType1);
        boolean result2 = ProductType.containsStockType(giveType2);
        boolean result3 = ProductType.containsStockType(giveType3);

        // then
        assertThat(result1).isFalse();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @CsvSource({"HANDMADE,false", "BOTTLE,true", "BAKERY,true"})
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @ParameterizedTest
    void containsStockType4(ProductType productType, boolean expected) {
        /**
         * @CsvSource({}) 로 넣어준 값이 메소드의 파라미터로 들어오게 된다
         */
        // when
        boolean result = ProductType.containsStockType(productType);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @MethodSource("provideProductTypesForCheckingStockType")
    @ParameterizedTest
    void containsStockType5(ProductType productType, boolean expected) {
        /**
         * @MethodSource 어노테이션에 값을 체크할 메소드 이름을 넣고 Stream<Argument> 반환을 해준다
         */
        // when
        boolean result = ProductType.containsStockType(productType);

        // then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideProductTypesForCheckingStockType() {
        return Stream.of(
                Arguments.of(ProductType.HANDMADE, false),
                Arguments.of(ProductType.BOTTLE, true),
                Arguments.of(ProductType.BAKERY, true)
        );
    }

}