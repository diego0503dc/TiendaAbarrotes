package com.tienda.modelos;

public class Proveedor {
    private int id;
    private String empresa;
    private String contacto;
    private String telefono;
    private String direccion;

    public Proveedor() {}

    public Proveedor(int id, String empresa, String contacto, String telefono, String direccion) {
        this.id = id;
        this.empresa = empresa;
        this.contacto = contacto;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}