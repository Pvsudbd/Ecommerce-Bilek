package com.example.Ecommerce_YV.Security;
import org.mindrot.jbcrypt.BCrypt;

public class BcryptUtil {

    /**
     * Mengubah password plaintext menjadi hash BCrypt yang aman (sudah otomatis menggunakan salt).
     * @param plainPassword Password asli yang diketik user
     * @return Hash string BCrypt, biar jadi kayak $224#
     */
    public static String hashPassword(String plainPassword) {
        // Level 10 level sulit di web :v
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Memeriksa apakah password yang diinput cocok dengan hash yang ada di database.
     * @param plainPassword Password yang diinput user saat login
     * @param hashedPassword Hash password yang diambil dari database MySQL
     * @return true jika cocok, false jika salah
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false; // Bukan format BCrypt atau password kosong
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {

            return false;
        }
    }
}
