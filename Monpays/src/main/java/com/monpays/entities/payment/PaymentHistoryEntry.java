package com.monpays.entities.payment;

import com.monpays.entities._generic.AbstractHistoryEntry;
import com.monpays.entities._generic.Currency;
import com.monpays.entities.account.Account;
import com.monpays.entities.payment.enums.EPaymentStatus;
import com.monpays.entities.payment.enums.EPaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryEntry extends AbstractHistoryEntry {
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
    @Column
    private Long amount;
    // the money is transferred FROM this account
    @ManyToOne
    private Account debitAccount;
    // the money is transferred INTO this account
    @ManyToOne
    private Account creditAccount;
    @Column
    private String description;
    @Column
    private EPaymentType type;
    @Column
    private EPaymentStatus status;
    @ManyToOne
    private Payment payment;
}
