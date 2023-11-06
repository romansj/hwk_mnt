package com.jromans.hwk;

import com.jromans.hwk.configuration.exceptions.MyConstraintException;
import com.jromans.hwk.transactions.SendRequestDTO;
import com.jromans.hwk.transactions.validation.TransactionRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionRequestValidatorTest {

    @Test
    void correctPaymentIsValidated() {
        var requestDTO = DTOData.getRequest();
        assertDoesNotThrow(() -> TransactionRequestValidator.validate(requestDTO));
    }

    @ParameterizedTest()
    @MethodSource("getBadPayments")
    void incorrectPaymentsCauseException(SendRequestDTO payment) {
        assertThrows(MyConstraintException.class, () -> TransactionRequestValidator.validate(payment));
    }

    private static Stream<Arguments> getBadPayments() {
        var requestDTO = DTOData.getRequest();

        return Stream.of(
                Arguments.of(requestDTO.withRecipientName("")), // no destination name
                Arguments.of(requestDTO.withAccountDst("")), // no destination account
                Arguments.of(requestDTO.withAmount(BigDecimal.valueOf(0.00))), // payment of nothing
                Arguments.of(requestDTO.withAmount(BigDecimal.valueOf(-1.00))), // negative amount
                Arguments.of(requestDTO.withAmount(BigDecimal.valueOf(10000001.00))) // payment amount too much for mere mortal
        );
    }
}