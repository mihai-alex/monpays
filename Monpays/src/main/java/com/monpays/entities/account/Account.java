package com.monpays.entities.account;

import com.monpays.entities._generic.Currency;
import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountOperation;
import com.monpays.entities.account.enums.EAccountStatus;
import com.monpays.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String accountNumber;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private Currency currency;

    @Size(max = 50)
    @Column
    private String name;

    @Positive
    @Column
    private Long transactionLimit;

    @Column
    @Enumerated(EnumType.STRING)
    private EAccountStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private EAccountLockStatus accountLockStatus;

    public boolean hasRight(EAccountOperation operation) {
        if(operation == EAccountOperation.SEND) {
            return internalHasSendRight();
        }
        else if(operation == EAccountOperation.RECEIVE) {
            return internalHasReceiveRight();
        }
        else {
            return false;
        }
    }

    private boolean internalHasSendRight() {
        return !List.of(EAccountLockStatus.BLOCKED_DEBIT, EAccountLockStatus.BLOCKED, EAccountLockStatus.CLOSED)
                .contains(this.accountLockStatus);
    }

    private boolean internalHasReceiveRight() {
        return !List.of(EAccountLockStatus.BLOCKED_CREDIT, EAccountLockStatus.BLOCKED, EAccountLockStatus.CLOSED)
                .contains(this.accountLockStatus);
    }
}
