package DRTSPlugins.drtsAuthPlugin.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;

import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String password) {
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = skf.generateSecret(spec).getEncoded();

            Base64.Encoder enc = Base64.getEncoder();
            return enc.encodeToString(salt) + ":" + enc.encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("[DRTS Auth Plugin] Failed to has password: " + e.getMessage(), e);
        }
    }

    public static boolean verifyPassword(String providedPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("[DRTS Auth Plugin] Saved password should has format 'salt:hash'");
        }

        Base64.Decoder dec = Base64.getDecoder();
        byte[] salt = dec.decode(parts[0]);
        byte[] storedHash = dec.decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(providedPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] providedHash = skf.generateSecret(spec).getEncoded();

            if (providedHash.length != storedHash.length) return false;

            for (int i = 0; i < providedHash.length; i++) {
                if (providedHash[i] != storedHash[i]) return false;
            }

            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("[DRTS Auth Plugin] Failed to check password: " + e.getMessage(), e);
        }
    }

    private static byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
