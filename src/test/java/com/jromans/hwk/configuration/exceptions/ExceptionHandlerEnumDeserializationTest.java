package com.jromans.hwk.configuration.exceptions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.jromans.hwk.configuration.exceptions.ExceptionHandlerAdvice.ENUM_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ExceptionHandlerEnumDeserializationTest {
    @ParameterizedTest
    @MethodSource("getErrorMessages")
    void returnsExpectedRegexMatch(String errorMessage, boolean expected) {
        var matches = ENUM_MSG.matcher(errorMessage).matches();
        assertEquals(expected, matches);
    }

    private static Stream<Arguments> getErrorMessages() {
        return Stream.of(
                Arguments.of("JSON parse error: Cannot deserialize value of type `com.jromans.hwk.constants.MintosCurrency` from String \"UNKNOWN\": not one of the values accepted for Enum class: [AUD, EUR, DKK, GBP, CAD, UAH, INR, XAG, KRW, USD, XAU]", true), // text before wanted pattern -- OK
                Arguments.of("not one of the values accepted for Enum class: [AUD, EUR, DKK, GBP, CAD, UAH, INR, XAG, KRW, USD, XAU]", true), // multiple -- OK
                Arguments.of("not one of the values accepted for Enum class: [AUD]", true), // just one -- OK
                Arguments.of("not one of the values accepted for Enum class: []", false), // empty -- NOK
                Arguments.of("not of the values accepted for Enum class: []", false) // not matching erorr pattern -- NOK
        );
    }

}