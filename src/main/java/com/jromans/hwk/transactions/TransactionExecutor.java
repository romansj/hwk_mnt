package com.jromans.hwk.transactions;

import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.AccountRepository;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.accounts.db.BalanceRepository;
import com.jromans.hwk.shared.constants.MintosCurrency;
import com.jromans.hwk.shared.constants.RequestStatus;
import com.jromans.hwk.shared.constants.TransactionStatus;
import com.jromans.hwk.transactions.db.RequestRepository;
import com.jromans.hwk.transactions.db.Transaction;
import com.jromans.hwk.transactions.db.TransactionRepository;
import com.jromans.hwk.transactions.validation.TransactionValidator;
import com.jromans.hwk.transactions.validation.exceptions.InsufficientFundsException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import static java.math.BigDecimal.ZERO;

@Slf4j
@AllArgsConstructor
@Transactional
@Component
public class TransactionExecutor {
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceRepository balanceRepository;


    public void executeTransactions(Pair<Transaction, Transaction> transactions) throws InsufficientFundsException {
        executeTransaction(transactions.getLeft());
        executeTransaction(transactions.getRight());
    }

    private void executeTransaction(Transaction transaction) throws InsufficientFundsException {
        saveTransaction(transaction);
        updateBalance(transaction);
    }

    public void updateBalance(Transaction transaction) throws InsufficientFundsException {
        saveNewBalance(transaction);
        transactionCompleted(transaction);
    }

    private void saveNewBalance(Transaction transaction) throws InsufficientFundsException {
        var account = transaction.getAccount();
        var balance = getCurrentBalance(account, transaction.getCurrency());

        var balanceAmount = balance.getAmount();
        var newBalance = Balance.getNewBalance(balanceAmount, transaction);
        TransactionValidator.validateBalance(balanceAmount, transaction);

        balance.setAmount(newBalance);
        balanceRepository.save(balance);

        saveOrUpdateBalances(account, balance);
    }

    private void saveOrUpdateBalances(Account account, Balance balance) {
        var balances = account.getBalances();

        int indexOf = balances.indexOf(balance);
        if (indexOf != -1) {
            balances.remove(indexOf);
            balances.add(balance);

        } else {
            balances.add(balance);
        }

        account.setBalances(balances);
        accountRepository.save(account);
    }


    /**
     * Gets current balance in account for specified currency.
     * Creates empty balance if account doesn't have currency yet.
     */
    public Balance getCurrentBalance(Account account, MintosCurrency currency) {
        String accountNumber = account.getAccountNumber();

        return balanceRepository
                .findByAccount_AccountNumberIgnoreCaseAndCurrency(accountNumber, currency)
                .orElseGet(() -> {
                    log.warn("Balance not found for {}, currency {}", accountNumber, currency);
                    return new Balance(account, ZERO, currency);
                });
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    private void transactionFailed(Transaction transaction, InsufficientFundsException e) {
        finalizeTransaction(transaction, RequestStatus.REJECTED, TransactionStatus.FAILED);
    }

    public void transactionCompleted(Transaction transaction) {
        finalizeTransaction(transaction, RequestStatus.COMPLETED, TransactionStatus.COMPLETED);
    }

    private void finalizeTransaction(Transaction transaction, RequestStatus requestStatus, TransactionStatus transactionStatus) {
        var request = transaction.getRequest();
        request.setStatus(requestStatus);
        requestRepository.save(request);

        transaction.setTransactionStatus(transactionStatus);
        saveTransaction(transaction);
    }

}
