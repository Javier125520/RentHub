package org.example.renthub.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inmueble {
    private int id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private double precioNoche;
    private boolean disponible;

    // Relación bidireccional
    private Usuario propietario;
    private List<Reserva> reservas = new ArrayList<>();
    private List<Reseña> resenas = new ArrayList<>();
    private List<InmuebleServicio> servicios = new ArrayList<>();

    public Inmueble() {}

    public Inmueble(int id, String titulo, String descripcion, String direccion,
                    String ciudad, double precioNoche, boolean disponible, Usuario propietario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.precioNoche = precioNoche;
        this.disponible = disponible;
        this.setPropietario(propietario);
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Usuario getPropietario() { return propietario; }
    public void setPropietario(Usuario propietario) {
        // mantener coherencia bidireccional
        if (this.propietario != null && this.propietario.getInmuebles().contains(this)) {
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

    public void addReserva(Reserva r) {
        if (!reservas.contains(r)) {
            reservas.add(r);
            r.setInmueble(this);
        }
    }

    public void addResena(Reseña res) {
        if (!resenas.contains(res)) {
            resenas.add(res);
            res.setInmueble(this);
        }
    }

    public void addServicio(InmuebleServicio is) {
        if (!servicios.contains(is)) {
            servicios.add(is);
            is.setInmueble(this);
        }
    }

    @Override
    public String toString() {
        // evitar desbordes incluyendo solo id y título del propietario
        String propietarioInfo = (propietario == null) ? "null" :
                ("Usuario{id=" + propietario.getId() + ", nombre=" + propietario.getNombre() + "}");
        return "Inmueble{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", precioNoche=" + precioNoche +
                ", disponible=" + disponible +
                ", propietario=" + propietarioInfo +
                ", reservas=" + reservas.size() +
                ", servicios=" + servicios.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inmueble inmueble = (Inmueble) o;
        return id == inmueble.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
