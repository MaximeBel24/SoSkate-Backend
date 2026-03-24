package com.soskate.api.services.token;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Generates secure tokens and passwords using cryptographically secure random.
 */
@Service
public class TokenGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "@$!%*?&";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a unique activation token (UUID format).
     * @return A 36-character UUID string
     */
    public String generateActivationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a secure random password that meets security requirements:
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     *
     * @param length The desired password length (minimum 8)
     * @return A secure random password
     */
    public String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each required category
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // Fill the rest with random characters from all categories
        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }

        // Shuffle the password to avoid predictable pattern
        return shuffleString(password.toString());
    }

    /**
     * Gets a random character from the given character set.
     */
    private char getRandomChar(String charSet) {
        int index = secureRandom.nextInt(charSet.length());
        return charSet.charAt(index);
    }

    /**
     * Shuffles the characters in a string using Fisher-Yates algorithm.
     */
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * Generates a 6-digit numeric code for password reset.
     * @return A 6-digit string (e.g., "482917")
     */
    public String generateResetCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }
}