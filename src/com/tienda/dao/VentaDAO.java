package com.tienda.dao;

import com.tienda.database.ConexionDB;
import com.tienda.modelos.DetalleVenta;
import com.tienda.modelos.Venta;
import java.sql.*;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        
        String sqlVenta = "INSERT INTO ventas (total, id_usuario) VALUES (?, ?) RETURNING id_venta";
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

        try {
            conn = ConexionDB.getConnection();
            // 1. DESACTIVAMOS EL GUARDADO AUTOMÁTICO (Inicia Transacción)
            conn.setAutoCommit(false);

            // 2. Insertar Venta y obtener el ID generado
            psVenta = conn.prepareStatement(sqlVenta);
            psVenta.setDouble(1, venta.getTotal());
            psVenta.setInt(2, venta.getIdUsuario());
            
            ResultSet rs = psVenta.executeQuery();
            int idVentaGenerado = 0;
            if (rs.next()) {
                idVentaGenerado = rs.getInt(1);
            }

            // 3. Insertar Detalles y Actualizar Stock
            psDetalle = conn.prepareStatement(sqlDetalle);
            psStock = conn.prepareStatement(sqlUpdateStock);

            for (DetalleVenta dv : detalles) {
                // Insertar detalle
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, dv.getIdProducto());
                psDetalle.setInt(3, dv.getCantidad());
                psDetalle.setDouble(4, dv.getPrecioUnitario());
                psDetalle.setDouble(5, dv.getSubtotal());
                psDetalle.addBatch(); // Agregamos al lote

                // Actualizar stock
                psStock.setInt(1, dv.getCantidad());
                psStock.setInt(2, dv.getIdProducto());
                psStock.addBatch(); // Agregamos al lote
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            // 4. SI TODO SALIÓ BIEN, GUARDAMOS DE VERDAD
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en transacción de venta: " + e.getMessage());
            try {
                if (conn != null) conn.rollback(); // CANCELAR TODO SI HUBO ERROR
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Restaurar estado normal
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (psStock != null) psStock.close();
                // No cerramos conn aquí porque usamos Singleton, pero en frameworks grandes se hace diferente
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}