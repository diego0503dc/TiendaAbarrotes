package com.tienda.modelos;

public class Producto {
    private int id;
    private String codigoBarras;
    private String nombre;
    private double precio;
    private int stock;
    private int idProveedor;
    private String nombreProveedor; // Campo extra solo para mostrar en la tabla

    public Producto() {
    }

    public Producto(int id, String codigoBarras, String nombre, double precio, int stock, int idProveedor) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    
    // Este método es un truco para llenar el ComboBox (Lista desplegable) más adelante
    @Override
    public String toString() {
        return nombre;
    }
}