package org.example.renthub.model;

import org.example.renthub.model.enums.EstadoReserva;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

public class Reserva {
    private int idReserva;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double PrecioTotal;
    private EstadoReserva estado;
    private Timestamp fechaRegistro;

    private Inmueble inmueble;
    private Usuario huesped;

    public Reserva() {}

    public Reserva(int idReserva, LocalDate fechaEntrada, LocalDate fechaSalida, double total, EstadoReserva estado, Timestamp fechaRegistro, Inmueble inmueble, Usuario huesped) {
        this.idReserva = idReserva;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.PrecioTotal = total;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.setInmueble(inmueble);
        this.setHuesped(huesped);
    }

    // getters y setters
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public double getPrecioTotal() { return PrecioTotal; }
    public void setPrecioTotal(double precioTotal) { this.PrecioTotal = precioTotal; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null) {
            this.inmueble.getReservas().remove(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null && !inmueble.getReservas().contains(this)) {
            inmueble.getReservas().add(this);
        }
    }

    public Usuario getHuesped() { return huesped; }
    public void setHuesped(Usuario huesped) {
        if (this.huesped != null) {
            this.huesped.getReservas().remove(this);
        }
        this.huesped = huesped;
        if (huesped != null && !huesped.getReservas().contains(this)) {
            huesped.getReservas().add(this);
        }
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", total=" + PrecioTotal +
                ", estado=" + estado +
                ", inmueble=" + inmueble +
                ", huesped=" + huesped +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reserva reserva = (Reserva) o;
        return idReserva == reserva.idReserva;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva);
    }

}