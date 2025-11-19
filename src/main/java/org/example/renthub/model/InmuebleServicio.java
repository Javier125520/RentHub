package org.example.renthub.model;

import java.util.Objects;

public class InmuebleServicio {
    private int id;
    private int idInmueble;
    private int idServicio;
    private EstadoServicio estado;
    private double precio_adicional;
    private boolean incluido_en_precio;

    public InmuebleServicio() {}

    public InmuebleServicio(int id, int idInmueble, int idServicio, EstadoServicio estado, double precio_adicional, boolean incluido_en_precio) {
        this.id = id;
        this.idInmueble = idInmueble;
        this.idServicio = idServicio;
        this.estado = estado;
        this.precio_adicional = precio_adicional;
        this.incluido_en_precio = incluido_en_precio;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInmueble() {
        return idInmueble;
    }

    public void setIdInmueble(int idInmueble) {
        this.idInmueble = idInmueble;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public double getPrecio_adicional() {
        return precio_adicional;
    }

    public void setPrecio_adicional(double precio_adicional) {
        this.precio_adicional = precio_adicional;
    }

    public boolean isIncluido_en_precio() {
        return incluido_en_precio;
    }

    public void setIncluido_en_precio(boolean incluido_en_precio) {
        this.incluido_en_precio = incluido_en_precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InmuebleServicio that = (InmuebleServicio) o;
        return id == that.id && idInmueble == that.idInmueble && idServicio == that.idServicio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idInmueble, idServicio);
    }

    @Override
    public String toString() {
        return "InmuebleServicio{" +
                "id=" + id +
                ", idInmueble=" + idInmueble +
                ", idServicio=" + idServicio +
                ", estado=" + estado +
                ", precio_adicional=" + precio_adicional +
                ", incluido_en_precio=" + incluido_en_precio +
                '}';
    }
}
