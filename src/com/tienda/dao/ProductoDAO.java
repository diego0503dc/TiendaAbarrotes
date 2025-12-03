package com.tienda.dao;

import com.tienda.database.ConexionDB;
import com.tienda.modelos.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean registrar(Producto p) {
        String sql = "INSERT INTO productos (codigo_barras, nombre, precio, stock, id_proveedor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getCodigoBarras());
            pstmt.setString(2, p.getNombre());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setInt(4, p.getStock());
            pstmt.setInt(5, p.getIdProveedor());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET codigo_barras=?, nombre=?, precio=?, stock=?, id_proveedor=? WHERE id_producto=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getCodigoBarras());
            pstmt.setString(2, p.getNombre());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setInt(4, p.getStock());
            pstmt.setInt(5, p.getIdProveedor());
            pstmt.setInt(6, p.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        // JOINS: Traemos datos de Productos Y el nombre de la empresa de Proveedores
        String sql = "SELECT p.*, prov.empresa FROM productos p " +
                     "LEFT JOIN proveedores prov ON p.id_proveedor = prov.id_proveedor " +
                     "ORDER BY p.nombre ASC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id_producto"));
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setIdProveedor(rs.getInt("id_proveedor"));
                
                // Guardamos el nombre del proveedor para mostrarlo en la tabla
                p.setNombreProveedor(rs.getString("empresa")); 
                
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error listar productos: " + e.getMessage());
        }
        return lista;
    }
    // Buscar producto por CÓDIGO DE BARRAS (Para el escáner)
    public Producto buscarPorCodigo(String codigo) {
        Producto p = null;
        String sql = "SELECT * FROM productos WHERE codigo_barras = ? AND stock > 0";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setCodigoBarras(rs.getString("codigo_barras"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar por código: " + e.getMessage());
        }
        return p;
    }
    // Buscar productos que contengan cierto texto en el nombre
    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        // ILIKE es exclusivo de Postgres: ignora mayúsculas y minúsculas
        String sql = "SELECT * FROM productos WHERE nombre ILIKE ? AND stock > 0"; 
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + texto + "%"); // Los % son para buscar coincidencia parcial
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id_producto"));
                    p.setCodigoBarras(rs.getString("codigo_barras"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar por nombre: " + e.getMessage());
        }
        return lista;
    }
}