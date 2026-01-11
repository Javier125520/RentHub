package org.example.renthub.services;

import org.example.renthub.model.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static boolean estaLogueado() {
        return usuarioActual != null;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }
}

