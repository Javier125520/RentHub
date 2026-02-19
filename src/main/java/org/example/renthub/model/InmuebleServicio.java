package org.example.renthub.model;

import org.example.renthub.model.enums.EstadoServicio;

import java.util.Objects;

public class InmuebleServicio {
    private int id;
    private Inmueble inmueble;
    private ServicioExtra servicio;
    private EstadoServicio estado;
    private double precioAdicional;

    public InmuebleServicio() {}

    public InmuebleServicio(int id, Inmueble inmueble, ServicioExtra servicio, double precioAdicional, EstadoServicio estado) {
        this.id = id;
        setInmueble(inmueble);
        setServicio(servicio);
        this.precioAdicional = precioAdicional;
        this.estado = estado;
    }

    // Getter y Setter


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public double getPrecioAdicional() {
        return precioAdicional;
    }

    public void setPrecioAdicional(double precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null) {
            this.inmueble.getServicios().remove(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null && !inmueble.getServicios().contains(this)) {
            inmueble.getServicios().add(this);
        }
    }

    public Inmueble getInmueble() { return inmueble; }

    public void setServicio(ServicioExtra servicio) {
        if (this.servicio != null) {
            this.servicio.getInmuebles().remove(this);
        }
        this.servicio = servicio;
        if (servicio != null && !servicio.getInmuebles().contains(this)) {
            servicio.getInmuebles().add(this);
        }
    }

    public ServicioExtra getServicio() { return servicio; }

    @Override
    public String toString() {
        String inmuebleInfo = (inmueble == null) ? "null" : ("Inmueble{id=" + inmueble.getIdInmueble() + "}");
        String servicioInfo = (servicio == null) ? "null" : ("Servicio{id=" + servicio.getIdServicio() + ", nombre=" + servicio.getNombre() + "}");
        return "InmuebleServicio{" +
                inmuebleInfo +
                ", " + servicioInfo +
                ", precioAdicional=" + precioAdicional +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InmuebleServicio that = (InmuebleServicio) o;
        return Objects.equals(inmueble, that.inmueble) && Objects.equals(servicio, that.servicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inmueble, servicio);
    }
}
