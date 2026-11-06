import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class IF_Producto extends JInternalFrame {
    
    private static final String SP_BUSCAR = "ObtenerProductos"; 
    private static final String SP_DATOS = "ObtenerDatosProducto";
    private static final String SP_ACTUALIZAR = "ActualizarProducto";
    
    public Connection ConexionBD; 
    
    // Componentes de la interfaz
    private JTable TablaProductos;
    private DefaultTableModel ModeloTabla;
    private JTextField CampoBuscador;
    
    // Componentes del Panel de Modificación
    private JPanel PanelModificacion;
    private JLabel EtiquetaNombreProducto;
    private JTextField CampoInventario;
    private JTextField CampoCosto;
    private JTextField CampoPrecioVenta;
    private JLabel EtiquetaTipoProductoValor;
    private JButton BotonCancelar;
    private JButton BotonGuardar;
    
    // Variables de estado
    private int ClaveProductoSeleccionada = -1;
    private final DecimalFormat FormatoDecimal = new DecimalFormat("#,##0.00");
    private final DecimalFormat FormatoInventario = new DecimalFormat("#,##0.000");

    // Fuentes personalizadas
    private final Font FuenteTitulo = new Font("Arial", Font.BOLD, 24);
    private final Font FuenteEtiqueta = new Font("Arial", Font.BOLD, 18);
    private final Font FuenteValor = new Font("Arial", Font.PLAIN, 18);
    private final Font FuenteInput = new Font("Arial", Font.PLAIN, 20);
    private final Font FuenteBoton = new Font("Arial", Font.BOLD, 18);

    public IF_Producto(Connection Conexion) {
        super("Gestión de Productos e Inventario", true, true, true, true);
        this.ConexionBD = Conexion;
        
        this.setMaximizable(true); 
        this.setClosable(true);
        this.setIconifiable(true); 
        this.setResizable(true);
        this.setBorder(null); 
        
        InicializarComponentes();
        ConfigurarTablaModelo();
        ConfigurarEscuchadores();

        CargarProductos(null);
        
        this.setSize(1200, 700);
        this.setVisible(true);
    }

    private void InicializarComponentes() {
        this.setLayout(new BorderLayout());
        
        JPanel PanelPrincipal = new JPanel(new BorderLayout(5, 5));
        PanelPrincipal.setBackground(new Color(60, 60, 65));
        PanelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(PanelPrincipal, BorderLayout.CENTER);

        JSplitPane SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        SplitPane.setResizeWeight(0.60); 
        SplitPane.setDividerSize(5);
        SplitPane.setBorder(null);
        PanelPrincipal.add(SplitPane, BorderLayout.CENTER);

        // Panel Lista y Buscador
        JPanel PanelLista = new JPanel(new BorderLayout(5, 5));
        PanelLista.setBackground(new Color(60, 60, 65));
        
        // Panel Buscador
        JPanel PanelBuscador = new JPanel(new BorderLayout(10, 10));
        PanelBuscador.setBackground(new Color(45, 45, 48));
        PanelBuscador.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel EtiquetaBuscador = new JLabel("Buscar Producto:");
        EtiquetaBuscador.setForeground(Color.WHITE);
        EtiquetaBuscador.setFont(FuenteEtiqueta);
        
        CampoBuscador = new JTextField();
        CampoBuscador.setFont(new Font("Arial", Font.PLAIN, 20));
        CampoBuscador.setBackground(Color.WHITE);
        CampoBuscador.setForeground(Color.BLACK);
        CampoBuscador.setCaretColor(Color.BLACK);
        CampoBuscador.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        CampoBuscador.setPreferredSize(new Dimension(200, 45));
        
        PanelBuscador.add(EtiquetaBuscador, BorderLayout.WEST);
        PanelBuscador.add(CampoBuscador, BorderLayout.CENTER);
        PanelLista.add(PanelBuscador, BorderLayout.NORTH);

        // Tabla
        TablaProductos = new JTable();
        TablaProductos.setFont(FuenteValor); 
        TablaProductos.setRowHeight(35);     
        TablaProductos.getTableHeader().setFont(FuenteEtiqueta);
        TablaProductos.getTableHeader().setBackground(new Color(0, 120, 215));
        TablaProductos.getTableHeader().setForeground(Color.WHITE);
        TablaProductos.setBackground(new Color(40, 40, 40));
        TablaProductos.setForeground(Color.WHITE);
        TablaProductos.setSelectionBackground(new Color(0, 150, 136));
        TablaProductos.setSelectionForeground(Color.WHITE);
        
        JScrollPane ScrollPane = new JScrollPane(TablaProductos);
        ScrollPane.getViewport().setBackground(new Color(40, 40, 40));
        ScrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 48)));
        PanelLista.add(ScrollPane, BorderLayout.CENTER);
        
        SplitPane.setLeftComponent(PanelLista);

        // Panel Modificación de Producto
        PanelModificacion = new JPanel(new BorderLayout(10, 10)); 
        PanelModificacion.setBackground(new Color(45, 45, 48));
        PanelModificacion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel EtiquetaTituloDetalles = new JLabel("Descripción", SwingConstants.CENTER);
        EtiquetaTituloDetalles.setFont(FuenteTitulo);
        EtiquetaTituloDetalles.setForeground(new Color(0, 120, 215));
        PanelModificacion.add(EtiquetaTituloDetalles, BorderLayout.NORTH);

        JPanel PanelFormulario = new JPanel(new GridBagLayout());
        PanelFormulario.setBackground(new Color(45, 45, 48));
        GridBagConstraints Gbc = new GridBagConstraints();
        Gbc.insets = new Insets(15, 10, 15, 10);
        Gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nombre
        Gbc.gridx = 0; Gbc.gridy = 0; Gbc.weightx = 0;
        PanelFormulario.add(CrearEtiqueta("Nombre:"), Gbc);
        Gbc.gridx = 1; Gbc.weightx = 1;
        EtiquetaNombreProducto = new JLabel("Seleccione un producto...");
        EtiquetaNombreProducto.setFont(FuenteEtiqueta);
        EtiquetaNombreProducto.setForeground(Color.WHITE);
        PanelFormulario.add(EtiquetaNombreProducto, Gbc);
        
        // Tipo
        Gbc.gridx = 0; Gbc.gridy = 1;
        PanelFormulario.add(CrearEtiqueta("Tipo:"), Gbc);
        Gbc.gridx = 1;
        EtiquetaTipoProductoValor = CrearEtiqueta("N/A");
        EtiquetaTipoProductoValor.setForeground(new Color(0, 120, 215));
        PanelFormulario.add(EtiquetaTipoProductoValor, Gbc);

        // Separador
        Gbc.gridx = 0; Gbc.gridy = 2; Gbc.gridwidth = 2;
        JSeparator Separator = new JSeparator();
        Separator.setBackground(new Color(70, 70, 75));
        PanelFormulario.add(Separator, Gbc);
        Gbc.gridwidth = 1;

        // Inventario
        Gbc.gridx = 0; Gbc.gridy = 3;
        PanelFormulario.add(CrearEtiqueta("Inventario (kg):"), Gbc);
        Gbc.gridx = 1;
        CampoInventario = CrearCampoTexto();
        PanelFormulario.add(CampoInventario, Gbc);
        
        // Costo
        Gbc.gridx = 0; Gbc.gridy = 4;
        PanelFormulario.add(CrearEtiqueta("Costo ($):"), Gbc);
        Gbc.gridx = 1;
        CampoCosto = CrearCampoTexto();
        PanelFormulario.add(CampoCosto, Gbc);

        // Precio de Venta
        Gbc.gridx = 0; Gbc.gridy = 5;
        PanelFormulario.add(CrearEtiqueta("Precio Venta ($):"), Gbc);
        Gbc.gridx = 1;
        CampoPrecioVenta = CrearCampoTexto();
        PanelFormulario.add(CampoPrecioVenta, Gbc);
        
        JPanel PanelCentro = new JPanel(new BorderLayout());
        PanelCentro.setBackground(new Color(45, 45, 48));
        PanelCentro.add(PanelFormulario, BorderLayout.NORTH);
        PanelModificacion.add(PanelCentro, BorderLayout.CENTER);
        
        JPanel PanelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        PanelBotones.setBackground(new Color(45, 45, 48));
        
        BotonCancelar = CrearBoton("Cancelar", new Color(244, 67, 54)); 
        BotonGuardar = CrearBoton("Guardar Cambios", new Color(76, 175, 80)); 
        
        PanelBotones.add(BotonCancelar);
        PanelBotones.add(BotonGuardar);
        PanelModificacion.add(PanelBotones, BorderLayout.SOUTH);
        
        SplitPane.setRightComponent(PanelModificacion);
        
        SetModificacionEnabled(false);
        
        SwingUtilities.invokeLater(() -> SplitPane.setDividerLocation(0.65));
    }
    
    private void ConfigurarTablaModelo() {
        String[] NombresColumnas = {"ID", "Producto", "Precio ($)", "Inventario"};
        ModeloTabla = new DefaultTableModel(NombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                return String.class;
            }
        };
        TablaProductos.setModel(ModeloTabla);
        
        // Ocultar ID
        TablaProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        TablaProductos.getColumnModel().getColumn(0).setMinWidth(0);
        TablaProductos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Renderizado
        DefaultTableCellRenderer RendererDerecha = new DefaultTableCellRenderer();
        RendererDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        RendererDerecha.setBackground(new Color(40, 40, 40));
        RendererDerecha.setForeground(Color.WHITE);
        
        TablaProductos.getColumnModel().getColumn(2).setCellRenderer(RendererDerecha);
        TablaProductos.getColumnModel().getColumn(3).setCellRenderer(RendererDerecha);
    }

    private void ConfigurarEscuchadores() {
        CampoBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                CargarProductos(CampoBuscador.getText());
            }
        });
        
        TablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TablaProductos.getSelectedRow() != -1) {
                SeleccionarProducto(TablaProductos.getSelectedRow());
            }
        });
        
        BotonCancelar.addActionListener(e -> CancelarModificacion());
        BotonGuardar.addActionListener(e -> GuardarCambios());
    }
    
    private JLabel CrearEtiqueta(String Texto) {
        JLabel Etiqueta = new JLabel(Texto);
        Etiqueta.setForeground(Color.LIGHT_GRAY);
        Etiqueta.setFont(FuenteEtiqueta);
        return Etiqueta;
    }
    
    private JTextField CrearCampoTexto() {
        JTextField Campo = new JTextField();
        Campo.setFont(FuenteInput);
        Campo.setBackground(new Color(55, 55, 60));
        Campo.setForeground(Color.WHITE);
        Campo.setCaretColor(Color.WHITE);
        Campo.setPreferredSize(new Dimension(100, 40));
        Campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return Campo;
    }
    
    private JButton CrearBoton(String Texto, Color ColorFondo) {
        JButton Boton = new JButton(Texto);
        Boton.setFont(FuenteBoton);
        Boton.setBackground(ColorFondo);
        Boton.setForeground(Color.WHITE);
        Boton.setFocusPainted(false);
        Boton.setBorderPainted(false);
        Boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Boton.setPreferredSize(new Dimension(200, 50));
        return Boton;
    }

    private void SetModificacionEnabled(boolean Estado) {
        CampoInventario.setEnabled(Estado);
        CampoCosto.setEnabled(Estado);
        CampoPrecioVenta.setEnabled(Estado);
        BotonGuardar.setEnabled(Estado);
        BotonCancelar.setEnabled(Estado);
        
        if (!Estado) {
            EtiquetaNombreProducto.setForeground(Color.GRAY);
            EtiquetaTipoProductoValor.setForeground(Color.GRAY);
        } else {
            EtiquetaNombreProducto.setForeground(Color.WHITE);
            EtiquetaTipoProductoValor.setForeground(new Color(0, 120, 215));
        }
    }

    private void CargarProductos(String Filtro) {
        ModeloTabla.setRowCount(0);
        String FiltroParam = (Filtro == null || Filtro.trim().isEmpty()) ? null : Filtro.trim();
        try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_BUSCAR + "(?)}")) { 
            
            if (FiltroParam == null) {
                CS.setNull(1, Types.VARCHAR);
            } else {
                CS.setString(1, FiltroParam);
            }
            
            try (ResultSet RS = CS.executeQuery()) {
                while (RS.next()) {
                    ModeloTabla.addRow(new Object[]{
                        RS.getInt("Clav_Prd"),
                        RS.getString("Nom_Prd"),
                        FormatoDecimal.format(RS.getDouble("Pre_Prd")),
                        FormatoInventario.format(RS.getDouble("Inv_Prd"))
                    });
                }
            }
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void SeleccionarProducto(int Fila) {
        ClaveProductoSeleccionada = (int) ModeloTabla.getValueAt(Fila, 0);
        
        try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_DATOS + "(?)}")) {
            CS.setInt(1, ClaveProductoSeleccionada);
            
            try (ResultSet RS = CS.executeQuery()) {
                if (RS.next()) {
                    EtiquetaNombreProducto.setText(RS.getString("Nom_Prd"));
                    EtiquetaTipoProductoValor.setText(RS.getString("Nom_TiP"));
                    
                    CampoInventario.setText(FormatoInventario.format(RS.getDouble("Inv_Prd")));
                    CampoCosto.setText(FormatoDecimal.format(RS.getDouble("Cos_Prd")));
                    CampoPrecioVenta.setText(FormatoDecimal.format(RS.getDouble("Pre_Prd")));
                    
                    SetModificacionEnabled(true);
                    CampoInventario.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    CancelarModificacion();
                }
            }
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalles: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            CancelarModificacion();
        }
    }

    private void GuardarCambios() {
        if (ClaveProductoSeleccionada == -1) return;

        try {
            double NuevoInventario = Double.parseDouble(CampoInventario.getText().replace(",", ""));
            double NuevoCosto = Double.parseDouble(CampoCosto.getText().replace(",", ""));
            double NuevoPrecio = Double.parseDouble(CampoPrecioVenta.getText().replace(",", ""));

            if (NuevoInventario < 0 || NuevoCosto < 0 || NuevoPrecio < 0) {
                 JOptionPane.showMessageDialog(this, "Los valores no pueden ser negativos.", "Error", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_ACTUALIZAR + "(?, ?, ?, ?)}")) { 
                CS.setInt(1, ClaveProductoSeleccionada);
                CS.setDouble(2, NuevoInventario);
                CS.setDouble(3, NuevoCosto);
                CS.setDouble(4, NuevoPrecio);
                
                int Filas = CS.executeUpdate();
                
                if (Filas > 0) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    CargarProductos(CampoBuscador.getText()); 
                    CancelarModificacion();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException Ex) {
            JOptionPane.showMessageDialog(this, "Error de formato numérico.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void CancelarModificacion() {
        ClaveProductoSeleccionada = -1;
        EtiquetaNombreProducto.setText("Seleccione un producto...");
        EtiquetaTipoProductoValor.setText("N/A");
        CampoInventario.setText("");
        CampoCosto.setText("");
        CampoPrecioVenta.setText("");
        TablaProductos.clearSelection();
        SetModificacionEnabled(false);
    }
}