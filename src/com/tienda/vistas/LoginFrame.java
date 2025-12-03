package com.tienda.vistas;

import com.tienda.dao.UsuarioDAO;
import com.tienda.modelos.Usuario;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginFrame() {
        // 1. Configuración básica de la ventana
        setTitle("Login - Abarrotes Violeta");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(null); // Diseño libre
        setResizable(false);

        // 2. Título
        JLabel lblTitulo = new JLabel("Abarrotes Violeta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(90, 20, 250, 30);
        add(lblTitulo);
        lblTitulo.setForeground(new Color(138, 43, 226)); // Texto Violeta

        // 3. Email
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        lblEmail.setBounds(50, 80, 200, 20);
        add(lblEmail);

        txtEmail = new JTextField("admin@tienda.com"); // Texto de prueba
        txtEmail.setBounds(50, 105, 280, 30);
        add(txtEmail);

        // 4. Contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(50, 150, 200, 20);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 175, 280, 30);
        add(txtPassword);

        // 5. Botón
        btnIngresar = new JButton("INGRESAR");
        btnIngresar.setBounds(50, 230, 280, 40);
        // Antes era azul o verde, ahora:
        btnIngresar.setBackground(new Color(138, 43, 226)); // Fondo Violeta
        btnIngresar.setForeground(Color.WHITE); // Letra Blanca para que resalte
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnIngresar);

        // 6. Acción del Botón
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hacerLogin();
            }
        });
    }

    private void hacerLogin() {
        String email = txtEmail.getText();
        String pass = new String(txtPassword.getPassword());

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.login(email, pass);

        if (usuario != null) {
            // AQUÍ MÁS ADELANTE ABRIREMOS EL MENÚ PRINCIPAL
            this.dispose(); // Cierra esta ventana
            MenuPrincipal menu = new MenuPrincipal(usuario);
            menu.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
