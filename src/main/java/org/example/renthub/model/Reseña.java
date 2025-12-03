package org.example.renthub.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reseña {
    private int id;
    private int puntuacion; // 1..5
    private String comentario;
    private LocalDate fecha;

    // Relaciones
    private Inmueble inmueble;
    private Usuario huesped;

    public Reseña() {}

    public Reseña(int id, int puntuacion, String comentario, LocalDate fecha, Inmueble inmueble, Usuario huesped) {
        this.id = id;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.setInmueble(inmueble);
        this.setHuesped(huesped);
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null && this.inmueble.getResenas().contains(this)) {
            this.inmueble.getResenas().remove(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null && !inmueble.getResenas().contains(this)) {
            inmueble.getResenas().add(this);
        }
    }

    public Usuario getHuesped() { return huesped; }
    public void setHuesped(Usuario huesped) {
        if (this.huesped != null && this.huesped.getResenas().contains(this)) {
            this.huesped.getResenas().remove(this);
        }
        this.huesped = huesped;
        if (huesped != null && !huesped.getResenas().contains(this)) {
            huesped.getResenas().add(this);
        }
    }

    @Override
    public String toString() {
        String inmuebleInfo = (inmueble == null) ? "null" : ("Inmueble{id=" + inmueble.getIdInmueble() + ", titulo=" + inmueble.getTitulo() + "}");
        String huespedInfo = (huesped == null) ? "null" : ("Usuario{id=" + huesped.getIdUsuario() + ", nombre=" + huesped.getNombre() + "}");
        return "Resena{" +
                "id=" + id +
                ", puntuacion=" + puntuacion +
                ", fecha=" + fecha +
                ", inmueble=" + inmuebleInfo +
                ", huesped=" + huespedInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reseña resena = (Reseña) o;
        return id == resena.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

