package com.monpays.utils;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 3; // Adjust as needed

    public static boolean validatePassword(String password) {
        // Define your password policy rules
        int minLength = 8;            // Minimum password length
        int minLowercase = 1;         // Minimum number of lowercase letters
        int minUppercase = 1;         // Minimum number of uppercase letters
        int minDigits = 1;            // Minimum number of digits
        int minSymbols = 1;           // Minimum number of symbols
        //int minStrengthScore = 3;     // Minimum strength score

        // Check minimum length
        if (password.length() < minLength) {
            return false;
        }

        // Check minimum lowercase letters
        if (password.chars().filter(Character::isLowerCase).count() < minLowercase) {
            return false;
        }

        // Check minimum uppercase letters
        if (password.chars().filter(Character::isUpperCase).count() < minUppercase) {
            return false;
        }

        // Check minimum digits
        if (password.chars().filter(Character::isDigit).count() < minDigits) {
            return false;
        }

        // Check minimum symbols
        if (password.chars().filter(ch -> "!@#$%^&*()_-+=<>?".indexOf(ch) >= 0).count() < minSymbols) {
            return false;
        }

        // You can implement additional checks to calculate a strength score here

        // Return true if the password meets all criteria
        return true;
    }
}
