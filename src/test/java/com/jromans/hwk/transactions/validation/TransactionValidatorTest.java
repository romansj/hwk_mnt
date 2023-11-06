package com.jromans.hwk.transactions.validation;

import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.transactions.db.Request;
import com.jromans.hwk.transactions.db.Transaction;
import com.jromans.hwk.transactions.validation.exceptions.IncompatibleCurrenciesException;
import com.jromans.hwk.transactions.validation.exceptions.InsufficientFundsException;
import com.jromans.hwk.transactions.validation.exceptions.MoneyNotGoingAnywhereException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jromans.hwk.Data.*;
import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static com.jromans.hwk.shared.constants.MintosCurrency.USD;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {

    @Test
    void validTransactionDoesNotThrow() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001");
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002");

        Request request = getRequest(accountSrc, accountDst);

        assertDoesNotThrow(() -> {
            TransactionValidator.validate(request, accountSrc, accountDst);
        });
    }


    @Test
    void invalidTransactionThrows() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001");
        Account accountDst = getAccountWithBalances("LV10MINT0012345678001");

        Request request = getRequest(accountSrc, accountDst);

        assertThrows(MoneyNotGoingAnywhereException.class, () -> {
            TransactionValidator.validate(request, accountSrc, accountDst);
        });
    }

    @Test
    void emptyTransactionThrows() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001");
        Account accountDst = getAccountWithBalances("LV10MINT0012345678001");

        Request request = getRequest(accountSrc, accountDst, 0);

        assertThrows(MoneyNotGoingAnywhereException.class, () -> {
            TransactionValidator.validate(request, accountSrc, accountDst);
        });
    }

    @Test
    void validateAccountHasRequestedCurrency() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withBalances(List.of(
                new Balance(EUR, valueOf(200)),
                new Balance(USD, valueOf(200))
        ));
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002");
        Request request = getRequest(accountSrc, accountDst).withCurrency(EUR).withAmount(valueOf(100));

        assertTrue(TransactionValidator.validateAccountHasRequestedCurrency(accountSrc, request));
    }

    @Test
    void givenEnoughCashInRequestedCurrencyDoesNotThrow() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withBalances(List.of(
                new Balance(EUR, valueOf(200)),
                new Balance(USD, valueOf(200))
        ));
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002");
        Request request = getRequest(accountSrc, accountDst).withCurrency(EUR).withAmount(valueOf(200));

        assertTrue(TransactionValidator.validateAccountHasRequestedCurrency(accountSrc, request));
    }


    @Test
    void validateBalanceAndTransaction() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withBalances(List.of(
                new Balance(EUR, valueOf(200))
        ));
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002");
        Transaction transaction = getTransaction(accountSrc, accountDst, 200.01);

        assertThrows(InsufficientFundsException.class, () -> {
            TransactionValidator.validateBalanceAndTransaction(accountSrc, transaction);
        });
    }

    @Test
    void emptyBalanceAttemptingRequestThrows() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withBalances(List.of(
                new Balance(EUR, valueOf(0))
        ));
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002");
        Transaction transaction = getTransaction(accountSrc, accountDst, 200.01);

        assertThrows(InsufficientFundsException.class, () -> {
            TransactionValidator.validateBalanceAndTransaction(accountSrc, transaction);
        });
    }


    @Test
    void validateDestinationAndTransactionCurrencyMatchDoesNotThrow() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withAccountCurrency(EUR);
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002").withAccountCurrency(EUR);
        Request request = getRequest(accountSrc, accountDst).withCurrency(EUR);

        assertDoesNotThrow(() -> {
            TransactionValidator.validateDestinationAndTransactionCurrencyMatch(accountDst, request);
        });
    }

    @Test
    void validateDestinationAndTransactionCurrencyMatchThrows() {
        Account accountSrc = getAccountWithBalances("LV10MINT0012345678001").withAccountCurrency(EUR);
        Account accountDst = getAccountWithBalances("LV10MINT0012345678002").withAccountCurrency(USD);
        Request request = getRequest(accountSrc, accountDst).withCurrency(EUR);

        assertThrows(IncompatibleCurrenciesException.class, () -> {
            TransactionValidator.validateDestinationAndTransactionCurrencyMatch(accountDst, request);
        });
    }
}