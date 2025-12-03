package com.tienda.modelos;
import java.sql.Timestamp;

public class Venta {
    private int id;
    private Timestamp fecha;
    private double total;
    private int idUsuario;

    public Venta() {}

    public Venta(int id, Timestamp fecha, double total, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}