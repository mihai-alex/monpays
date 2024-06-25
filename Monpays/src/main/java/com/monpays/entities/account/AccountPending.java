package com.monpays.entities.account;

import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pending_accounts")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPending {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "original_account_accountNumber", referencedColumnName = "accountNumber", unique = true)
    private Account originalAccount;

    @Column
    private String accountNumber;

    @Column
    private String ownerUserName;

    @Column
    private String currency; // this is the Currency Code, e.g. USD, EUR, etc.

    @Column
    private String name;

    @Column
    private BigDecimal transactionLimit;

    @Column
    @Enumerated(EnumType.STRING)
    private EAccountStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private EAccountLockStatus accountLockStatus;

    @Column
    private String actorUserName; // the username of the user who created the pending entry
}
