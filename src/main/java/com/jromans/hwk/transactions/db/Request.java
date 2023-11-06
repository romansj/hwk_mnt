package com.jromans.hwk.transactions.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jromans.hwk.shared.constants.*;
import com.jromans.hwk.transactions.TransactionData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static com.jromans.hwk.shared.constants.TransactionCategory.FX;
import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder(toBuilder = true)
@Entity
@Table(name = "request")
public class Request implements TransactionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @NotNull
    private PaymentType paymentType;

    @NotNull
    private TransactionCategory category;

    @NotNull
    private String accountSrc;

    @NotNull
    private String accountDst;

    @NotNull
    private String recipientName;

    private Country countryDst;

    private String bankDst;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    private ZonedDateTime paymentDate = ZonedDateTime.now();

    @Builder.Default
    private ComissionCoverage comissionCoverage = ComissionCoverage.DIVIDED;

    @NotNull
    private String swift;

    @NotNull
    private BigDecimal amount;

    @Builder.Default
    @NotNull
    @Column(length = 3)
    @Enumerated(STRING)
    private MintosCurrency currency = EUR;

    @NotNull
    private String description;

    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Builder.Default
    @Column
    private LocalDateTime dateTime = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "request")
    private List<Transaction> transactions = new ArrayList<>();

    public static Request getFxRequest(Request request, MintosCurrency currency, BigDecimal amount) {
        return request
                .withRequestId(null)
                .withTransactions(List.of())
                .withCategory(FX)
                .withAccountDst(request.getAccountSrc()).withAccountSrc(request.getAccountSrc())
                .withCurrency(currency).withAmount(amount);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.DEBIT;
    }
}
