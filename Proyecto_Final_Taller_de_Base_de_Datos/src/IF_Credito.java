import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class IF_Credito extends JInternalFrame {
    
    // Nombres de Procedimientos Almacenados
    private static final String SP_BUSCAR = "ObtenerClientes"; 
    private static final String SP_DATOS = "ObtenerDatosCliente";
    private static final String SP_ACTUALIZAR = "ActualizarCliente";
    private static final String SP_DEUDA_TOTAL = "ObtenerDeudaCliente"; 
    
    public Connection ConexionBD; 
    
    // Componentes de la interfaz
    private JTable TablaClientes;
    private DefaultTableModel ModeloTabla;
    private JTextField CampoBuscador;
    
    // Componentes del Panel de Modificación
    private JPanel PanelModificacion;
    private JLabel EtiquetaNombreCompleto; 
    private JLabel EtiquetaTicketsPendientes; 
    private JLabel EtiquetaTotalDeuda; 
    
    // Campos de texto para edición
    private JTextField CampoNombre;
    private JTextField CampoApellido;
    private JTextField CampoTelefono;
    private JTextField CampoCalle;
    private JTextField CampoColonia;
    private JTextField CampoNumeroCasa;
    private JTextField CampoPoblacion;
    
    private JButton BotonCancelar;
    private JButton BotonGuardar;
    
    // Variables de estado
    private int ClaveClienteSeleccionada = -1;
    private final DecimalFormat FormatoMoneda = new DecimalFormat("$#,##0.00");

    // Fuentes personalizadas
    private final Font FuenteTitulo = new Font("Arial", Font.BOLD, 24);
    private final Font FuenteEtiqueta = new Font("Arial", Font.BOLD, 16);
    private final Font FuenteValor = new Font("Arial", Font.PLAIN, 16);
    private final Font FuenteInput = new Font("Arial", Font.PLAIN, 18);
    private final Font FuenteBoton = new Font("Arial", Font.BOLD, 18);
    private final Font FuenteAlerta = new Font("Arial", Font.BOLD, 20); 
    private final Font FuenteDinero = new Font("Arial", Font.BOLD, 22); 

    public IF_Credito(Connection Conexion) {
        super("Gestión de Clientes y Crédito", true, true, true, true);
        this.ConexionBD = Conexion;
        
        this.setMaximizable(true); 
        this.setClosable(true);
        this.setIconifiable(true); 
        this.setResizable(true);
        this.setBorder(null); 
        
        InicializarComponentes();
        ConfigurarTablaModelo();
        ConfigurarEscuchadores();

        CargarClientes(null); 
        
        this.setSize(1200, 750);
        this.setVisible(true);
    }

    private void InicializarComponentes() {
        this.setLayout(new BorderLayout());
        
        JPanel PanelPrincipal = new JPanel(new BorderLayout(5, 5));
        PanelPrincipal.setBackground(new Color(60, 60, 65));
        PanelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(PanelPrincipal, BorderLayout.CENTER);

        JSplitPane SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        SplitPane.setResizeWeight(0.55); 
        SplitPane.setDividerSize(5);
        SplitPane.setBorder(null);
        PanelPrincipal.add(SplitPane, BorderLayout.CENTER);

        // Panel Izquierdo
        JPanel PanelLista = new JPanel(new BorderLayout(5, 5));
        PanelLista.setBackground(new Color(60, 60, 65));
        
        JPanel PanelBuscador = new JPanel(new BorderLayout(10, 10));
        PanelBuscador.setBackground(new Color(45, 45, 48));
        PanelBuscador.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel LblBuscador = new JLabel("Buscar Cliente:");
        LblBuscador.setForeground(Color.WHITE);
        LblBuscador.setFont(FuenteEtiqueta);
        
        CampoBuscador = new JTextField();
        CampoBuscador.setFont(FuenteInput);
        CampoBuscador.setBackground(Color.WHITE);
        CampoBuscador.setForeground(Color.BLACK);
        CampoBuscador.setCaretColor(Color.BLACK);
        CampoBuscador.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        PanelBuscador.add(LblBuscador, BorderLayout.WEST);
        PanelBuscador.add(CampoBuscador, BorderLayout.CENTER);
        PanelLista.add(PanelBuscador, BorderLayout.NORTH);

        TablaClientes = new JTable();
        TablaClientes.setFont(FuenteValor); 
        TablaClientes.setRowHeight(35);     
        TablaClientes.getTableHeader().setFont(FuenteEtiqueta);
        TablaClientes.getTableHeader().setBackground(new Color(0, 120, 215));
        TablaClientes.getTableHeader().setForeground(Color.WHITE);
        TablaClientes.setBackground(new Color(40, 40, 40));
        TablaClientes.setForeground(Color.WHITE);
        TablaClientes.setSelectionBackground(new Color(0, 150, 136));
        TablaClientes.setSelectionForeground(Color.WHITE);
        
        JScrollPane ScrollPane = new JScrollPane(TablaClientes);
        ScrollPane.getViewport().setBackground(new Color(40, 40, 40));
        ScrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 48)));
        PanelLista.add(ScrollPane, BorderLayout.CENTER);
        
        SplitPane.setLeftComponent(PanelLista);

        // Panel Derecho
        PanelModificacion = new JPanel(new BorderLayout(10, 10)); 
        PanelModificacion.setBackground(new Color(45, 45, 48));
        PanelModificacion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel Título y Tickets Pendientes
        JPanel PanelHeaderDerecho = new JPanel(new GridLayout(3, 1, 5, 5)); 
        PanelHeaderDerecho.setBackground(new Color(45, 45, 48));
        
        EtiquetaNombreCompleto = new JLabel("Seleccione Cliente", SwingConstants.CENTER);
        EtiquetaNombreCompleto.setFont(FuenteTitulo);
        EtiquetaNombreCompleto.setForeground(new Color(0, 120, 215)); 
       
        EtiquetaTicketsPendientes = new JLabel("Tickets Sin Pagar: -", SwingConstants.CENTER);
        EtiquetaTicketsPendientes.setFont(FuenteAlerta);
        EtiquetaTicketsPendientes.setForeground(Color.GRAY); 
        
        EtiquetaTotalDeuda = new JLabel("Deuda Total: $0.00", SwingConstants.CENTER);
        EtiquetaTotalDeuda.setFont(FuenteDinero);
        EtiquetaTotalDeuda.setForeground(Color.GRAY);

        PanelHeaderDerecho.add(EtiquetaNombreCompleto);
        PanelHeaderDerecho.add(EtiquetaTicketsPendientes);
        PanelHeaderDerecho.add(EtiquetaTotalDeuda);
        PanelModificacion.add(PanelHeaderDerecho, BorderLayout.NORTH);

        // Formulario
        JPanel PanelFormulario = new JPanel(new GridBagLayout());
        PanelFormulario.setBackground(new Color(45, 45, 48));
        GridBagConstraints Gbc = new GridBagConstraints();
        Gbc.insets = new Insets(10, 5, 10, 5);
        Gbc.fill = GridBagConstraints.HORIZONTAL;
        
        AgregarFilaFormulario(PanelFormulario, Gbc, 0, "Nombre:", CampoNombre = CrearCampoTexto());
        AgregarFilaFormulario(PanelFormulario, Gbc, 1, "Apellido:", CampoApellido = CrearCampoTexto());
        AgregarFilaFormulario(PanelFormulario, Gbc, 2, "Teléfono:", CampoTelefono = CrearCampoTexto());
        
        Gbc.gridx = 0; Gbc.gridy = 3; Gbc.gridwidth = 2;
        JLabel LblDireccion = new JLabel("Direccion", SwingConstants.CENTER);
        LblDireccion.setFont(FuenteTitulo);
        LblDireccion.setForeground(new Color(0, 120, 215));
        LblDireccion.setHorizontalAlignment(SwingConstants.LEFT);
        PanelFormulario.add(LblDireccion, Gbc);
        Gbc.gridwidth = 1;

        AgregarFilaFormulario(PanelFormulario, Gbc, 4, "Calle:", CampoCalle = CrearCampoTexto());
        AgregarFilaFormulario(PanelFormulario, Gbc, 5, "Colonia:", CampoColonia = CrearCampoTexto());
        AgregarFilaFormulario(PanelFormulario, Gbc, 6, "Número #:", CampoNumeroCasa = CrearCampoTexto());
        AgregarFilaFormulario(PanelFormulario, Gbc, 7, "Población:", CampoPoblacion = CrearCampoTexto());

        JScrollPane ScrollFormulario = new JScrollPane(PanelFormulario);
        ScrollFormulario.setBorder(null);
        ScrollFormulario.getViewport().setBackground(new Color(45, 45, 48));
        PanelModificacion.add(ScrollFormulario, BorderLayout.CENTER);
        
        JPanel PanelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        PanelBotones.setBackground(new Color(45, 45, 48));
        
        BotonCancelar = CrearBoton("Cancelar", new Color(244, 67, 54)); 
        BotonGuardar = CrearBoton("Guardar Cambios", new Color(76, 175, 80)); 
        
        PanelBotones.add(BotonCancelar);
        PanelBotones.add(BotonGuardar);
        PanelModificacion.add(PanelBotones, BorderLayout.SOUTH);
        
        SplitPane.setRightComponent(PanelModificacion);
        
        SetModificacionEnabled(false);
        
        SwingUtilities.invokeLater(() -> SplitPane.setDividerLocation(0.55));
    }
    
    private void AgregarFilaFormulario(JPanel Panel, GridBagConstraints Gbc, int Fila, String TextoEtiqueta, JTextField Campo) {
        Gbc.gridx = 0; Gbc.gridy = Fila; Gbc.weightx = 0;
        Panel.add(CrearEtiqueta(TextoEtiqueta), Gbc);
        Gbc.gridx = 1; Gbc.weightx = 1;
        Panel.add(Campo, Gbc);
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
        Campo.setPreferredSize(new Dimension(100, 35));
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
    
    private void ConfigurarTablaModelo() {
        String[] NombresColumnas = {"ID", "Nombre", "Apellido", "Teléfono", "Pendientes"};
        ModeloTabla = new DefaultTableModel(NombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 4) return Integer.class;
                return String.class;
            }
        };
        TablaClientes.setModel(ModeloTabla);
        
        TablaClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        TablaClientes.getColumnModel().getColumn(0).setMinWidth(0);
        TablaClientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        TablaClientes.getColumnModel().getColumn(1).setPreferredWidth(150);
        TablaClientes.getColumnModel().getColumn(2).setPreferredWidth(150);
        TablaClientes.getColumnModel().getColumn(3).setPreferredWidth(100);
        TablaClientes.getColumnModel().getColumn(4).setPreferredWidth(80); 
        
        DefaultTableCellRenderer RendererCentro = new DefaultTableCellRenderer();
        RendererCentro.setHorizontalAlignment(SwingConstants.CENTER);
        RendererCentro.setBackground(new Color(40, 40, 40));
        RendererCentro.setForeground(Color.WHITE);
        TablaClientes.getColumnModel().getColumn(4).setCellRenderer(RendererCentro);
    }

    private void ConfigurarEscuchadores() {
        CampoBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                CargarClientes(CampoBuscador.getText());
            }
        });
        
        TablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TablaClientes.getSelectedRow() != -1) {
                SeleccionarCliente(TablaClientes.getSelectedRow());
            }
        });
        
        BotonCancelar.addActionListener(e -> CancelarModificacion());
        BotonGuardar.addActionListener(e -> GuardarCambios());
    }
    
    private void SetModificacionEnabled(boolean Estado) {
        CampoNombre.setEnabled(Estado);
        CampoApellido.setEnabled(Estado);
        CampoTelefono.setEnabled(Estado);
        CampoCalle.setEnabled(Estado);
        CampoColonia.setEnabled(Estado);
        CampoNumeroCasa.setEnabled(Estado);
        CampoPoblacion.setEnabled(Estado);
        
        BotonGuardar.setEnabled(Estado);
        BotonCancelar.setEnabled(Estado);
        
        if (!Estado) {
            EtiquetaNombreCompleto.setText("SELECCIONE CLIENTE");
            EtiquetaNombreCompleto.setForeground(new Color(0, 120, 215)); // <-- MANTIENE EL AZUL
            
            EtiquetaTicketsPendientes.setText("Tickets Sin Pagar: -");
            EtiquetaTicketsPendientes.setForeground(Color.GRAY);
            EtiquetaTotalDeuda.setText("Deuda Total: $0.00");
            EtiquetaTotalDeuda.setForeground(Color.GRAY);
        }
    }

    private void CargarClientes(String Filtro) {
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
                        RS.getInt("Clav_Cr"),
                        RS.getString("Nom_Cr"),
                        RS.getString("Ape_Cr"),
                        RS.getString("Tel_Cr"),
                        RS.getInt("Tickets_Pendientes")
                    });
                }
            }
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void SeleccionarCliente(int Fila) {
        ClaveClienteSeleccionada = (int) ModeloTabla.getValueAt(Fila, 0);
        int TicketsPendientes = (int) ModeloTabla.getValueAt(Fila, 4);
        
        EtiquetaTicketsPendientes.setText("Tickets Sin Pagar: " + TicketsPendientes);
        switch (TicketsPendientes) {
            case 0:
                EtiquetaTicketsPendientes.setForeground(new Color(76, 175, 80)); // Verde
                break;
            case 1:
                EtiquetaTicketsPendientes.setForeground(new Color(255, 235, 59)); // Amarillo
                break;
            case 2:
                EtiquetaTicketsPendientes.setForeground(new Color(255, 152, 0)); // Naranja
                break;
            default:
                EtiquetaTicketsPendientes.setForeground(new Color(244, 67, 54)); // Rojo
                break;
        }

        try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_DATOS + "(?)}")) {
            CS.setInt(1, ClaveClienteSeleccionada);
            
            try (ResultSet RS = CS.executeQuery()) {
                if (RS.next()) {
                    String Nombre = RS.getString("Nom_Cr");
                    String Apellido = RS.getString("Ape_Cr");
                    
                    EtiquetaNombreCompleto.setText((Nombre + " " + Apellido).toUpperCase());
                    
                    CampoNombre.setText(Nombre);
                    CampoApellido.setText(Apellido);
                    CampoTelefono.setText(RS.getString("Tel_Cr"));
                    CampoCalle.setText(RS.getString("Cll_Cr"));
                    CampoColonia.setText(RS.getString("Col_Cr"));
                    CampoNumeroCasa.setText(String.valueOf(RS.getInt("NCl_Cr")));
                    CampoPoblacion.setText(RS.getString("Pob_Cr"));
                    
                    // Obtener Deuda
                    ObtenerDeudaTotal(ClaveClienteSeleccionada);
                    
                    SetModificacionEnabled(true);
                    CampoNombre.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    CancelarModificacion();
                }
            }
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            CancelarModificacion();
        }
    }
    
    private void ObtenerDeudaTotal(int ClaveCliente) {
        try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_DEUDA_TOTAL + "(?, ?)}")) {
            CS.setInt(1, ClaveCliente);
            CS.registerOutParameter(2, Types.DECIMAL);
            
            CS.execute();
            
            double DeudaTotal = CS.getDouble(2);
            EtiquetaTotalDeuda.setText("Deuda Total: " + FormatoMoneda.format(DeudaTotal));
            
            if (DeudaTotal > 0) {
                EtiquetaTotalDeuda.setForeground(Color.WHITE);
            } else {
                EtiquetaTotalDeuda.setForeground(Color.GRAY);
            }
            
        } catch (SQLException Ex) {
            System.err.println("Error al obtener deuda: " + Ex.getMessage());
            EtiquetaTotalDeuda.setText("Deuda Total: Error");
        }
    }

    private void GuardarCambios() {
        if (ClaveClienteSeleccionada == -1) return;

        try {
            int NumeroCasa = 0;
            try {
                if (!CampoNumeroCasa.getText().trim().isEmpty())
                    NumeroCasa = Integer.parseInt(CampoNumeroCasa.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El número de casa debe ser un entero válido.", "Error Formato", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_ACTUALIZAR + "(?, ?, ?, ?, ?, ?, ?, ?)}")) { 
                CS.setInt(1, ClaveClienteSeleccionada);
                CS.setString(2, CampoNombre.getText().trim());
                CS.setString(3, CampoApellido.getText().trim());
                CS.setString(4, CampoTelefono.getText().trim());
                CS.setString(5, CampoCalle.getText().trim());
                CS.setString(6, CampoColonia.getText().trim());
                CS.setInt(7, NumeroCasa);
                CS.setString(8, CampoPoblacion.getText().trim());
                
                int Filas = CS.executeUpdate();
                
                if (Filas > 0) {
                    JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    CargarClientes(CampoBuscador.getText()); 
                    CancelarModificacion();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void CancelarModificacion() {
        ClaveClienteSeleccionada = -1;
        CampoNombre.setText("");
        CampoApellido.setText("");
        CampoTelefono.setText("");
        CampoCalle.setText("");
        CampoColonia.setText("");
        CampoNumeroCasa.setText("");
        CampoPoblacion.setText("");
        
        TablaClientes.clearSelection();
        SetModificacionEnabled(false);
    }
}