import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class IF_Venta extends JInternalFrame {

    public Connection Conectar;

    private JSplitPane DividirPanel;
    private JTextField CampoCodigoProducto;
    private DefaultTableModel TablaModelo;
    private JTable TablaDetalle;
    private JLabel EtiquetaTotalValor;

    // Constantes de índices de columna para el Ticket
    private static final int COL_ID = 0;
    private static final int COL_CANTIDAD = 2;
    private static final int COL_PRECIO = 3;
    private static final int COL_SUBTOTAL = 4;

    private double TotalGeneral = 0.00;
    private double DescuentoGeneral = 0.00;
    private final DecimalFormat FormatoDecimal = new DecimalFormat("#,##0.00");
    private final Font FuenteEtiqueta = new Font("Arial", Font.BOLD, 18);

    // Nombres de Procedimientos Almacenados
    private static final String INSERTAR_TICKET = "InsertarTicket";
    private static final String INSERTAR_DETALLE = "InsertarTicketDetalle";
    private static final String ACTUALIZAR_INVENTARIO = "ActualizarInventarioVenta";

    public IF_Venta(Connection Conexion, String Titulo) {
        super(Titulo, true, true, true, true);
        this.Conectar = Conexion;

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setClosable(true);
        setIconifiable(true);
        setResizable(true);

        InicializarComponentes();
        ConfigurarTablaModelo();
        ConfigurarEscuchadores();

        setSize(new Dimension(1100, 650));
    }

    protected boolean asegurarConexion() {
        try {
            if (this.Conectar == null || this.Conectar.isClosed()) {
                ConectarBD cb = new ConectarBD();
                this.Conectar = cb.Conexion();
            }
            return this.Conectar != null && !this.Conectar.isClosed();
        } catch (SQLException ex) {
            return this.Conectar != null;
        }
    }

    private void InicializarComponentes() {
        JPanel PanelPOS = new JPanel(new BorderLayout(5, 5));
        PanelPOS.setBackground(new Color(60, 60, 65));
        PanelPOS.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DividirPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        DividirPanel.setDividerSize(5);
        DividirPanel.setBorder(null);

        // Panel Buscador y Detalle del Ticket
        JPanel PanelTicket = new JPanel(new BorderLayout(5, 5));
        PanelTicket.setBackground(new Color(60, 60, 65));

        // Area de Búsqueda de Productos
        JPanel PanelBuscador = new JPanel(new BorderLayout(10, 10));
        PanelBuscador.setBackground(new Color(45, 45, 48));
        PanelBuscador.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        CampoCodigoProducto = new JTextField();
        CampoCodigoProducto.setFont(new Font("Arial", Font.PLAIN, 20));
        CampoCodigoProducto.setPreferredSize(new Dimension(200, 40));

        JButton BotonAgregar = new JButton("Agregar");
        BotonAgregar.setBackground(new Color(0, 120, 215));
        BotonAgregar.setForeground(Color.WHITE);
        BotonAgregar.setFont(new Font("Arial", Font.BOLD, 16));

        JButton BotonBuscar = new JButton("Buscar");
        BotonBuscar.setBackground(new Color(0, 150, 136));
        BotonBuscar.setForeground(Color.WHITE);
        BotonBuscar.setFont(new Font("Arial", Font.BOLD, 16));

        // Panel para contener los botones a la derecha del campo de búsqueda
        JPanel PanelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        PanelBotonesBusqueda.setBackground(new Color(45, 45, 48));
        PanelBotonesBusqueda.add(BotonBuscar);
        PanelBotonesBusqueda.add(BotonAgregar);

        PanelBuscador.add(new JLabel("Código/Nombre:") {
            {
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 16));
            }
        }, BorderLayout.WEST);
        PanelBuscador.add(CampoCodigoProducto, BorderLayout.CENTER);
        PanelBuscador.add(PanelBotonesBusqueda, BorderLayout.EAST);

        PanelTicket.add(PanelBuscador, BorderLayout.NORTH);

        // Tabla de Detalle del Ticket
        TablaDetalle = new JTable();
        JScrollPane BarraDesplazamiento = new JScrollPane(TablaDetalle);
        BarraDesplazamiento.getViewport().setBackground(new Color(40, 40, 40));
        BarraDesplazamiento.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        PanelTicket.add(BarraDesplazamiento, BorderLayout.CENTER);
        DividirPanel.setLeftComponent(PanelTicket);

        // Panel Totales y Opciones de Pago
        JPanel PanelTotales = new JPanel(new BorderLayout(10, 10));
        PanelTotales.setBackground(new Color(45, 45, 48));
        PanelTotales.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total
        JPanel PanelResumen = new JPanel(new BorderLayout(10, 10));
        PanelResumen.setBackground(new Color(45, 45, 48));

        JPanel PanelTotalDisplay = new JPanel(new BorderLayout(10, 10));
        PanelTotalDisplay.setBackground(new Color(45, 45, 48));

        JLabel EtiquetaTotalLabel = new JLabel("TOTAL:") {
            {
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 28));
            }
        };
        EtiquetaTotalValor = new JLabel("$0.00") {
            {
                setForeground(new Color(255, 255, 0));
                setFont(new Font("Arial", Font.BOLD, 30));
                setHorizontalAlignment(JLabel.RIGHT);
            }
        };

        PanelTotalDisplay.add(EtiquetaTotalLabel, BorderLayout.WEST);
        PanelTotalDisplay.add(EtiquetaTotalValor, BorderLayout.CENTER);
        PanelResumen.add(PanelTotalDisplay, BorderLayout.CENTER);
        PanelResumen.add(Box.createVerticalGlue(), BorderLayout.SOUTH);

        // Botones de Acción
        JPanel PanelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
        PanelBotones.setBackground(new Color(45, 45, 48));

        JButton BotonCobrar = CrearBotonAccion("Cobrar", new Color(76, 175, 80));
        JButton BotonBuscarCliente = CrearBotonAccion("Credito", new Color(0, 120, 215));
        JButton BotonEliminar = CrearBotonAccion("Eliminar", new Color(255, 152, 0));
        JButton BotonCancelar = CrearBotonAccion("Cancelar", new Color(244, 67, 54));

        PanelBotones.add(BotonCobrar);
        PanelBotones.add(BotonBuscarCliente);
        PanelBotones.add(BotonEliminar);
        PanelBotones.add(BotonCancelar);

        PanelTotales.add(PanelResumen, BorderLayout.NORTH);
        PanelTotales.add(PanelBotones, BorderLayout.SOUTH);
        PanelTotales.add(Box.createGlue(), BorderLayout.CENTER);

        DividirPanel.setRightComponent(PanelTotales);
        PanelPOS.add(DividirPanel, BorderLayout.CENTER);
        this.add(PanelPOS);

        SwingUtilities.invokeLater(() -> DividirPanel.setDividerLocation(850));

        // Asignación de Listeners a botones
        BotonAgregar.addActionListener(e -> MostrarBuscadorProductos());
        BotonBuscar.addActionListener(e -> {
            String criterio = CampoCodigoProducto.getText().trim();
            if (criterio.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese código o nombre de producto para buscar.", "Campo vacio",
                        JOptionPane.WARNING_MESSAGE);
                CampoCodigoProducto.requestFocusInWindow();
                return;
            }

            Object[] producto = buscarProducto(criterio);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado: " + criterio, "No encontrado",
                        JOptionPane.WARNING_MESSAGE);
                CampoCodigoProducto.setText("");
                CampoCodigoProducto.requestFocusInWindow();
                return;
            }

            int Clave = (int) producto[0];
            String Nombre = (String) producto[1];
            double Precio = (double) producto[2];

            String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad para: " + Nombre, "1");
            if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
                CampoCodigoProducto.setText("");
                CampoCodigoProducto.requestFocusInWindow();
                return;
            }
            try {
                double cantidad = Double.parseDouble(cantidadStr.trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Cantidad inválida",
                            JOptionPane.ERROR_MESSAGE);
                    CampoCodigoProducto.setText("");
                    CampoCodigoProducto.requestFocusInWindow();
                    return;
                }
                AgregarProductoAlTicket(Clave, Nombre, Precio, cantidad);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            CampoCodigoProducto.setText("");
            CampoCodigoProducto.requestFocusInWindow();
        });
        BotonCobrar.addActionListener(e -> CobrarTicket());
        BotonCancelar.addActionListener(e -> CancelarTicket());
        BotonEliminar.addActionListener(e -> EliminarProductoDelTicket());
        BotonBuscarCliente.addActionListener(e -> BuscarCliente());

        CampoCodigoProducto.addActionListener(e -> {
            String criterio = CampoCodigoProducto.getText().trim();
            if (criterio.isEmpty())
                return;
            Object[] producto = buscarProducto(criterio);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado: " + criterio, "No encontrado",
                        JOptionPane.WARNING_MESSAGE);
                CampoCodigoProducto.setText("");
                return;
            }
            int Clave = (int) producto[0];
            String Nombre = (String) producto[1];
            double Precio = (double) producto[2];

            String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad para: " + Nombre, "1");
            if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
                CampoCodigoProducto.setText("");
                return;
            }
            try {
                double cantidad = Double.parseDouble(cantidadStr.trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Cantidad inválida",
                            JOptionPane.ERROR_MESSAGE);
                    CampoCodigoProducto.setText("");
                    return;
                }
                AgregarProductoAlTicket(Clave, Nombre, Precio, cantidad);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            CampoCodigoProducto.setText("");
        });
    }

    private void ConfigurarTablaModelo() {
        String[] NombresColumnas = { "ID", "Producto", "Cantidad", "P. Unitario", "Subtotal" };
        TablaModelo = new DefaultTableModel(NombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int Fila, int Columna) {
                return Columna == COL_CANTIDAD;
            }

            @Override
            public Class<?> getColumnClass(int IndiceColumna) {
                if (IndiceColumna == COL_CANTIDAD || IndiceColumna == COL_PRECIO || IndiceColumna == COL_SUBTOTAL) {
                    return Double.class;
                }
                return super.getColumnClass(IndiceColumna);
            }
        };
        TablaDetalle.setModel(TablaModelo);

        // Estilos y ocultar ID
        TablaDetalle.setFont(new Font("Arial", Font.PLAIN, 16));
        TablaDetalle.setRowHeight(25);
        TablaDetalle.getTableHeader().setBackground(new Color(0, 120, 215));
        TablaDetalle.getTableHeader().setForeground(Color.WHITE);
        TablaDetalle.setBackground(new Color(40, 40, 40));
        TablaDetalle.setForeground(Color.WHITE);
        TablaDetalle.setSelectionBackground(new Color(0, 150, 136));
        TablaDetalle.setSelectionForeground(Color.WHITE);
        TablaDetalle.setRowHeight(35);
        TablaDetalle.getTableHeader().setFont(FuenteEtiqueta);
        TablaDetalle.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        TablaDetalle.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        TablaDetalle.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);

        DefaultTableCellRenderer RenderizadorDerecha = new DefaultTableCellRenderer();
        RenderizadorDerecha.setHorizontalAlignment(JLabel.RIGHT);

        DefaultTableCellRenderer RenderizadorDecimal = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText(FormatoDecimal.format(((Number) value).doubleValue()));
                } else if (value != null) {
                    setText(value.toString());
                } else {
                    setText("0.00");
                }
                setHorizontalAlignment(RIGHT);
                setForeground(Color.WHITE);
                setBackground(new Color(60, 60, 65));
            }
        };

        TablaDetalle.getColumnModel().getColumn(COL_CANTIDAD).setCellRenderer(RenderizadorDecimal);
        TablaDetalle.getColumnModel().getColumn(COL_PRECIO).setCellRenderer(RenderizadorDecimal);
        TablaDetalle.getColumnModel().getColumn(COL_SUBTOTAL).setCellRenderer(RenderizadorDecimal);
    }

    private void ConfigurarEscuchadores() {
        TablaModelo.addTableModelListener(e -> {
            if (e.getColumn() == COL_CANTIDAD && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int Fila = e.getFirstRow();
                ModificarCantidadDesdeTabla(Fila);
            }
        });
    }

    private JButton CrearBotonAccion(String Texto, Color Color) {
        JButton Boton = new JButton(Texto);
        Boton.setFont(new Font("Arial", Font.BOLD, 18));
        Boton.setBackground(Color);
        Boton.setForeground(java.awt.Color.WHITE);
        Boton.setFocusPainted(false);
        Boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Boton.setPreferredSize(new Dimension(0, 70));
        return Boton;
    }

    private void MostrarBuscadorProductos() {
        MostrarBuscadorProductos(null);
    }

    private void MostrarBuscadorProductos(String Filtro) {
        Frame Propietario = JOptionPane.getFrameForComponent(this);

        DialogoBuscadorPOS Buscador = new DialogoBuscadorPOS(
                Propietario,
                "Catálogo de Productos - POS",
                true,
                Conectar,
                this,
                Filtro);

        Buscador.setSize(900, 550);
        Buscador.setLocationRelativeTo(Propietario);
        Buscador.setVisible(true);
    }

    public void AgregarProductoAlTicket(int Clave, String Nombre, double Precio, double Cantidad) {
        int FilaExistente = -1;
        for (int i = 0; i < TablaModelo.getRowCount(); i++) {
            try {
                Object idObj = TablaModelo.getValueAt(i, COL_ID);
                int idVal = (idObj instanceof Number) ? ((Number) idObj).intValue()
                        : Integer.parseInt(idObj.toString());
                if (idVal == Clave) {
                    FilaExistente = i;
                    break;
                }
            } catch (Exception ex) {
            }
        }

        double CantidadBase = 0.0;
        if (FilaExistente != -1) {
            Object cantObj = TablaModelo.getValueAt(FilaExistente, COL_CANTIDAD);
            if (cantObj instanceof Number)
                CantidadBase = ((Number) cantObj).doubleValue();
            else
                try {
                    CantidadBase = Double.parseDouble(cantObj.toString());
                } catch (Exception ex) {
                    CantidadBase = 0.0;
                }
        }
        double NuevaCantidad = CantidadBase + Cantidad;

        if (!VerificarStockSuficiente(Clave, NuevaCantidad)) {
            return;
        }

        double Subtotal = NuevaCantidad * Precio;

        if (FilaExistente != -1) {
            TablaModelo.setValueAt(NuevaCantidad, FilaExistente, COL_CANTIDAD);
            TablaModelo.setValueAt(Subtotal, FilaExistente, COL_SUBTOTAL);
        } else {
            Object[] FilaNueva = { Clave, Nombre, NuevaCantidad, Precio, Subtotal };
            TablaModelo.addRow(FilaNueva);
        }

        RefrescarTotales();
        RefrescarTablaDetalle();
    }

    private void ModificarCantidadDesdeTabla(int Fila) {
        try {
            Object idObj = TablaModelo.getValueAt(Fila, COL_ID);
            int Clave = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());

            Object cantObj = TablaModelo.getValueAt(Fila, COL_CANTIDAD);
            double NuevaCantidad = (cantObj instanceof Number) ? ((Number) cantObj).doubleValue()
                    : Double.parseDouble(cantObj.toString());

            Object precObj = TablaModelo.getValueAt(Fila, COL_PRECIO);
            double Precio = (precObj instanceof Number) ? ((Number) precObj).doubleValue()
                    : Double.parseDouble(precObj.toString());

            if (NuevaCantidad <= 0.001) {
                TablaModelo.removeRow(Fila);
                RefrescarTotales();
                return;
            }

            if (!VerificarStockSuficiente(Clave, NuevaCantidad)) {
                RefrescarTotales();
                return;
            }

            double Subtotal = NuevaCantidad * Precio;
            TablaModelo.setValueAt(Subtotal, Fila, COL_SUBTOTAL);

            RefrescarTotales();

        } catch (Exception Excepcion) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida. Debe ser un número: " + Excepcion.getMessage(),
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
            RefrescarTotales();
        }
    }

    private void EliminarProductoDelTicket() {
        int FilaSeleccionada = TablaDetalle.getSelectedRow();
        if (FilaSeleccionada != -1) {
            TablaModelo.removeRow(FilaSeleccionada);
            RefrescarTotales();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void RefrescarTotales() {
        double NuevoSubtotal = 0.00;
        for (int i = 0; i < TablaModelo.getRowCount(); i++) {
            Object obj = TablaModelo.getValueAt(i, COL_SUBTOTAL);
            double subtotal = (obj instanceof Number) ? ((Number) obj).doubleValue() : 0.0;
            NuevoSubtotal += subtotal;
        }

        TotalGeneral = NuevoSubtotal;

        EtiquetaTotalValor.setText("$" + FormatoDecimal.format(TotalGeneral));
    }

    private void RefrescarTablaDetalle() {
        TablaDetalle.repaint();
        if (TablaModelo.getRowCount() > 0) {
            TablaDetalle.setRowSelectionInterval(TablaModelo.getRowCount() - 1, TablaModelo.getRowCount() - 1);
        }
    }

    private boolean VerificarStockSuficiente(int Clave, double CantidadRequerida) {
        if (CantidadRequerida <= 0)
            return true;

        // Si no hay conexión, permitimos la operación (modo demo)
        if (Conectar == null) {
            return true;
        }

        double StockActual = 0.0;
        String sql = "SELECT Inv_Prd FROM Productos WHERE Clav_Prd = ?";
        try (PreparedStatement ps = Conectar.prepareStatement(sql)) {
            ps.setInt(1, Clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockActual = rs.getDouble("Inv_Prd");
                } else {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado (clave: " + Clave + ").", "Error DB",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            if (StockActual >= CantidadRequerida) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente para " + Clave + ". Stock actual: " + FormatoDecimal.format(StockActual) +
                                " | Cantidad requerida: " + FormatoDecimal.format(CantidadRequerida) + ".",
                        "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar stock por SELECT: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error de BD al verificar stock. Venta abortada.", "Error DB",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void CancelarTicket() {
        int Confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar el ticket actual?",
                "Cancelar Ticket", JOptionPane.YES_NO_OPTION);

        if (Confirmacion == JOptionPane.YES_OPTION) {
            TablaModelo.setRowCount(0);
            RefrescarTotales();
            CampoCodigoProducto.setText("");
        }
    }

    // Helpers para extraer valores del modelo de forma segura y legible
    private int valorIntModelo(int fila, int columna) {
        Object obj = TablaModelo.getValueAt(fila, columna);
        if (obj instanceof Number num)
            return num.intValue();
        if (obj != null) {
            try {
                return Integer.parseInt(obj.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private double valorDoubleModelo(int fila, int columna) {
        Object obj = TablaModelo.getValueAt(fila, columna);
        if (obj instanceof Number num)
            return num.doubleValue();
        if (obj != null) {
            try {
                return Double.parseDouble(obj.toString());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // Busca un producto por clave o por nombre (primer resultado).
    private Object[] buscarProducto(String criterio) {
        if (criterio == null || criterio.trim().isEmpty())
            return null;
        criterio = criterio.trim();
        if (!asegurarConexion()) {
            return null;
        }

        try {
            try {
                int clave = Integer.parseInt(criterio);
                String sql = "SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd FROM Productos WHERE Clav_Prd = ? LIMIT 1";
                try (PreparedStatement ps = Conectar.prepareStatement(sql)) {
                    ps.setInt(1, clave);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new Object[] { rs.getInt("Clav_Prd"), rs.getString("Nom_Prd"),
                                    rs.getDouble("Pre_Prd"), rs.getDouble("Inv_Prd") };
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                // no es numérico -> buscar por nombre (LIKE)
                String sql = "SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd FROM Productos WHERE Nom_Prd LIKE ? ORDER BY Nom_Prd LIMIT 1";
                try (PreparedStatement ps = Conectar.prepareStatement(sql)) {
                    ps.setString(1, "%" + criterio + "%");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new Object[] { rs.getInt("Clav_Prd"), rs.getString("Nom_Prd"),
                                    rs.getDouble("Pre_Prd"), rs.getDouble("Inv_Prd") };
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar producto: " + ex.getMessage());
        }
        return null;
    }

    // Metodo para Cobrar Tickets
    private void CobrarTicket() {
        if (TablaModelo.getRowCount() == 0 || TotalGeneral <= 0) {
            JOptionPane.showMessageDialog(this, "Ticket vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String PagoStr = JOptionPane.showInputDialog(this,
                "Total: $" + FormatoDecimal.format(TotalGeneral) + "\nIngrese Monto:", "Cobrar",
                JOptionPane.QUESTION_MESSAGE);
        if (PagoStr == null || PagoStr.trim().isEmpty())
            return;

        try {
            double MontoPagado = Double.parseDouble(PagoStr);
            if (MontoPagado < TotalGeneral) {
                JOptionPane.showMessageDialog(this, "Monto insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double Cambio = MontoPagado - TotalGeneral;
            if (Conectar == null || Conectar.isClosed()) {
                JOptionPane.showMessageDialog(this, "Sin conexión a BD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Conectar.setAutoCommit(false);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al iniciar transacción: " + e.getMessage());
                return;
            }

            int ClaveTicketGenerada = -1;

            try {
                try (CallableStatement CStmtTicket = Conectar
                        .prepareCall("{ call " + INSERTAR_TICKET + "(?, ?, ?, ?, ?) }")) {
                    CStmtTicket.registerOutParameter(1, Types.INTEGER);
                    CStmtTicket.setDouble(2, TotalGeneral);
                    CStmtTicket.setDouble(3, DescuentoGeneral);
                    CStmtTicket.setString(4, "Si");
                    CStmtTicket.setInt(5, 0);
                    CStmtTicket.executeUpdate();
                    ClaveTicketGenerada = CStmtTicket.getInt(1);
                }

                for (int i = 0; i < TablaModelo.getRowCount(); i++) {
                    int ClavePrd = (int) TablaModelo.getValueAt(i, COL_ID);
                    double Cantidad = (double) TablaModelo.getValueAt(i, COL_CANTIDAD);
                    double Precio = (double) TablaModelo.getValueAt(i, COL_PRECIO);

                    try (CallableStatement CStmtDetalle = Conectar
                            .prepareCall("{ call " + INSERTAR_DETALLE + "(?, ?, ?, ?) }")) {
                        CStmtDetalle.setInt(1, ClaveTicketGenerada);
                        CStmtDetalle.setInt(2, ClavePrd);
                        CStmtDetalle.setDouble(3, Cantidad);
                        CStmtDetalle.setDouble(4, Precio);
                        CStmtDetalle.executeUpdate();
                    }
                    try (CallableStatement CStmtInv = Conectar
                            .prepareCall("{ call " + ACTUALIZAR_INVENTARIO + "(?, ?) }")) {
                        CStmtInv.setInt(1, ClavePrd);
                        CStmtInv.setDouble(2, Cantidad);
                        CStmtInv.executeUpdate();
                    }
                }
                Conectar.commit();

                JOptionPane.showMessageDialog(this, "Venta Exitosa!\nCambio: $" + FormatoDecimal.format(Cambio),
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
                TablaModelo.setRowCount(0);
                RefrescarTotales();
                CampoCodigoProducto.setText("");

            } catch (SQLException Ex) {
                try {
                    Conectar.rollback();
                } catch (SQLException e1) {
                }
                JOptionPane.showMessageDialog(this, "Error en BD: " + Ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    Conectar.setAutoCommit(true);
                } catch (SQLException e2) {
                }
            }

        } catch (NumberFormatException Ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + Ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método BuscarCliente
    private void BuscarCliente() {
        if (TablaModelo.getRowCount() == 0 || TotalGeneral <= 0) {
            JOptionPane.showMessageDialog(this, "El ticket está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Pedir Clave al usuario
        String Input = JOptionPane.showInputDialog(this, "Ingrese Clave del Cliente:", "Venta a Crédito",
                JOptionPane.QUESTION_MESSAGE);
        if (Input == null || Input.trim().isEmpty())
            return; // Si cancela, no hace nada

        try {
            int IdCliente = Integer.parseInt(Input.trim());
            // Validar si el cliente existe en la BD
            String NombreCliente = ValidarClienteEnBD(IdCliente);

            if (NombreCliente != null) {
                // Confirmar la operación con el usuario
                int Confirm = JOptionPane.showConfirmDialog(this,
                        "Cliente encontrado: " + NombreCliente + "\n¿Confirmar venta a crédito?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (Confirm == JOptionPane.YES_OPTION) {
                    RegistrarVentaCredito(IdCliente, NombreCliente);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Cliente con clave " + IdCliente + " no encontrado.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException Ex) {
            JOptionPane.showMessageDialog(this, "La clave debe ser un número entero.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodopara verificar existencia del cliente.
    private String ValidarClienteEnBD(int IdCliente) {
        String nombre = null;
        String sql = "SELECT CONCAT(Nom_Cr, ' ', Ape_Cr) FROM Credito WHERE Clav_Cr = ?";

        try (PreparedStatement ps = Conectar.prepareStatement(sql)) {
            ps.setInt(1, IdCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error validando cliente: " + ex.getMessage());
        }
        return nombre;
    }

    // Método que realiza la transacción en la base de datos.
    private void RegistrarVentaCredito(int IdCliente, String NombreCliente) {
        if (Conectar == null)
            return;
        try {
            Conectar.setAutoCommit(false);
            int ClaveTicketGenerada = -1;
            try (CallableStatement CStmtTicket = Conectar
                    .prepareCall("{ call " + INSERTAR_TICKET + "(?, ?, ?, ?, ?) }")) {
                CStmtTicket.registerOutParameter(1, Types.INTEGER);
                CStmtTicket.setDouble(2, TotalGeneral);
                CStmtTicket.setDouble(3, DescuentoGeneral);
                CStmtTicket.setString(4, "No");
                CStmtTicket.setInt(5, IdCliente);
                CStmtTicket.executeUpdate();
                ClaveTicketGenerada = CStmtTicket.getInt(1);
            }

            // Insertar Detalles y Actualizar Inventario
            for (int i = 0; i < TablaModelo.getRowCount(); i++) {
                int ClavePrd = (int) TablaModelo.getValueAt(i, COL_ID);
                double Cantidad = (double) TablaModelo.getValueAt(i, COL_CANTIDAD);
                double Precio = (double) TablaModelo.getValueAt(i, COL_PRECIO);

                // Insertar Detalle
                try (CallableStatement CStmtDetalle = Conectar
                        .prepareCall("{ call " + INSERTAR_DETALLE + "(?, ?, ?, ?) }")) {
                    CStmtDetalle.setInt(1, ClaveTicketGenerada);
                    CStmtDetalle.setInt(2, ClavePrd);
                    CStmtDetalle.setDouble(3, Cantidad);
                    CStmtDetalle.setDouble(4, Precio);
                    CStmtDetalle.executeUpdate();
                }

                // Actualizar Inventario
                try (CallableStatement CStmtInv = Conectar
                        .prepareCall("{ call " + ACTUALIZAR_INVENTARIO + "(?, ?) }")) {
                    CStmtInv.setInt(1, ClavePrd);
                    CStmtInv.setDouble(2, Cantidad);
                    CStmtInv.executeUpdate();
                }
            }

            Conectar.commit();

            JOptionPane.showMessageDialog(this,
                    "Venta a CRÉDITO registrada correctamente.\nCliente: " + NombreCliente + "\nTicket ID: "
                            + ClaveTicketGenerada,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            TablaModelo.setRowCount(0);
            RefrescarTotales();
            CampoCodigoProducto.setText("");

        } catch (SQLException Ex) {
            try {
                Conectar.rollback();
            } catch (SQLException e1) {
            }
            JOptionPane.showMessageDialog(this, "Error BD: " + Ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                Conectar.setAutoCommit(true);
            } catch (SQLException e2) {
            }
        }
    }

    // Clase: DialogoBuscadorPOS
    private class DialogoBuscadorPOS extends JDialog {

        private final Connection Conectar;
        private final IF_Venta PropietarioPOS;
        private JTable TablaProductos;
        private DefaultTableModel TablaModelo;
        private JTextField CampoBuscador;
        private final DecimalFormat FormatoDecimal2 = new DecimalFormat("#,##0.00");

        public DialogoBuscadorPOS(Frame Propietario, String Titulo, boolean Modal,
                Connection Conexion, IF_Venta POS, String FiltroInicial) {
            super(Propietario, Titulo, Modal);
            this.Conectar = Conexion;
            this.PropietarioPOS = POS;

            InicializarComponentes();
            CargarTodosProductos();

            // Buscar automáticamente
            if (FiltroInicial != null && !FiltroInicial.trim().isEmpty()) {
                CampoBuscador.setText(FiltroInicial);
                BuscarProductos(FiltroInicial);
            }

            CampoBuscador.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent Evento) {
                    BuscarProductos(CampoBuscador.getText());
                }
            });
        }

        private void InicializarComponentes() {
            this.setLayout(new BorderLayout(10, 10));
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            this.setResizable(true);

            // Panel Buscador
            JPanel PanelTop = new JPanel(new BorderLayout(5, 5));
            PanelTop.setBackground(new Color(0, 120, 215));
            PanelTop.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel EtiquetaTitulo = new JLabel("Productos");
            EtiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 20));
            EtiquetaTitulo.setForeground(Color.WHITE);
            PanelTop.add(EtiquetaTitulo, BorderLayout.NORTH);

            JPanel PanelBuscador = new JPanel(new BorderLayout(10, 5));
            PanelBuscador.setBackground(new Color(0, 120, 215));

            JLabel EtiquetaBuscador = new JLabel("Buscar:");
            EtiquetaBuscador.setFont(new Font("Arial", Font.BOLD, 14));
            EtiquetaBuscador.setForeground(Color.WHITE);
            PanelBuscador.add(EtiquetaBuscador, BorderLayout.WEST);

            CampoBuscador = new JTextField();
            CampoBuscador.setFont(new Font("Arial", Font.PLAIN, 16));
            CampoBuscador.setPreferredSize(new Dimension(300, 40));
            PanelBuscador.add(CampoBuscador, BorderLayout.CENTER);

            PanelTop.add(PanelBuscador, BorderLayout.SOUTH);
            this.add(PanelTop, BorderLayout.NORTH);

            // Panel Tabla de Productos
            String[] NombresColumnas = { "ID", "Producto", "Precio Unitario", "Existencia" };
            TablaModelo = new DefaultTableModel(NombresColumnas, 0) {
                @Override
                public boolean isCellEditable(int Fila, int Columna) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int IndiceColumna) {
                    switch (IndiceColumna) {
                        case 0:
                            return Integer.class;
                        case 1:
                            return String.class;
                        case 2:
                            return Double.class;
                        case 3:
                            return Double.class;
                        default:
                            return Object.class;
                    }
                }
            };

            TablaProductos = new JTable(TablaModelo);
            TablaProductos.setFont(new Font("Arial", Font.PLAIN, 14));
            TablaProductos.setRowHeight(30);
            TablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            TablaProductos.getTableHeader().setBackground(new Color(0, 120, 215));
            TablaProductos.getTableHeader().setForeground(Color.WHITE);
            TablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TablaProductos.setBackground(new Color(40, 40, 40));
            TablaProductos.setForeground(Color.WHITE);

            // Ocultar columna ID
            TablaProductos.getColumnModel().getColumn(0).setMaxWidth(0);
            TablaProductos.getColumnModel().getColumn(0).setMinWidth(0);
            TablaProductos.getColumnModel().getColumn(0).setPreferredWidth(0);

            // Alineación de columnas
            DefaultTableCellRenderer RenderizadorCentro = new DefaultTableCellRenderer();
            RenderizadorCentro.setHorizontalAlignment(JLabel.CENTER);
            DefaultTableCellRenderer RenderizadorDerecha = new DefaultTableCellRenderer();
            RenderizadorDerecha.setHorizontalAlignment(JLabel.RIGHT);

            // Renderizador de los valores numericos
            DefaultTableCellRenderer RenderizadorPrecio = new DefaultTableCellRenderer() {
                @Override
                protected void setValue(Object value) {
                    if (value instanceof Number) {
                        setText(FormatoDecimal2.format(((Number) value).doubleValue()));
                    } else if (value != null) {
                        setText(value.toString());
                    } else {
                        setText("0.00");
                    }
                    setHorizontalAlignment(JLabel.RIGHT);
                    setForeground(Color.WHITE);
                    setBackground(new Color(40, 40, 40));
                }
            };

            TablaProductos.getColumnModel().getColumn(2).setCellRenderer(RenderizadorPrecio); // Precio
            TablaProductos.getColumnModel().getColumn(3).setCellRenderer(RenderizadorPrecio); // Existencia

            // Listener para seleccionar producto
            TablaProductos.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent Evento) {
                    if (Evento.getClickCount() == 2) {
                        int Fila = TablaProductos.rowAtPoint(Evento.getPoint());
                        if (Fila >= 0) {
                            SeleccionarProducto(Fila);
                        }
                    }
                }
            });

            TablaProductos.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent Evento) {
                    if (Evento.getKeyCode() == KeyEvent.VK_ENTER) {
                        int Fila = TablaProductos.getSelectedRow();
                        if (Fila >= 0) {
                            SeleccionarProducto(Fila);
                        }
                    }
                }
            });

            JScrollPane ScrollPane = new JScrollPane(TablaProductos);
            ScrollPane.getViewport().setBackground(new Color(40, 40, 40));
            ScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            this.add(ScrollPane, BorderLayout.CENTER);

            // Panel Botones
            JPanel PanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            PanelBottom.setBackground(new Color(45, 45, 48));

            JButton BotonSeleccionar = new JButton("Seleccionar");
            BotonSeleccionar.setFont(new Font("Arial", Font.BOLD, 12));
            BotonSeleccionar.setBackground(new Color(76, 175, 80));
            BotonSeleccionar.setForeground(Color.WHITE);
            BotonSeleccionar.addActionListener(e -> {
                int Fila = TablaProductos.getSelectedRow();
                if (Fila >= 0) {
                    SeleccionarProducto(Fila);
                }
            });
            PanelBottom.add(BotonSeleccionar);

            JButton BotonCancelar = new JButton("Cancelar");
            BotonCancelar.setFont(new Font("Arial", Font.BOLD, 12));
            BotonCancelar.setBackground(new Color(244, 67, 54));
            BotonCancelar.setForeground(Color.WHITE);
            BotonCancelar.addActionListener(e -> this.dispose());
            PanelBottom.add(BotonCancelar);

            this.add(PanelBottom, BorderLayout.SOUTH);
        }

        private void CargarTodosProductos() {
            if (!PropietarioPOS.asegurarConexion()) {
                CargarProductosEjemplo();
                return;
            }

            TablaModelo.setRowCount(0);
            try {
                String SQL = "SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd FROM Productos ORDER BY Nom_Prd ASC";
                try (Statement ST = Conectar.createStatement();
                        ResultSet RS = ST.executeQuery(SQL)) {
                    while (RS.next()) {
                        int Id = RS.getInt("Clav_Prd");
                        String Nombre = RS.getString("Nom_Prd");
                        double Precio = RS.getDouble("Pre_Prd");
                        double Existencia = RS.getDouble("Inv_Prd");
                        TablaModelo.addRow(new Object[] { Id, Nombre, Precio, Existencia });
                    }
                }
            } catch (SQLException Excepcion) {
                System.err.println("Error al cargar productos: " + Excepcion.getMessage());
                CargarProductosEjemplo();
            }
        }

        private void BuscarProductos(String Criterio) {
            if (Criterio == null || Criterio.trim().isEmpty()) {
                CargarTodosProductos();
                return;
            }
            if (!PropietarioPOS.asegurarConexion()) {
                CargarProductosEjemplo();
                return;
            }

            TablaModelo.setRowCount(0);
            String criterioTrim = Criterio.trim();
            boolean criterioNumerico = false;
            int claveBuscada = -1;
            try {
                claveBuscada = Integer.parseInt(criterioTrim);
                criterioNumerico = true;
            } catch (NumberFormatException nf) {
                criterioNumerico = false;
            }

            try {
                if (criterioNumerico) {
                    String SQL = "SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd FROM Productos WHERE Clav_Prd = ? OR Nom_Prd LIKE ? ORDER BY Nom_Prd ASC";
                    try (PreparedStatement PS = Conectar.prepareStatement(SQL)) {
                        PS.setInt(1, claveBuscada);
                        PS.setString(2, "%" + criterioTrim + "%");
                        try (ResultSet RS = PS.executeQuery()) {
                            while (RS.next()) {
                                int Id = RS.getInt("Clav_Prd");
                                String Nombre = RS.getString("Nom_Prd");
                                double Precio = RS.getDouble("Pre_Prd");
                                double Existencia = RS.getDouble("Inv_Prd");
                                TablaModelo.addRow(new Object[] { Id, Nombre, Precio, Existencia });
                            }
                        }
                    }
                } else {
                    String SQL = "SELECT Clav_Prd, Nom_Prd, Pre_Prd, Inv_Prd FROM Productos WHERE Nom_Prd LIKE ? ORDER BY Nom_Prd ASC";
                    try (PreparedStatement PS = Conectar.prepareStatement(SQL)) {
                        PS.setString(1, "%" + criterioTrim + "%");
                        try (ResultSet RS = PS.executeQuery()) {
                            while (RS.next()) {
                                int Id = RS.getInt("Clav_Prd");
                                String Nombre = RS.getString("Nom_Prd");
                                double Precio = RS.getDouble("Pre_Prd");
                                double Existencia = RS.getDouble("Inv_Prd");
                                TablaModelo.addRow(new Object[] { Id, Nombre, Precio, Existencia });
                            }
                        }
                    }
                }
            } catch (SQLException Excepcion) {
                System.err.println("Error al buscar productos: " + Excepcion.getMessage());
            }
        }

        private void CargarProductosEjemplo() {
            TablaModelo.setRowCount(0);
            TablaModelo.addRow(new Object[] { 1, "Carne de Res - Carne Molida", 12.50, 45.0 });
            TablaModelo.addRow(new Object[] { 2, "Carne de Pollo - Pechuga", 8.00, 60.0 });
            TablaModelo.addRow(new Object[] { 3, "Carne de Cerdo - Chuleta", 10.00, 30.0 });
            TablaModelo.addRow(new Object[] { 4, "Carne de Res - Filete", 18.00, 20.0 });
            TablaModelo.addRow(new Object[] { 5, "Carne de Pollo - Muslo", 5.50, 75.0 });
        }

        private void SeleccionarProducto(int Fila) {
            if (Fila < 0)
                return;

            int Clave;
            String Nombre = "";
            double Precio = 0.0;
            try {
                Object idObj = TablaModelo.getValueAt(Fila, 0);
                if (idObj instanceof Number) {
                    Clave = ((Number) idObj).intValue();
                } else {
                    Clave = Integer.parseInt(idObj.toString());
                }

                Nombre = TablaModelo.getValueAt(Fila, 1).toString();

                Object priceObj = TablaModelo.getValueAt(Fila, 2);
                if (priceObj instanceof Number) {
                    Precio = ((Number) priceObj).doubleValue();
                } else if (priceObj != null) {
                    String PrecioStr = priceObj.toString().replace("$", "").replace(",", "").trim();
                    Precio = Double.parseDouble(PrecioStr);
                }
            } catch (Exception Ex) {
                JOptionPane.showMessageDialog(this, "Error al leer precio del producto: " + Ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String CantidadStr = JOptionPane.showInputDialog(this,
                    "Ingrese cantidad para:\n\n" + Nombre,
                    "1");

            if (CantidadStr == null || CantidadStr.trim().isEmpty()) {
                return;
            }

            try {
                double Cantidad = Double.parseDouble(CantidadStr);
                if (Cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PropietarioPOS.AgregarProductoAlTicket(Clave, Nombre, Precio, Cantidad);
                this.dispose();

            } catch (NumberFormatException Excepcion) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida. Ingrese un número", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}