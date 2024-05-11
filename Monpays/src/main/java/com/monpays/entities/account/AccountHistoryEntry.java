package com.monpays.entities.account;

import com.monpays.entities._generic.Currency;
import com.monpays.entities._generic.AbstractHistoryEntry;
import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import com.monpays.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "account_history")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountHistoryEntry extends AbstractHistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(nullable = false)
    @NotBlank
    private String accountNumber;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private Currency currency;

    @Column
    private String name;

    @Column
    private Long transactionLimit;

    @Column
    @Enumerated(EnumType.STRING)
    protected EAccountStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    protected EAccountLockStatus accountLockStatus;

    @ManyToOne
    private Account account;

    public AccountHistoryEntry(Account account, Timestamp timestamp) {
        super(timestamp);
        this.id = 0L;
        this.accountNumber = account.getAccountNumber();
        this.owner = account.getOwner();
        this.currency = account.getCurrency();
        this.name = account.getName();
        this.transactionLimit = account.getTransactionLimit();
        this.status = account.getStatus();
        this.accountLockStatus = account.getAccountLockStatus();
        this.account = account;
    }
}
