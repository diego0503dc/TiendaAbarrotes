package com.tienda.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    // Configuración de la Base de Datos
    private static final String URL = "jdbc:postgresql://localhost:5432/TiendaAbarrotes";
    
    // AQUI es donde le decimos a Java que use el usuario que creamos
    private static final String USER = "cajero_app"; 
    private static final String PASS = "tienda2025"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Conexión Exitosa a la BD desde ConexionDB");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        return conn;
    }
}