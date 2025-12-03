package com.tienda.vistas;

import com.tienda.dao.ProductoDAO;
import com.tienda.modelos.Producto;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// JDialog significa que es una ventana hija (flotante)
public class BuscadorProductos extends JDialog {
    
    private JTextField txtBuscar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private ProductoDAO productoDAO = new ProductoDAO();
    
    // Esta variable guardará el código que el usuario elija para mandarlo a la otra ventana
    public String codigoSeleccionado = null;

    public BuscadorProductos(JFrame parent) {
        super(parent, "Buscar Producto", true); // true = Modal (bloquea la ventana de atrás)
        setSize(600, 450);
        setLayout(null);
        setLocationRelativeTo(parent);
        
        JLabel lblInfo = new JLabel("Escribe el nombre y haz DOBLE CLIC para seleccionar:");
        lblInfo.setBounds(20, 10, 400, 20);
        lblInfo.setForeground(Color.GRAY);
        add(lblInfo);

        txtBuscar = new JTextField();
        txtBuscar.setBounds(20, 35, 400, 30);
        txtBuscar.setToolTipText("Escribe ej: 'Coca'");
        add(txtBuscar);
        
        JButton btnBuscar = new JButton("BUSCAR");
        btnBuscar.setBounds(430, 35, 130, 30);
        btnBuscar.setBackground(new Color(33, 150, 243));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.addActionListener(e -> buscar());
        add(btnBuscar);

        // TABLA
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Stock");

        tablaResultados = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaResultados);
        scroll.setBounds(20, 80, 540, 310);
        add(scroll);

        // EVENTOS
        
        // 1. Buscar al escribir y dar Enter
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) buscar();
            }
        });

        // 2. Doble Clic para seleccionar
        tablaResultados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarProducto();
                }
            }
        });
        
        // Cargar todo al inicio
        buscar();
    }

    private void buscar() {
        String texto = txtBuscar.getText();
        modeloTabla.setRowCount(0);
        List<Producto> lista = productoDAO.buscarPorNombre(texto);
        
        for (Producto p : lista) {
            Object[] fila = { p.getCodigoBarras(), p.getNombre(), "$" + p.getPrecio(), p.getStock() };
            modeloTabla.addRow(fila);
        }
    }

    private void seleccionarProducto() {
        int fila = tablaResultados.getSelectedRow();
        if (fila >= 0) {
            // Obtenemos el código de la columna 0
            codigoSeleccionado = modeloTabla.getValueAt(fila, 0).toString();
            this.dispose(); // Cerramos la ventana
        }
    }
}