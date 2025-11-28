package org.example.renthub.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicioExtra {
    private int id;
    private String nombre;
    private String descripcion;

    private List<InmuebleServicio> inmuebles = new ArrayList<>();

    public ServicioExtra() {}

    public ServicioExtra(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<InmuebleServicio> getInmuebles() { return inmuebles; }
    public void setInmuebles(List<InmuebleServicio> inmuebles) { this.inmuebles = inmuebles; }

    public void addInmuebleServicio(InmuebleServicio is) {
        if (!inmuebles.contains(is)) {
            inmuebles.add(is);
            is.setServicio(this);
        }
    }

    @Override
    public String toString() {
        return "ServicioExtra{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", inmuebles=" + inmuebles.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicioExtra that = (ServicioExtra) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
