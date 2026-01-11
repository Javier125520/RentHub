package org.example.renthub.model;

import org.example.renthub.model.enums.TipoInmueble;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inmueble {
    private int idInmueble;
    private TipoInmueble tipoInmueble;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private int capacidad;
    private int numeroHabitaciones;
    private double precioNoche;
    private boolean disponible;


    // Relación bidireccional
    private Usuario propietario;
    private List<Reserva> reservas = new ArrayList<>();
    private List<Reseña> resenas = new ArrayList<>();
    private List<InmuebleServicio> servicios = new ArrayList<>();
    private List<ImagenInmueble> imagenes = new ArrayList<>();


    public Inmueble() {}

    public Inmueble(int idInmueble, TipoInmueble tipoInmueble, String titulo, String descripcion, String direccion, String ciudad, int capacidad, int numeroHabitaciones, double precioNoche, boolean disponible, Usuario propietario) {
        this.idInmueble = idInmueble;
        this.tipoInmueble = tipoInmueble;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.numeroHabitaciones = numeroHabitaciones;
        this.precioNoche = precioNoche;
        this.disponible = disponible;
        setPropietario(propietario);
    }

    // getters y setters
    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public int getNumeroHabitaciones() { return numeroHabitaciones; }
    public void setNumeroHabitaciones(int numeroHabitaciones) { this.numeroHabitaciones = numeroHabitaciones; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public Usuario getPropietario() { return propietario; }
    public void setPropietario(Usuario propietario) {
        if (this.propietario != null) {
            this.propietario.getInmuebles().remove(this);
        }
        this.propietario = propietario;
        if (propietario != null && !propietario.getInmuebles().contains(this)) {
            propietario.getInmuebles().add(this);
        }
    }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    public List<Reseña> getResenas() { return resenas; }
    public void setResenas(List<Reseña> resenas) { this.resenas = resenas; }

    public List<InmuebleServicio> getServicios() { return servicios; }
    public void setServicios(List<InmuebleServicio> servicios) { this.servicios = servicios; }

    public List<ImagenInmueble> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenInmueble> imagenes) {
        this.imagenes = imagenes;
    }

    void addImagen(ImagenInmueble imagen) {
        if (!imagenes.contains(imagen)) {
            imagenes.add(imagen);
        }
    }

    void removeImagen(ImagenInmueble imagen) {
        imagenes.remove(imagen);
    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "idInmueble=" + idInmueble +
                ", tipoInmueble=" + tipoInmueble +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", capacidad=" + capacidad +
                ", numeroHabitaciones=" + numeroHabitaciones +
                ", precioNoche=" + precioNoche +
                ", disponible=" + disponible +
                ", propietario=" + propietario +
                ", reservas=" + reservas +
                ", resenas=" + resenas +
                ", servicios=" + servicios +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inmueble inmueble = (Inmueble) o;
        return idInmueble == inmueble.idInmueble;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInmueble);
    }
}
