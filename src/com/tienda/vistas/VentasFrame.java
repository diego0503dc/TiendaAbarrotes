package com.tienda.vistas;

import com.tienda.dao.ProductoDAO;
import com.tienda.dao.VentaDAO;
import com.tienda.modelos.DetalleVenta;
import com.tienda.modelos.Producto;
import com.tienda.modelos.Usuario;
import com.tienda.modelos.Venta;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentasFrame extends JFrame {
    
    private JTextField txtCodigo, txtCantidad;
    private JLabel lblTotal, lblNombreProd;
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    
    private ProductoDAO productoDAO = new ProductoDAO();
    private VentaDAO ventaDAO = new VentaDAO();
    private Usuario usuarioActual; // El cajero
    
    // Lista temporal para guardar lo que el cliente lleva antes de pagar
    private List<DetalleVenta> listaDetalles = new ArrayList<>();
    private double totalVenta = 0.0;

    public VentasFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        
        setTitle("Caja Registradora - Cajero: " + usuario.getNombreCompleto());
        setSize(1100, 700);
        setLayout(null);
        setLocationRelativeTo(null);
        
        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Punto de Venta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(20, 20, 200, 30);
        add(lblTitulo);
        
        // --- ZONA DE ESCANEO (MODIFICADA) ---
        JPanel panelEscaneo = new JPanel();
        panelEscaneo.setLayout(null);
        panelEscaneo.setBounds(20, 70, 420, 200); // Lo hice un poquito más ancho
        panelEscaneo.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        add(panelEscaneo);
        
        JLabel lblCod = new JLabel("Cód. Barras (Enter):");
        lblCod.setBounds(20, 30, 150, 20);
        panelEscaneo.add(lblCod);
        
        txtCodigo = new JTextField();
        txtCodigo.setBounds(20, 55, 180, 35); // Hice la caja más chica para que quepa el botón
        txtCodigo.setFont(new Font("Arial", Font.BOLD, 14));
        panelEscaneo.add(txtCodigo);
        
        // --- NUEVO BOTÓN DE LUPA 🔍 ---
        JButton btnBuscarProd = new JButton("🔍");
        btnBuscarProd.setBounds(205, 55, 50, 35);
        btnBuscarProd.setToolTipText("Buscar producto por nombre");
        btnBuscarProd.addActionListener(e -> abrirBuscador());
        panelEscaneo.add(btnBuscarProd);
        // ------------------------------
        
        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setBounds(290, 30, 80, 20);
        panelEscaneo.add(lblCant);
        
        txtCantidad = new JTextField("1");
        txtCantidad.setBounds(290, 55, 80, 35);
        txtCantidad.setHorizontalAlignment(JTextField.CENTER);
        panelEscaneo.add(txtCantidad);
        
        // Label para mostrar qué producto se acaba de escanear
        lblNombreProd = new JLabel("Esperando producto...");
        lblNombreProd.setFont(new Font("Arial", Font.ITALIC, 14));
        lblNombreProd.setForeground(Color.GRAY);
        lblNombreProd.setBounds(20, 150, 350, 20);
        panelEscaneo.add(lblNombreProd);

        // --- ZONA DERECHA: TABLA Y TOTALES ---
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Precio Unit.");
        modeloTabla.addColumn("Cant.");
        modeloTabla.addColumn("Subtotal");
        
        tablaVentas = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBounds(450, 20, 600, 500);
        add(scroll);
        
        JLabel lblTextoTotal = new JLabel("TOTAL A PAGAR:");
        lblTextoTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblTextoTotal.setBounds(450, 540, 200, 30);
        add(lblTextoTotal);
        
        lblTotal = new JLabel("$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 40));
        lblTotal.setForeground(new Color(138, 43, 226));
        lblTotal.setBounds(700, 530, 350, 50);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblTotal);
        
        JButton btnCobrar = new JButton("COBRAR VENTA");
        btnCobrar.setBounds(800, 600, 250, 50);
        btnCobrar.setBackground(new Color(75, 0, 130)); // Indigo fuerte
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 18));
        btnCobrar.addActionListener(e -> realizarCobro());
        add(btnCobrar);

        // --- EVENTO DE TECLADO (ENTER) ---
        // Esto permite que al dar ENTER en la caja de código, se busque solo
        txtCodigo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarYAgregar();
                }
            }
        });
    }

    private void buscarYAgregar() {
        String codigo = txtCodigo.getText();
        if (codigo.isEmpty()) {
            txtCodigo.requestFocus();
            return;
        }

        Producto p = productoDAO.buscarPorCodigo(codigo);
        
        if (p != null) {
            // Verificar Stock
            int cantidadSolicitada = Integer.parseInt(txtCantidad.getText());
            if (p.getStock() >= cantidadSolicitada) {
                agregarAlaLista(p, cantidadSolicitada);
                txtCodigo.setText(""); // Limpiar para el siguiente
                txtCantidad.setText("1");
                txtCodigo.requestFocus(); // Volver el foco para seguir escaneando rápido
            } else {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Solo hay: " + p.getStock());
            }
        } else {
            lblNombreProd.setText("PRODUCTO NO ENCONTRADO ❌");
            lblNombreProd.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Código no encontrado");
        }
    }

    private void agregarAlaLista(Producto p, int cantidad) {
        // Verificar si ya estaba en la lista para solo sumar cantidad
        boolean yaExiste = false;
        for (DetalleVenta d : listaDetalles) {
            if (d.getIdProducto() == p.getId()) {
                d.setCantidad(d.getCantidad() + cantidad);
                d.setSubtotal(d.getCantidad() * d.getPrecioUnitario());
                yaExiste = true;
                break;
            }
        }

        if (!yaExiste) {
            DetalleVenta detalle = new DetalleVenta(p.getId(), p.getNombre(), cantidad, p.getPrecio());
            listaDetalles.add(detalle);
            lblNombreProd.setText("Último: " + p.getNombre());
            lblNombreProd.setForeground(new Color(0, 100, 0));
        }
        
        actualizarTablaYTotal();
    }

    private void actualizarTablaYTotal() {
        modeloTabla.setRowCount(0);
        totalVenta = 0.0;
        
        for (DetalleVenta d : listaDetalles) {
            Object[] fila = {
                d.getNombreProducto(),
                "$" + d.getPrecioUnitario(),
                d.getCantidad(),
                "$" + d.getSubtotal()
            };
            modeloTabla.addRow(fila);
            totalVenta += d.getSubtotal();
        }
        
        lblTotal.setText("$ " + String.format("%.2f", totalVenta));
    }

    private void realizarCobro() {
        if (listaDetalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta");
            return;
        }

        // Crear el objeto Venta
        Venta venta = new Venta();
        venta.setTotal(totalVenta);
        venta.setIdUsuario(usuarioActual.getId());
        
        // Guardar en BD (Aquí se descuenta el stock automágicamente)
        if (ventaDAO.registrarVenta(venta, listaDetalles)) {
            JOptionPane.showMessageDialog(this, "¡Venta Realizada con Éxito! \nTotal: $" + totalVenta);
            limpiarTodo();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la venta. Intente de nuevo.");
        }
    }

    private void limpiarTodo() {
        listaDetalles.clear();
        actualizarTablaYTotal();
        txtCodigo.setText("");
        lblNombreProd.setText("Esperando producto...");
        txtCodigo.requestFocus();
    }
    // Método para abrir la ventanita de búsqueda
    private void abrirBuscador() {
        BuscadorProductos buscador = new BuscadorProductos(this);
        buscador.setVisible(true);
        
        // Cuando la ventana se cierre, verificamos si seleccionó algo
        if (buscador.codigoSeleccionado != null) {
            txtCodigo.setText(buscador.codigoSeleccionado);
            txtCantidad.requestFocus(); // Ponemos el foco en cantidad o hacemos clic automático
            buscarYAgregar(); // Opcional: Si quieres que se agregue solo al volver
        }
    }
}