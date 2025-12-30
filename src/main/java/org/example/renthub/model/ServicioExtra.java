package org.example.renthub.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicioExtra {
    private int idServicio;
    private String nombre;
    private String descripcion;
    private List<InmuebleServicio> inmuebles = new ArrayList<>();

    public ServicioExtra() {}

    public ServicioExtra(int idServicio, String nombre, String descripcion) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // getters y setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<InmuebleServicio> getInmuebles() { return inmuebles; }
    public void setInmuebles(List<InmuebleServicio> inmuebles) { this.inmuebles = inmuebles; }


    @Override
    public String toString() {
        return "ServicioExtra{" +
                "id=" + idServicio +
                ", nombre='" + nombre + '\'' +
                ", inmuebles=" + inmuebles.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicioExtra that = (ServicioExtra) o;
        return idServicio == that.idServicio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idServicio);
    }
}
