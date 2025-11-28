package org.example.renthub.model;

import java.util.Objects;

public class InmuebleServicio {
    private int id;
    private Inmueble inmueble;
    private ServicioExtra servicio;
    private EstadoServicio estado;
    private double precioAdicional;
    private boolean incluidoEnPrecio;

    public InmuebleServicio() {}

    public InmuebleServicio(int id, Inmueble inmueble, ServicioExtra servicio, double precioAdicional, boolean incluidoEnPrecio, EstadoServicio estadoServicio) {
        this.id = id;
        this.setInmueble(inmueble);
        this.setServicio(servicio);
        this.precioAdicional = precioAdicional;
        this.incluidoEnPrecio = incluidoEnPrecio;
        this.estado = estadoServicio;
    }

    // getters y setters


    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null && this.inmueble.getServicios().contains(this)) {
            this.inmueble.getServicios().remove(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null && !inmueble.getServicios().contains(this)) {
            inmueble.getServicios().add(this);
        }
    }

    public ServicioExtra getServicio() { return servicio; }
    public void setServicio(ServicioExtra servicio) {
        if (this.servicio != null && this.servicio.getInmuebles().contains(this)) {
            this.servicio.getInmuebles().remove(this);
        }
        this.servicio = servicio;
        if (servicio != null && !servicio.getInmuebles().contains(this)) {
            servicio.getInmuebles().add(this);
        }
    }

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

    public boolean isIncluidoEnPrecio() {
        return incluidoEnPrecio;
    }

    public void setIncluidoEnPrecio(boolean incluidoEnPrecio) {
        this.incluidoEnPrecio = incluidoEnPrecio;
    }



    @Override
    public String toString() {
        String inmuebleInfo = (inmueble == null) ? "null" : ("Inmueble{id=" + inmueble.getId() + "}");
        String servicioInfo = (servicio == null) ? "null" : ("Servicio{id=" + servicio.getId() + ", nombre=" + servicio.getNombre() + "}");
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
        // igualdad por pareja (inmueble, servicio) si ambos no null
        return Objects.equals(inmueble == null ? null : inmueble.getId(),
                that.inmueble == null ? null : that.inmueble.getId())
                && Objects.equals(servicio == null ? null : servicio.getId(),
                that.servicio == null ? null : that.servicio.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                inmueble == null ? null : inmueble.getId(),
                servicio == null ? null : servicio.getId()
        );
    }
}
