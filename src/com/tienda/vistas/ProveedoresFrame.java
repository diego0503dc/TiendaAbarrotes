package com.tienda.vistas;

import com.tienda.dao.ProveedorDAO;
import com.tienda.modelos.Proveedor;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProveedoresFrame extends JFrame {
    
    private JTextField txtEmpresa, txtContacto, txtTelefono, txtDireccion;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private ProveedorDAO dao = new ProveedorDAO();
    private int idSeleccionado = -1; // Guardará el ID del proveedor que estemos editando

    public ProveedoresFrame() {
        setTitle("Gestión de Proveedores");
        setSize(900, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        
        // --- SECCIÓN IZQUIERDA: FORMULARIO ---
        JLabel lblTitulo = new JLabel("Gestión Proveedores");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 200, 30);
        add(lblTitulo);

        add(crearLabel("Empresa:", 20, 60));
        txtEmpresa = crearInput(20, 85);
        add(txtEmpresa);

        add(crearLabel("Nombre Contacto:", 20, 125));
        txtContacto = crearInput(20, 150);
        add(txtContacto);

        add(crearLabel("Teléfono:", 20, 190));
        txtTelefono = crearInput(20, 215);
        add(txtTelefono);
        
        add(crearLabel("Dirección:", 20, 255));
        txtDireccion = crearInput(20, 280);
        add(txtDireccion);

        // Botón GUARDAR
        JButton btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(20, 330, 115, 40);
        btnGuardar.setBackground(new Color(0, 153, 76)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarProveedor());
        add(btnGuardar);
        
        // Botón MODIFICAR
        JButton btnModificar = new JButton("MODIFICAR");
        btnModificar.setBounds(145, 330, 115, 40);
        btnModificar.setBackground(new Color(255, 140, 0)); // Naranja
        btnModificar.setForeground(Color.WHITE);
        btnModificar.addActionListener(e -> modificarProveedor());
        add(btnModificar);
        
        // Botón LIMPIAR
        JButton btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setBounds(20, 380, 240, 30);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        add(btnLimpiar);

        // --- SECCIÓN DERECHA: TABLA ---
        
        modeloTabla = new DefaultTableModel() {
            @Override // Para que no puedan editar directo en la tabla
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Empresa");
        modeloTabla.addColumn("Contacto");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Dirección");

        tablaProveedores = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.setBounds(300, 20, 560, 520);
        add(scroll);

        // EVENTO: Al hacer clic en la tabla, llenar el formulario
        // EVENTO: Al hacer clic en la tabla, llenar el formulario (VERSIÓN CORREGIDA)
        tablaProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProveedores.getSelectedRow();
                if (fila >= 0) {
                    // Guardamos el ID
                    idSeleccionado = (int) modeloTabla.getValueAt(fila, 0); 
                    
                    // Para los textos, verificamos que no sean NULL antes de convertirlos
                    txtEmpresa.setText(obtenerTexto(fila, 1));
                    txtContacto.setText(obtenerTexto(fila, 2));
                    txtTelefono.setText(obtenerTexto(fila, 3));
                    txtDireccion.setText(obtenerTexto(fila, 4));
                }
            }
            // Método auxiliar para evitar errores si un campo está vacío en la BD
    private String obtenerTexto(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        if (valor == null) {
            return ""; // Si es null, devuelve vacío
        } else {
            return valor.toString(); // Si tiene datos, devuelve el texto
        }
    }
        });

        cargarTabla();
    }

    private void guardarProveedor() {
        String empresa = txtEmpresa.getText();
        String contacto = txtContacto.getText();
        String tel = txtTelefono.getText();
        String dir = txtDireccion.getText();

        if (empresa.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llene los campos obligatorios");
            return;
        }

        Proveedor p = new Proveedor(0, empresa, contacto, tel, dir);
        if (dao.registrar(p)) {
            JOptionPane.showMessageDialog(this, "¡Guardado!");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar");
        }
    }
    
    private void modificarProveedor() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor de la tabla primero");
            return;
        }
        
        String empresa = txtEmpresa.getText();
        String contacto = txtContacto.getText();
        String tel = txtTelefono.getText();
        String dir = txtDireccion.getText();

        // Creamos el proveedor CON el ID que seleccionamos
        Proveedor p = new Proveedor(idSeleccionado, empresa, contacto, tel, dir);
        
        if (dao.actualizar(p)) {
            JOptionPane.showMessageDialog(this, "¡Modificado con éxito!");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar");
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Proveedor> lista = dao.listar();
        for (Proveedor p : lista) {
            Object[] fila = { p.getId(), p.getEmpresa(), p.getContacto(), p.getTelefono(), p.getDireccion() };
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarFormulario() {
        txtEmpresa.setText("");
        txtContacto.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        idSeleccionado = -1; // Reiniciamos el ID para evitar errores
        tablaProveedores.clearSelection();
    }
    
    private JLabel crearLabel(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setBounds(x, y, 200, 20);
        return lbl;
    }
    
    private JTextField crearInput(int x, int y) {
        JTextField txt = new JTextField();
        txt.setBounds(x, y, 250, 30);
        return txt;
    }
}