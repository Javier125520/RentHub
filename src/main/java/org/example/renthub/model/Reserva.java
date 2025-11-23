package org.example.renthub.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva {
    private int id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double total;
    private EstadoReserva estado;

    // Relaciones
    private Inmueble inmueble;
    private Usuario huesped;
    private Pago pago; // opcional, puede ser null hasta que se realice

    public Reserva() {}

    public Reserva(int id, LocalDate fechaInicio, LocalDate fechaFin, double total,
                   EstadoReserva estado, Inmueble inmueble, Usuario huesped) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
        this.estado = estado;
        this.setInmueble(inmueble);
        this.setHuesped(huesped);
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null && this.inmueble.getReservas().contains(this)) {
            this.inmueble.getReservas().remove(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null && !inmueble.getReservas().contains(this)) {
            inmueble.getReservas().add(this);
        }
    }

    public Usuario getHuesped() { return huesped; }
    public void setHuesped(Usuario huesped) {
        if (this.huesped != null && this.huesped.getReservas().contains(this)) {
            this.huesped.getReservas().remove(this);
        }
        this.huesped = huesped;
        if (huesped != null && !huesped.getReservas().contains(this)) {
            huesped.getReservas().add(this);
        }
    }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) {
        this.pago = pago;
        if (pago != null && pago.getReserva() != this) {
            pago.setReserva(this);
        }
    }

    @Override
    public String toString() {
        String inmuebleInfo = (inmueble == null) ? "null" :
                ("Inmueble{id=" + inmueble.getId() + ", titulo=" + inmueble.getTitulo() + "}");
        String huespedInfo = (huesped == null) ? "null" :
                ("Usuario{id=" + huesped.getId() + ", nombre=" + huesped.getNombre() + "}");
        return "Reserva{" +
                "id=" + id +
                ", inicio=" + fechaInicio +
                ", fin=" + fechaFin +
                ", total=" + total +
                ", estado=" + estado +
                ", inmueble=" + inmuebleInfo +
                ", huesped=" + huespedInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reserva reserva = (Reserva) o;
        return id == reserva.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}