package org.example.renthub.model;

import java.util.Objects;

public class Inmueble {
    private int id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private double precioNoche;
    private boolean disponible;
    private int idPropietario;

    public Inmueble() {}

    public Inmueble(int id, String titulo, String descripcion, String direccion, String ciudad, double precioNoche, boolean disponible, int idPropietario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.precioNoche = precioNoche;
        this.disponible = disponible;
        this.idPropietario = idPropietario;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inmueble inmueble = (Inmueble) o;
        return id == inmueble.id && Objects.equals(direccion, inmueble.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, direccion);
    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", precioNoche=" + precioNoche +
                ", disponible=" + disponible +
                ", idPropietario=" + idPropietario +
                '}';
    }
}
