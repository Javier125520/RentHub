package org.example.renthub.model;

import java.util.Objects;

public class Pago {
    private int id;
    private int idReserva;
    private double monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;

    public Pago() {}

    public Pago(int id, int idReserva, double monto, MetodoPago metodoPago, EstadoPago estado) {
        this.id = id;
        this.idReserva = idReserva;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;
        return id == pago.id && idReserva == pago.idReserva;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idReserva);
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", idReserva=" + idReserva +
                ", monto=" + monto +
                ", metodoPago=" + metodoPago +
                ", estado=" + estado +
                '}';
    }
}
