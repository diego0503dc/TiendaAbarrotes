package com.tienda.dao;

import com.tienda.database.ConexionDB;
import com.tienda.modelos.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario login(String email, String password) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setIdRol(rs.getInt("id_rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en Login: " + e.getMessage());
        }
        return usuario;
    }
    // --- NUEVOS MÉTODOS PARA GESTIÓN DE USUARIOS ---

    public boolean registrar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre_completo, email, password, id_rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, u.getNombreCompleto());
            pstmt.setString(2, u.getEmail());
            pstmt.setString(3, u.getPassword());
            pstmt.setInt(4, u.getIdRol());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre_completo=?, email=?, password=?, id_rol=? WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, u.getNombreCompleto());
            pstmt.setString(2, u.getEmail());
            pstmt.setString(3, u.getPassword());
            pstmt.setInt(4, u.getIdRol());
            pstmt.setInt(5, u.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public java.util.List<Usuario> listar() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id_usuario ASC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}
