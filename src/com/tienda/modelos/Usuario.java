package com.tienda.modelos;

public class Usuario {
    private int id;
    private String nombreCompleto;
    private String email;
    private String password;
    private int idRol;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor lleno
    public Usuario(int id, String nombreCompleto, String email, String password, int idRol) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.idRol = idRol;
    }

    // Getters y Setters (Para acceder a los datos)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
}