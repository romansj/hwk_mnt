package com.jromans.hwk.transactions.validation;

import com.jromans.hwk.accounts.IAccount;
import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.transactions.TransactionData;
import com.jromans.hwk.transactions.db.Request;
import com.jromans.hwk.transactions.validation.exceptions.IncompatibleCurrenciesException;
import com.jromans.hwk.transactions.validation.exceptions.InsufficientFundsException;
import com.jromans.hwk.transactions.validation.exceptions.MoneyNotGoingAnywhereException;
import com.jromans.hwk.transactions.validation.exceptions.NotConvertedException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.jromans.hwk.shared.constants.TransactionCategory.FX;
import static java.math.BigDecimal.ZERO;

@Slf4j
public class TransactionValidator {

    public static void validate(Request request, IAccount accountOrigin, IAccount accountDestination) throws InsufficientFundsException, NotConvertedException, MoneyNotGoingAnywhereException {


        log.info("Validating request {}, accSrc {}, accDst {}", request, accountOrigin, accountDestination);


        validateAmount(request);
        validateSrcAndDstNotTheSameOrIsFx(accountOrigin, accountDestination, request);
        validateBalanceAndTransaction(accountOrigin, request);
        if (!validateAccountHasRequestedCurrency(accountOrigin, request)) throw new NotConvertedException();

        log.info("Validations passed");
    }


    public static boolean validateAccountHasRequestedCurrency(IAccount accountOrigin, Request request) {
        var currency = request.getCurrency();
        if (accountOrigin.getAccountCurrency() == currency) {
            log.info("Account currency the same as request currency");
            return true;
        }

        var balanceInRequestCurrency = accountOrigin.getBalanceInCurrency(currency);
        var transferAmount = request.getAmount();

        log.info("balanceInRequestCurrency {}, transferAmount {}", balanceInRequestCurrency, transferAmount);

        if (balanceInRequestCurrency.compareTo(transferAmount) < 0) {
            log.warn("Currency not converted, request transfer {} {}, account has {} {}", currency, transferAmount, currency, balanceInRequestCurrency);
            // should have as much as needed or more
            return false;
        }

        return true;
    }


    public static void validateBalanceAndTransaction(IAccount account, TransactionData transaction) throws InsufficientFundsException {
        var currentBalance = account.getBalanceInCurrency(transaction.getCurrency());

        log.info("validateBalanceAndTransaction acc {}, currentBalance {}, tx {}", account.getAccountNumber(), currentBalance, transaction);

        if (currentBalance.compareTo(ZERO) <= 0) {
            log.error("insufficient currentBalance {}", currentBalance);
            // no funds to transfer
            throw new InsufficientFundsException();
        }

        validateBalance(currentBalance, transaction);
    }

    public static void validateBalance(BigDecimal currentBalance, TransactionData transaction) throws InsufficientFundsException {
        var newBalance = Balance.getNewBalance(currentBalance, transaction);
        if (newBalance.compareTo(ZERO) < 0) {
            log.error("insufficient newBalance {}", newBalance);
            // not enough funds to transfer
            throw new InsufficientFundsException();
        }
    }

    public static void validateDestinationAndTransactionCurrencyMatch(Account accountDestination, Request request) throws IncompatibleCurrenciesException {
        var currencyDestination = accountDestination.getAccountCurrency();
        var transactionCurrency = request.getCurrency();

        if (currencyDestination != transactionCurrency) {
            log.error("Transaction[{}] and destination[{}] currencies don't match", transactionCurrency, currencyDestination);
            // error, wrong currency
            throw new IncompatibleCurrenciesException();
        }
    }

    private static void validateAmount(Request request) throws MoneyNotGoingAnywhereException {
        if (request.getAmount().compareTo(ZERO) <= 0) {
            log.error("Request is not transfering any money");
            throw new MoneyNotGoingAnywhereException();
        }
    }

    private static void validateSrcAndDstNotTheSameOrIsFx(IAccount accountOrigin, IAccount accountDestination, Request request) throws MoneyNotGoingAnywhereException {
        var srcAndDstSame = accountOrigin.getAccountNumber().equalsIgnoreCase(accountDestination.getAccountNumber());
        if (srcAndDstSame && request.getCategory() != FX) {
            log.error("src & dst same, request {}", request);
            throw new MoneyNotGoingAnywhereException();
        }
    }
}
