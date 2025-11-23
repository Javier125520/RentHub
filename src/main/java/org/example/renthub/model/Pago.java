package org.example.renthub.model;

import java.time.LocalDate;
import java.util.Objects;

public class Pago {
    private int id;
    private MetodoPago metodo;
    private LocalDate fechaPago;
    private double monto;
    private EstadoPago estado;

    // Relación bidireccional
    private Reserva reserva;

    public Pago() {}

    public Pago(int id, MetodoPago metodo, LocalDate fechaPago, double monto, EstadoPago estado, Reserva reserva) {
        this.id = id;
        this.metodo = metodo;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.estado = estado;
        this.setReserva(reserva);
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        if (reserva != null && reserva.getPago() != this) {
            reserva.setPago(this);
        }
    }

    @Override
    public String toString() {
        String reservaInfo = (reserva == null) ? "null" : ("Reserva{id=" + reserva.getId() + "}");
        return "Pago{" +
                "id=" + id +
                ", metodo=" + metodo +
                ", monto=" + monto +
                ", estado=" + estado +
                ", reserva=" + reservaInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pago pago = (Pago) o;
        return id == pago.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}