package com.jromans.hwk;

import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.accounts.db.Balance;
import com.jromans.hwk.customers.db.Customer;
import com.jromans.hwk.shared.constants.*;
import com.jromans.hwk.transactions.db.Request;
import com.jromans.hwk.transactions.db.Transaction;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static com.jromans.hwk.shared.constants.MintosCurrency.USD;
import static com.jromans.hwk.shared.constants.PaymentType.EEA;
import static com.jromans.hwk.shared.constants.TransactionCategory.TRANSFER;
import static java.math.BigDecimal.valueOf;

public class Data {

    @NotNull
    public static Account getAccountWithBalances(String accountNumber) {
        return getAccountWithCustomer().withAccountNumber(accountNumber);
    }

    @NotNull
    public static Account getAccount(String accountNumber) {
        return new Account(accountNumber, List.of(), EUR, null);
    }

    @NotNull
    public static Request getRequest(Account accountSrc, Account accountDst, double amount) {
        return getRequest(accountSrc.getAccountNumber(), accountDst.getAccountNumber(), amount);
    }

    public static Request getRequest(String accountSrc, String accountDst, double amount) {
        return getRequest()
                .withAccountSrc(accountSrc)
                .withAccountDst(accountDst)
                .withAmount(valueOf(amount));
    }

    @NotNull
    public static Request getRequest(Account accountSrc, Account accountDst) {
        return getRequest(accountSrc, accountDst, 10);
    }

    @NotNull
    public static Transaction getTransaction(Account accountSrc, Account accountDst, double amount) {
        Request request = getRequest(accountSrc, accountDst, amount).withCurrency(EUR);
        return Transaction.getTransaction(request, accountSrc, TransactionType.DEBIT);
    }

    @NotNull
    public static Account getAccountWithCustomer() {
        Customer customer = getCustomer();
        return getAccountWithCustomer(customer);
    }

    @NotNull
    private static Account getAccountWithCustomer(Customer customer) {
        Account account = getAccountX(customer);

        List<Balance> balances = getBalances(account);
        account.setBalances(balances);

        customer.setAccounts(List.of(account));

        return account;
    }

    @NotNull
    private static List<Balance> getBalances(Account account) {
        Balance balanceEur = new Balance(account, valueOf(500), EUR);
        Balance balanceUsd = new Balance(account, valueOf(500), USD);
        return List.of(balanceEur, balanceUsd);
    }

    @NotNull
    private static Account getAccountX(Customer customer) {
        return new Account("ACC", List.of(), EUR, customer);
    }


    @NotNull
    public static Customer getCustomer(String customerName) {
        return new Customer().withName(customerName);
    }

    @NotNull
    public static Customer getCustomer() {
        return getCustomer("Customer Name");
    }

    public static Balance getBalance(Account account, double amount, MintosCurrency currency) {
        return new Balance(account, valueOf(amount), currency);
    }

    @NotNull
    public static Request getRequest() {
        return Request.builder()
                .requestId(1L)
                .accountDst("DST")
                .accountSrc("SRC")
                .bankDst("BANK_DST")
                .countryDst(Country.LATVIA)
                .amount(valueOf(100))
                .paymentDate(ZonedDateTime.now())
                .paymentType(EEA)
                .swift("MINTLV22")
                .dateTime(LocalDateTime.now())
                .category(TRANSFER)
                .currency(EUR)
                .status(RequestStatus.PENDING)
                .comissionCoverage(ComissionCoverage.DIVIDED)
                .description("Some trustworthy payment")
                .recipientName("RECIPIENT")
                .build();
    }

}
