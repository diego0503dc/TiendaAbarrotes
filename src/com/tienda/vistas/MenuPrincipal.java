package com.tienda.vistas;

import com.tienda.modelos.Usuario;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MenuPrincipal extends JFrame {

    private Usuario usuarioActual;

    public MenuPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // 1. Configuración de la ventana
        setTitle("Sistema de Tienda - Menú Principal");
        setSize(800, 600); // Ventana grande
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // 2. Bienvenida
        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuario.getNombreCompleto());
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        lblBienvenida.setBounds(30, 20, 500, 30);
        add(lblBienvenida);
        
        JLabel lblRol = new JLabel("Rol: " + (usuario.getIdRol() == 1 ? "ADMINISTRADOR" : "CAJERO"));
        lblRol.setFont(new Font("Arial", Font.ITALIC, 14));
        lblRol.setForeground(Color.GRAY);
        lblRol.setBounds(30, 50, 300, 20);
        add(lblRol);

        // --- BOTONES DEL MENÚ ---
        
        // Botón Proveedores
        JButton btnProveedores = crearBoton("Gestionar Proveedores", 50, 100);
        btnProveedores.addActionListener(e -> {
            new ProveedoresFrame().setVisible(true);
            
        });
        add(btnProveedores);

        // Botón Productos
        JButton btnProductos = crearBoton("Gestionar Productos", 280, 100);
        btnProductos.addActionListener(e -> {
             new ProductosFrame().setVisible(true);
        });
        add(btnProductos);

        // Botón Ventas (Caja)
        JButton btnVentas = crearBoton("Nueva Venta", 510, 100);
        btnVentas.setBackground(new Color(0, 153, 76)); // Verde para ventas
        add(btnVentas);
        
        // Botón Usuarios (Solo visible para Admins)
        if (usuario.getIdRol() == 1) { // 1 = Admin
            JButton btnUsuarios = crearBoton("Gestionar Usuarios", 50, 200);
            btnUsuarios.setBackground(new Color(255, 102, 0)); // Naranja
            add(btnUsuarios);
        }
    }

    // Método auxiliar para crear botones bonitos y no repetir código
    private JButton crearBoton(String texto, int x, int y) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 200, 80);
        btn.setBackground(new Color(33, 150, 243)); // Azul
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
}