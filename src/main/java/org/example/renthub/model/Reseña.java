package org.example.renthub.model;

import java.sql.Date;
import java.util.Objects;

public class Reseña {
    private int id;
    private int calificacion;
    private String comentario;
    private Date fecha;

    public Reseña() {}

    public Reseña(int id, int calificacion, String comentario, Date fecha) {
        this.id = id;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reseña resena = (Reseña) o;
        return Objects.equals(id, resena.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reseña{" +
                "id=" + id +
                ", calificacion=" + calificacion +
                ", comentario='" + comentario + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
