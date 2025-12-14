package com.salleo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class HashUtil {
    private HashUtil() {}

    private static final Pattern SHA256_HEX = Pattern.compile("^[0-9a-fA-F]{64}$");
    private static final Pattern BCRYPT = Pattern.compile("^\\$2[aby]\\$.{56}$");

    public static boolean looksLikeSha256Hex(String s) {
        return s != null && SHA256_HEX.matcher(s).matches();
    }

    public static String sha256Hex(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format(Locale.ROOT, "%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean looksLikeBcrypt(String s) {
        return s != null && BCRYPT.matcher(s).matches();
    }
}

