package com.tienda.vistas;

import com.tienda.dao.ProductoDAO;
import com.tienda.dao.ProveedorDAO;
import com.tienda.modelos.Producto;
import com.tienda.modelos.Proveedor;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductosFrame extends JFrame {

    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock;
    private JComboBox<ComboItem> cmbProveedor; // La lista desplegable
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    
    private ProductoDAO productoDAO = new ProductoDAO();
    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private int idSeleccionado = -1;

    public ProductosFrame() {
        setTitle("Gestión de Productos");
        setSize(1000, 650); // Un poco más ancha
        setLayout(null);
        setLocationRelativeTo(null);

        // --- SECCIÓN IZQUIERDA: FORMULARIO ---
        JLabel lblTitulo = new JLabel("Inventario de Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 250, 30);
        add(lblTitulo);

        // Campos
        add(crearLabel("Código Barras:", 20, 60));
        txtCodigo = crearInput(20, 85);
        add(txtCodigo);

        add(crearLabel("Nombre Producto:", 20, 125));
        txtNombre = crearInput(20, 150);
        add(txtNombre);

        add(crearLabel("Precio ($):", 20, 190));
        txtPrecio = crearInput(20, 215);
        add(txtPrecio);

        add(crearLabel("Stock (Cantidad):", 20, 255));
        txtStock = crearInput(20, 280);
        add(txtStock);

        add(crearLabel("Proveedor:", 20, 320));
        cmbProveedor = new JComboBox<>();
        cmbProveedor.setBounds(20, 345, 250, 30);
        add(cmbProveedor);

        // Botones
        JButton btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(20, 400, 115, 40);
        btnGuardar.setBackground(new Color(0, 153, 76));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarProducto());
        add(btnGuardar);

        JButton btnModificar = new JButton("MODIFICAR");
        btnModificar.setBounds(145, 400, 115, 40);
        btnModificar.setBackground(new Color(255, 140, 0));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.addActionListener(e -> modificarProducto());
        add(btnModificar);
        
        JButton btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setBounds(20, 450, 240, 30);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        add(btnLimpiar);

        // --- SECCIÓN DERECHA: TABLA ---
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        modeloTabla.addColumn("ID"); // 0
        modeloTabla.addColumn("Código"); // 1
        modeloTabla.addColumn("Nombre"); // 2
        modeloTabla.addColumn("Precio"); // 3
        modeloTabla.addColumn("Stock"); // 4
        modeloTabla.addColumn("Proveedor"); // 5 (Nombre)
        modeloTabla.addColumn("ID Prov"); // 6 (Oculto visualmente, útil para lógica)

        tablaProductos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBounds(300, 20, 660, 570);
        add(scroll);

        // EVENTO CLIC EN TABLA (Con protección Anti-Error)
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.getSelectedRow();
                if (fila >= 0) {
                    llenarFormularioDesdeTabla(fila);
                }
            }
        });

        // CARGA INICIAL
        cargarComboProveedores();
        cargarTabla();
    }

    // --- MÉTODOS LÓGICOS ---

    private void cargarComboProveedores() {
        cmbProveedor.removeAllItems();
        List<Proveedor> lista = proveedorDAO.listar();
        
        for (Proveedor p : lista) {
            // Usamos la clase truco 'ComboItem' para guardar ID y Nombre juntos
            cmbProveedor.addItem(new ComboItem(p.getId(), p.getEmpresa()));
        }
    }

    private void guardarProducto() {
        try {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            double precio = Double.parseDouble(txtPrecio.getText()); // Puede fallar si no es numero
            int stock = Integer.parseInt(txtStock.getText());
            
            // Obtenemos el ID del proveedor seleccionado
            ComboItem itemSeleccionado = (ComboItem) cmbProveedor.getSelectedItem();
            int idProv = itemSeleccionado.getId();

            Producto p = new Producto(0, codigo, nombre, precio, stock, idProv);
            
            if (productoDAO.registrar(p)) {
                JOptionPane.showMessageDialog(this, "Producto Registrado!");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El Precio y Stock deben ser números válidos");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique los datos");
        }
    }
    
    private void modificarProducto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto primero");
            return;
        }
        try {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            
            ComboItem itemSeleccionado = (ComboItem) cmbProveedor.getSelectedItem();
            int idProv = itemSeleccionado.getId();

            Producto p = new Producto(idSeleccionado, codigo, nombre, precio, stock, idProv);
            
            if (productoDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Producto Modificado!");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en los datos numéricos");
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Producto> lista = productoDAO.listar();
        for (Producto p : lista) {
            Object[] fila = {
                p.getId(),
                p.getCodigoBarras(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getNombreProveedor(), // Nombre de la empresa (Coca-Cola)
                p.getIdProveedor()      // ID oculto (1)
            };
            modeloTabla.addRow(fila);
        }
    }

    private void llenarFormularioDesdeTabla(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtCodigo.setText(obtenerTexto(fila, 1));
        txtNombre.setText(obtenerTexto(fila, 2));
        txtPrecio.setText(obtenerTexto(fila, 3));
        txtStock.setText(obtenerTexto(fila, 4));
        
        // Seleccionar el proveedor correcto en el Combo
        int idProvTabla = (int) modeloTabla.getValueAt(fila, 6);
        
        // Recorrer el combo para encontrar cual tiene ese ID
        for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
            ComboItem item = cmbProveedor.getItemAt(i);
            if (item.getId() == idProvTabla) {
                cmbProveedor.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        if(cmbProveedor.getItemCount() > 0) cmbProveedor.setSelectedIndex(0);
        idSeleccionado = -1;
        tablaProductos.clearSelection();
    }
    
    private String obtenerTexto(int fila, int col) {
        Object val = modeloTabla.getValueAt(fila, col);
        return (val == null) ? "" : val.toString();
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
    
    // --- CLASE INTERNA PARA EL COMBOBOX (EL TRUCO) ---
    // Esto permite guardar ID (1) y Nombre ("Coca-Cola") juntos
    class ComboItem {
        private int id;
        private String texto;

        public ComboItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return texto; // Esto es lo que se ve en la lista desplegable
        }
    }
}