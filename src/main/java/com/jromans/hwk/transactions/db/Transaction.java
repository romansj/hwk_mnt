package com.jromans.hwk.transactions.db;

import com.jromans.hwk.accounts.db.Account;
import com.jromans.hwk.shared.constants.MintosCurrency;
import com.jromans.hwk.shared.constants.TransactionStatus;
import com.jromans.hwk.shared.constants.TransactionType;
import com.jromans.hwk.transactions.TransactionData;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.jromans.hwk.shared.constants.TransactionStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;

/**
 * The ledger
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@With
@Entity
@Table(name = "transaction")
public class Transaction implements TransactionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_number")
    private Account account;

    @Enumerated(STRING)
    @Column
    private TransactionType transactionType;

    @Enumerated(STRING)
    @Column
    private TransactionStatus transactionStatus;

    @Enumerated(STRING)
    @Column(length = 3)
    private MintosCurrency currency;

    @Column
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Builder.Default
    @Column
    private LocalDateTime dateTime = LocalDateTime.now();


    public static Transaction getTransaction(Request request, Account account, TransactionType type) {
        return Transaction.builder()
                .withRequest(request)
                .withCurrency(request.getCurrency())
                .withAmount(request.getAmount())
                .withAccount(account)
                .withTransactionType(type)
                .withTransactionStatus(PENDING)
                .build();
    }

    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("transactionId", transactionId)
                .append("account", account.getAccountNumber())
                .append("transactionType", transactionType)
                .append("transactionStatus", transactionStatus)
                .append("currency", currency)
                .append("amount", amount)
                .append("request", request.getRequestId())
                .append("dateTime", dateTime)
                .toString();
    }
}
