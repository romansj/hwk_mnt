package com.jromans.hwk.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jromans.hwk.shared.constants.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Long requestId;
    private PaymentType paymentType;
    private TransactionCategory category;
    private String accountSrc;
    private String accountDst;
    private String recipientName;
    private Country countryDst;
    private String bankDst;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime paymentDate;
    private ComissionCoverage comissionCoverage;
    private String swift;
    private BigDecimal amount;
    private MintosCurrency currency;
    private String description;
    private RequestStatus status;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
