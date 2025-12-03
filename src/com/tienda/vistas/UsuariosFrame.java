package com.tienda.vistas;

import com.tienda.dao.UsuarioDAO;
import com.tienda.modelos.Usuario;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UsuariosFrame extends JFrame {

    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    
    private UsuarioDAO dao = new UsuarioDAO();
    private int idSeleccionado = -1;

    public UsuariosFrame() {
        setTitle("Gestión de Usuarios (Solo Admin)");
        setSize(900, 600);
        setLayout(null);
        setLocationRelativeTo(null);

        // --- FORMULARIO ---
        JLabel lblTitulo = new JLabel("Administrar Empleados");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 250, 30);
        add(lblTitulo);

        add(crearLabel("Nombre Completo:", 20, 60));
        txtNombre = crearInput(20, 85);
        add(txtNombre);

        add(crearLabel("Correo (Usuario):", 20, 125));
        txtEmail = crearInput(20, 150);
        add(txtEmail);

        add(crearLabel("Contraseña:", 20, 190));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(20, 215, 250, 30);
        add(txtPassword);

        add(crearLabel("Rol (Permisos):", 20, 255));
        cmbRol = new JComboBox<>();
        cmbRol.addItem("ADMINISTRADOR"); // Índice 0 -> ID 1
        cmbRol.addItem("CAJERO");        // Índice 1 -> ID 2
        cmbRol.setBounds(20, 280, 250, 30);
        add(cmbRol);

        // Botones
        JButton btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(20, 330, 115, 40);
        btnGuardar.setBackground(new Color(0, 153, 76));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarUsuario());
        add(btnGuardar);

        JButton btnModificar = new JButton("MODIFICAR");
        btnModificar.setBounds(145, 330, 115, 40);
        btnModificar.setBackground(new Color(255, 140, 0));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.addActionListener(e -> modificarUsuario());
        add(btnModificar);
        
        JButton btnEliminar = new JButton("ELIMINAR");
        btnEliminar.setBounds(20, 380, 240, 30);
        btnEliminar.setBackground(new Color(204, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarUsuario());
        add(btnEliminar);

        JButton btnLimpiar = new JButton("LIMPIAR / CANCELAR");
        btnLimpiar.setBounds(20, 420, 240, 30);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        add(btnLimpiar);

        // --- TABLA ---
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Email");
        modeloTabla.addColumn("Rol");
        modeloTabla.addColumn("ID Rol"); // Oculto

        tablaUsuarios = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBounds(300, 20, 560, 520);
        add(scroll);

        // Evento Clic
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) {
                    llenarFormulario(fila);
                }
            }
        });

        cargarTabla();
    }

    private void guardarUsuario() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String pass = new String(txtPassword.getPassword());
        
        // Mapeo simple: Si seleccionó índice 0 es Admin (1), si no es Cajero (2)
        int idRol = (cmbRol.getSelectedIndex() == 0) ? 1 : 2;

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        Usuario u = new Usuario(0, nombre, email, pass, idRol);
        if (dao.registrar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario Registrado");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Quizás el correo ya existe");
        }
    }

    private void modificarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero");
            return;
        }
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String pass = new String(txtPassword.getPassword());
        int idRol = (cmbRol.getSelectedIndex() == 0) ? 1 : 2;

        Usuario u = new Usuario(idSeleccionado, nombre, email, pass, idRol);
        if (dao.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario Modificado");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar");
        }
    }
    
    private void eliminarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar a este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Usuario Eliminado");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar (Puede que tenga ventas registradas)");
            }
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = dao.listar();
        for (Usuario u : lista) {
            Object[] fila = {
                u.getId(),
                u.getNombreCompleto(),
                u.getEmail(),
                (u.getIdRol() == 1 ? "ADMINISTRADOR" : "CAJERO"), // Texto bonito
                u.getIdRol()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void llenarFormulario(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtEmail.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtPassword.setText(""); // Por seguridad, no traemos la contraseña
        
        int idRol = (int) modeloTabla.getValueAt(fila, 4);
        if (idRol == 1) cmbRol.setSelectedIndex(0);
        else cmbRol.setSelectedIndex(1);
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(1); // Default Cajero
        idSeleccionado = -1;
        tablaUsuarios.clearSelection();
    }

    private JLabel crearLabel(String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setBounds(x, y, 200, 20);
        return l;
    }
    
    private JTextField crearInput(int x, int y) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 250, 30);
        return t;
    }
}