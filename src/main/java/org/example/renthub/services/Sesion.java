package org.example.renthub.services;

import org.example.renthub.model.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }
}

