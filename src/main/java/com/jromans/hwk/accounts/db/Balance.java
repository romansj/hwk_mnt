package com.jromans.hwk.accounts.db;

import com.jromans.hwk.shared.constants.MintosCurrency;
import com.jromans.hwk.transactions.TransactionData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "balance", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_account_number", "currency"})})
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_account_number")
    private Account account;

    @Enumerated(STRING)
    @Column(name = "currency")
    private MintosCurrency currency;

    @Column(name = "amount")
    private BigDecimal amount;

    public Balance(Account account, BigDecimal amount, MintosCurrency currency) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
    }

    public Balance(MintosCurrency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public static BigDecimal getNewBalance(BigDecimal balance, TransactionData transaction) {
        var amount = transaction.getAmount();

        return switch (transaction.getTransactionType()) {
            case CREDIT -> balance.add(amount);
            case DEBIT -> balance.subtract(amount);
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("account", account.getAccountNumber())
                .append("currency", currency)
                .append("amount", amount)
                .toString();
    }
}