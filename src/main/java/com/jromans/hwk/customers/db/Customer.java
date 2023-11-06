package com.jromans.hwk.customers.db;

import com.jromans.hwk.accounts.db.Account;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long custId;

    @Column
    private String name;

    @Column(unique = true)
    private String identityNumber;

    @OneToMany(cascade = ALL, mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<>();

    @Override
    public String toString() {
        var accountNumbers = accounts.stream().map(Account::getAccountNumber).collect(Collectors.joining(", "));

        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("custId", custId)
                .append("name", name)
                .append("accounts", accountNumbers)
                .toString();
    }

    public Customer(String name, List<Account> accounts) {
        this.name = name;
        this.accounts = accounts;
    }

    public Customer(String name, String identityNumber) {
        this.name = name;
        this.identityNumber = identityNumber;
    }
}
