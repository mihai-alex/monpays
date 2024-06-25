package com.monpays.entities.payment;

import com.monpays.entities._generic.Currency;
import com.monpays.entities.account.Account;
import com.monpays.entities.payment.enums.EPaymentStatus;
import com.monpays.entities.payment.enums.EPaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String number;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;
    @Column
    private Currency currency;

    @Positive
    @Column
    private BigDecimal amount;
    // the money is transferred FROM this account
    @ManyToOne
    private Account debitAccount;
    // the money is transferred INTO this account
    @ManyToOne
    private Account creditAccount;
    @Column
    @Size(max = 255)
    private String description;
    @Column
    private EPaymentType type;
    @Column
    private EPaymentStatus status;

    @Column
    private BigDecimal debitAmount;
    @Column
    private BigDecimal creditAmount;

    public void setDebitAmountWithFraction(BigDecimal amount, int fractionDigits) {
        this.debitAmount = amount.setScale(fractionDigits, BigDecimal.ROUND_HALF_EVEN);
    }

    public void setCreditAmountWithFraction(BigDecimal amount, int fractionDigits) {
        this.creditAmount = amount.setScale(fractionDigits, BigDecimal.ROUND_HALF_EVEN);
    }
}
