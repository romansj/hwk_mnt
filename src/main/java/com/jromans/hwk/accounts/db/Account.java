package com.jromans.hwk.accounts.db;

import com.jromans.hwk.accounts.IAccount;
import com.jromans.hwk.customers.db.Customer;
import com.jromans.hwk.shared.constants.MintosCurrency;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static java.math.BigDecimal.ZERO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Table(name = "account")
@Entity
public class Account implements IAccount {
    @Column
    @Id
    private String accountNumber;

    @OneToMany(cascade = ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private List<Balance> balances = new ArrayList<>();

    @Column
    @Enumerated(STRING)
    private MintosCurrency accountCurrency = MintosCurrency.EUR;

    @ManyToOne
    @JoinColumn(name = "cust_id", nullable = false)
    private Customer customer;

    public BigDecimal getBalanceInAccountCurrency() {
        return getBalanceInCurrency(accountCurrency);
    }

    public BigDecimal getBalanceInCurrency(MintosCurrency currency) {
        return balances.stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .map(Balance::getAmount)
                .orElse(ZERO);
        // return balances.getOrDefault(accountCurrency, ZERO);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
