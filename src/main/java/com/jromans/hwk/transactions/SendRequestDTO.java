package com.jromans.hwk.transactions;

import com.jromans.hwk.shared.constants.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

@Getter
@Setter
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRequestDTO {
    private PaymentType paymentType;
    private TransactionCategory category;

    @NotBlank
    private String accountSrc;

    @NotBlank
    private String accountDst;

    @NotBlank
    private String recipientName;

    private Country countryDst;
    private String bankDst;
    private ComissionCoverage comissionCoverage;
    private String swift;

    @Min(0)
    @Max(1000000)
    private BigDecimal amount;

    @Builder.Default
    private MintosCurrency currency = MintosCurrency.EUR;

    @NotBlank
    private String description;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
