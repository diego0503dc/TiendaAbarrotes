package com.tienda.dao;

import com.tienda.database.ConexionDB;
import com.tienda.modelos.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    // Método para REGISTRAR
    public boolean registrar(Proveedor p) {
        String sql = "INSERT INTO proveedores (empresa, contacto_nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getEmpresa());
            pstmt.setString(2, p.getContacto());
            pstmt.setString(3, p.getTelefono());
            pstmt.setString(4, p.getDireccion());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar proveedor: " + e.getMessage());
            return false;
        }
    }

    // Método para LISTAR (Traer todos para la tabla)
    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id_proveedor ASC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("id_proveedor"));
                p.setEmpresa(rs.getString("empresa"));
                p.setContacto(rs.getString("contacto_nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        }
        return lista;
    }
    // Método para ACTUALIZAR (Modificar)
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET empresa=?, contacto_nombre=?, telefono=?, direccion=? WHERE id_proveedor=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getEmpresa());
            pstmt.setString(2, p.getContacto());
            pstmt.setString(3, p.getTelefono());
            pstmt.setString(4, p.getDireccion());
            pstmt.setInt(5, p.getId()); // Importante: el ID dice cuál modificar
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }
    
    // Método para ELIMINAR (De una vez, por si lo ocupas luego)
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}