package org.example.renthub.utils;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Utiles {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Mínimo 8 caracteres, una mayúscula, una minúscula y un número
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public static boolean correoValido(String correo) {
        return correo != null && EMAIL_PATTERN.matcher(correo).matches();
    }

    public static boolean passwordValida(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean fechasValidas(LocalDate inicio, LocalDate fin) {
        return inicio != null &&
                fin != null &&
                fin.isAfter(inicio);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar contraseña");
        }
    }
}
