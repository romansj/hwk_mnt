package com.jromans.hwk.transactions;

import com.jromans.hwk.transactions.validation.exceptions.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.naming.ServiceUnavailableException;

import static com.jromans.hwk.shared.constants.MintosCurrency.DKK;
import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionServiceTestDb extends FullTransactionTest {

    private static final String ACC_JOHN = "LV10MINT0012345678001";
    private static final String ACC_JANE = "LV10MINT0012345678002";

    static final int OK = HttpStatus.OK.value();
    static final int CREATED = HttpStatus.CREATED.value();


    @Test
    void validTransactionPostRequestAccepted() {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("Jane", ACC_JANE, 1000, EUR);


        RestRequest.sendMoney(ACC_JOHN, ACC_JANE, 1000, EUR);


        checkBalance(1000, ACC_JOHN, EUR);
        checkBalance(2000, ACC_JANE, EUR);
    }


    @Test
    void validTransactionShowsInTransactionList() throws MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, ServiceUnavailableException, AccountNotFoundException {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("Jane", ACC_JANE, 3000, EUR);


        sendMoney(ACC_JOHN, ACC_JANE, 200, EUR);


        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("accountNumber", ACC_JOHN)
                .get("/transactions/list")
                .then()
                .log().body()
                .statusCode(OK)
                .body("content", hasSize(1));
    }

    @Test
    void validTransactionMoneyIsTransferred() throws MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, ServiceUnavailableException, AccountNotFoundException {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("Jane", ACC_JANE, 3000, EUR);

        sendMoney(ACC_JOHN, ACC_JANE, 200, EUR);

        assertThat(valueOf(2000 - 200)).isEqualByComparingTo(getBalance(ACC_JOHN, EUR));
        assertThat(valueOf(3000 + 200)).isEqualByComparingTo(getBalance(ACC_JANE, EUR));
    }

    @Test
    void validTransactionCurrencyIsConverted() throws ServiceUnavailableException, MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, AccountNotFoundException {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("Jane", ACC_JANE, 3000, DKK);

        var request = sendMoney(ACC_JOHN, ACC_JANE, 746, DKK);
        var rate = conversionService.getRate(DKK, EUR);
        var eurAmount = request.getAmount().multiply(rate);

        checkBalance(valueOf(2000).subtract(eurAmount), ACC_JOHN, EUR);
        checkBalance(3000 + 746, ACC_JANE, DKK);
    }


    @Test
    void transactionWithIncompatibleCurrenciesNotAllowed() {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("Jane", ACC_JANE, 3000, DKK);

        assertThrows(IncompatibleCurrenciesException.class, () -> {
            sendMoney(ACC_JOHN, ACC_JANE, 746, EUR);
        });

        checkBalance(2000, ACC_JOHN, EUR);
        checkBalance(3000, ACC_JANE, DKK);
    }

    @Test
    void transactionOfFullPrimaryBalanceAccepted() throws ServiceUnavailableException, MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, AccountNotFoundException {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("John", ACC_JOHN, 500, DKK);
        createWithBalance("Jane", ACC_JANE, 1000, EUR);

        sendMoney(ACC_JOHN, ACC_JANE, 2000, EUR);

        checkBalance(0, ACC_JOHN, EUR);
        checkBalance(500, ACC_JOHN, DKK);
        checkBalance(3000, ACC_JANE, EUR);
    }

    @Test
    void transactionOfFullSecondaryBalanceAccepted() throws ServiceUnavailableException, MoneyNotGoingAnywhereException, NotConvertedException, InsufficientFundsException, IncompatibleCurrenciesException, AccountNotFoundException {
        createWithBalance("John", ACC_JOHN, 2000, EUR);
        createWithBalance("John", ACC_JOHN, 500, DKK);
        createWithBalance("Jane", ACC_JANE, 2500, DKK);

        sendMoney(ACC_JOHN, ACC_JANE, 500, DKK);

        checkBalance(2000, ACC_JOHN, EUR);
        checkBalance(0, ACC_JOHN, DKK);
        checkBalance(3000, ACC_JANE, DKK);
    }


}