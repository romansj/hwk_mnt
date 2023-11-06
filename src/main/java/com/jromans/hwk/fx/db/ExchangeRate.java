package com.jromans.hwk.fx.db;

import com.jromans.hwk.shared.constants.MintosCurrency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rate")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3)
    @Enumerated(STRING)
    private MintosCurrency base;

    @Column(length = 3)
    @Enumerated(STRING)
    private MintosCurrency output;

    private BigDecimal rate;

    private LocalDateTime dateTime;

    public ExchangeRate(MintosCurrency base, MintosCurrency output, BigDecimal rate, LocalDateTime dateTime) {
        this.base = base;
        this.output = output;
        this.rate = rate;
        this.dateTime = dateTime;
    }
}
