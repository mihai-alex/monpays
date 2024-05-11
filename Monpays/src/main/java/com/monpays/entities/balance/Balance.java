package com.monpays.entities.balance;

import com.monpays.entities.account.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "balances")
@Data
//@AllArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;
    @ManyToOne
    private Account account;
    // money THAT HE HAS RECEIVED
    @Column
    private Long availableCreditAmount;
    @Column
    private int availableCreditCount;
    // money THAT HE HAS SENT
    @Column
    private Long availableDebitAmount;
    @Column
    private int availableDebitCount;
    @Column
    private Long pendingCreditAmount;
    @Column
    private int pendingCreditCount;
    @Column
    private Long pendingDebitAmount;
    @Column
    private int pendingDebitCount;

    public Balance() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Balance(Balance balance) {
        this.id = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.account = balance.account;
        this.availableCreditAmount = balance.availableCreditAmount;
        this.availableCreditCount = balance.availableCreditCount;
        this.availableDebitAmount = balance.availableDebitAmount;
        this.availableDebitCount = balance.availableDebitCount;
        this.pendingCreditAmount = balance.pendingCreditAmount;
        this.pendingCreditCount = balance.pendingCreditCount;
        this.pendingDebitAmount = balance.pendingDebitAmount;
        this.pendingDebitCount = balance.pendingDebitCount;
    }

    // methods for getting amounts

    public Long getAvailableAmount() {              // working amount
        return availableCreditAmount - availableDebitAmount;
    }

    public Long getPendingAmount() {
        return pendingCreditAmount - pendingDebitAmount;
    }

    public Long getProjectedAmount() {
        return getAvailableAmount() + getPendingAmount();
    }

    // methods for sending and receiving money

    public void sendAmountPending(Long amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        pendingDebitAmount += amount;
        pendingDebitCount ++;
    }

    public void removeSentAmountPending(Long amount) throws IllegalArgumentException {
        internalVerifyAmountForRemoval(amount, pendingDebitAmount, pendingDebitCount);
        pendingDebitAmount -= amount;
        pendingDebitCount --;
    }

    public void receiveAmountPending(Long amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        pendingCreditAmount += amount;
        pendingCreditCount ++;
    }

    public void removeReceivedAmountPending(Long amount) throws IllegalArgumentException {
        internalVerifyAmountForRemoval(amount, pendingCreditAmount, pendingCreditCount);
        pendingCreditAmount -= amount;
        pendingCreditCount --;
    }

    public void sendAmountAvailable(Long amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        availableDebitAmount += amount;
        availableDebitCount ++;
    }

    public void receiveAmountAvailable(Long amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        availableCreditAmount += amount;
        availableCreditCount ++;
    }

    // verification methods for sending and receiving money

    private void internalVerifyAmount(Long amount) throws IllegalArgumentException {
        if(amount <= 0) {                          // amount must be positive
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void internalVerifyAmountForRemoval(Long amount, Long actualAmount, int count) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        if(amount > actualAmount) {         // amount must be less than or equal to pending amount
            throw new IllegalArgumentException("Amount to remove must be less than or equal to actual amount");
        }
        if(count <= 0) {              // pending count must be greater than 0
            throw new IllegalArgumentException("Transaction count must be greater than 0");
        }
    }
}
