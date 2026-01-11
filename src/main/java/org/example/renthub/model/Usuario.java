package org.example.renthub.model;

import org.example.renthub.model.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private RolUsuario rol;

    // Relaciones bidireccionales
    private List<Inmueble> inmuebles = new ArrayList<>(); // si rol == PROPIETARIO
    private List<Reserva> reservas = new ArrayList<>();  // si rol == HUESPED
    private List<Reseña> resenas = new ArrayList<>();    // reseñas escritas por este usuario

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String correo, String contrasena, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // getters y setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public List<Inmueble> getInmuebles() { return inmuebles; }
    public void setInmuebles(List<Inmueble> inmuebles) { this.inmuebles = inmuebles; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    public List<Reseña> getResenas() { return resenas; }
    public void setResenas(List<Reseña> resenas) { this.resenas = resenas; }

    // utilidades para mantener coherencia bidireccional

    public void addInmueble(Inmueble inmueble) {
        if (!inmuebles.contains(inmueble)) {
            inmuebles.add(inmueble);
            inmueble.setPropietario(this);
        }
    }

    public void removeInmueble(Inmueble inmueble) {
        if (inmuebles.remove(inmueble)) {
            inmueble.setPropietario(null);
        }
    }

    public void addReserva(Reserva r) {
        if (!reservas.contains(r)) {
            reservas.add(r);
            r.setHuesped(this);
        }
    }

    public void addResena(Reseña res) {
        if (!resenas.contains(res)) {
            resenas.add(res);
            res.setHuesped(this);
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol=" + rol +
                ", inmuebles=" + inmuebles.size() +
                ", reservas=" + reservas.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}