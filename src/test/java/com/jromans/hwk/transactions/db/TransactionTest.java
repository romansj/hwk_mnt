package com.jromans.hwk.transactions.db;

import com.jromans.hwk.Data;
import org.junit.jupiter.api.Test;

import static com.jromans.hwk.shared.constants.TransactionStatus.PENDING;
import static com.jromans.hwk.shared.constants.TransactionType.CREDIT;
import static com.jromans.hwk.shared.constants.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.assertSame;

class TransactionTest {

    @Test
    void transactionCreatedFromRequestAccountType() {
        var request = Data.getRequest();
        var account = Data.getAccountWithCustomer();
        var transactionDebit = Transaction.getTransaction(request, account, DEBIT);

        assertSame(PENDING, transactionDebit.getTransactionStatus());
        assertSame(request.getCurrency(), transactionDebit.getCurrency());
        assertSame(request.getAmount(), transactionDebit.getAmount());
    }

    @Test
    void debitTakesFromSourceAccount_ButCreditAddsToDestinationAccount() {
        var request = Data.getRequest().withAccountSrc("SRC").withAccountDst("DST");
        var accountSrc = Data.getAccountWithCustomer().withAccountNumber("SRC");
        var accountDst = Data.getAccountWithCustomer().withAccountNumber("DST");

        var transactionDebit = Transaction.getTransaction(request, accountSrc, DEBIT);
        var transactionCredit = Transaction.getTransaction(request, accountDst, CREDIT);

        assertSame(request.getAccountSrc(), transactionDebit.getAccountNumber());
        assertSame(request.getAccountDst(), transactionCredit.getAccountNumber());
    }
}