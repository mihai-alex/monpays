package com.monpays.utils;

import java.security.SecureRandom;

import com.monpays.persistence.repositories.account.IAccountRepository;

public class AccountNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

    // Private constructor to prevent instantiation
    private AccountNumberGenerator() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    public static String generateUniqueAccountNumber(IAccountRepository accountRepository) {
        SecureRandom random = new SecureRandom();
        StringBuilder accountNumber = new StringBuilder();

        // Generate a unique account number
        do {
            for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
                int index = random.nextInt(CHARACTERS.length());
                accountNumber.append(CHARACTERS.charAt(index));
            }
        } while (accountRepository.existsByAccountNumber(accountNumber.toString()));

        return accountNumber.toString();
    }
}

