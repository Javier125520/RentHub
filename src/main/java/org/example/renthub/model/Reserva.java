package org.example.renthub.model;

import java.time.LocalDate;

public class Reserva {
    private int id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;
    private EstadoReserva estado;
    private int idInmueble;
    private int idHuesped;

    public Reserva() {}

    public Reserva(int id, LocalDate fechaInicio, LocalDate fechaFin, double precioTotal, EstadoReserva estado, int idInmueble, int idHuesped) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.idInmueble = idInmueble;
        this.idHuesped = idHuesped;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public int getIdInmueble() {
        return idInmueble;
    }

    public void setIdInmueble(int idInmueble) {
        this.idInmueble = idInmueble;
    }

    public int getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(int idHuesped) {
        this.idHuesped = idHuesped;
    }



    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", total=" + precioTotal +
                ", estado='" + estado + '\'' +
                ", idInmueble=" + idInmueble +
                ", idHuesped=" + idHuesped +
                '}';
    }
}