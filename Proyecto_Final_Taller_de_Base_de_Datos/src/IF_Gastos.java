import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class IF_Gastos extends JInternalFrame {
    
    // Nombres de Procedimientos
    private static final String SP_BUSCAR = "ObtenerGastos"; 
    private static final String SP_INSERTAR = "InsertarGasto";
    private static final String SP_ELIMINAR = "EliminarGasto";
    
    public Connection ConexionBD; 
    
    // Componentes UI
    private JTable TablaGastos;
    private DefaultTableModel ModeloTabla;
    private JTextField CampoBuscador;
    
    // Panel de Registro
    private JTextField CampoDescripcion;
    private JTextField CampoTotal;
    private JButton BotonGuardar;
    private JButton BotonEliminar;
    
    // Formatos
    private final DecimalFormat FormatoMoneda = new DecimalFormat("$#,##0.00");
    
    // Fuentes
    private final Font FuenteTitulo = new Font("Arial", Font.BOLD, 24);
    private final Font FuenteEtiqueta = new Font("Arial", Font.BOLD, 16);
    private final Font FuenteInput = new Font("Arial", Font.PLAIN, 18);
    private final Font FuenteBoton = new Font("Arial", Font.BOLD, 18);

    public IF_Gastos(Connection Conexion) {
        super("Gestión de Gastos", true, true, true, true);
        this.ConexionBD = Conexion;
        
        this.setMaximizable(true); 
        this.setClosable(true);
        this.setIconifiable(true); 
        this.setResizable(true);
        this.setBorder(null); 
        
        InicializarComponentes();
        ConfigurarTablaModelo();
        ConfigurarEscuchadores();

        CargarGastos(null); 
        
        this.setSize(1000, 600);
        this.setVisible(true);
    }

    private void InicializarComponentes() {
        this.setLayout(new BorderLayout());
        
        JPanel PanelPrincipal = new JPanel(new BorderLayout(5, 5));
        PanelPrincipal.setBackground(new Color(60, 60, 65));
        PanelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(PanelPrincipal, BorderLayout.CENTER);

        JSplitPane SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        SplitPane.setResizeWeight(0.65); // 65% para la tabla
        SplitPane.setDividerSize(5);
        SplitPane.setBorder(null);
        PanelPrincipal.add(SplitPane, BorderLayout.CENTER);

        // Panel Tabla y Buscador
        JPanel PanelLista = new JPanel(new BorderLayout(5, 5));
        PanelLista.setBackground(new Color(60, 60, 65));
        
        // Buscador
        JPanel PanelBuscador = new JPanel(new BorderLayout(10, 10));
        PanelBuscador.setBackground(new Color(45, 45, 48));
        PanelBuscador.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel LblBuscador = new JLabel("Buscar Gasto:");
        LblBuscador.setForeground(Color.WHITE);
        LblBuscador.setFont(FuenteEtiqueta);
        
        CampoBuscador = new JTextField();
        CampoBuscador.setFont(FuenteInput);
        CampoBuscador.setBackground(Color.white);
        CampoBuscador.setForeground(Color.BLACK);
        CampoBuscador.setCaretColor(Color.BLACK);
        CampoBuscador.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        PanelBuscador.add(LblBuscador, BorderLayout.WEST);
        PanelBuscador.add(CampoBuscador, BorderLayout.CENTER);
        PanelLista.add(PanelBuscador, BorderLayout.NORTH);

        // Tabla
        TablaGastos = new JTable();
        TablaGastos.setFont(new Font("Arial", Font.PLAIN, 16)); 
        TablaGastos.setRowHeight(30);     
        TablaGastos.getTableHeader().setFont(FuenteEtiqueta);
        TablaGastos.getTableHeader().setBackground(new Color(0, 120, 215));
        TablaGastos.getTableHeader().setForeground(Color.WHITE);
        TablaGastos.setBackground(new Color(40, 40, 40));
        TablaGastos.setForeground(Color.WHITE);
        TablaGastos.setSelectionBackground(new Color(0, 150, 136));
        TablaGastos.setSelectionForeground(Color.WHITE);
        
        JScrollPane ScrollPane = new JScrollPane(TablaGastos);
        ScrollPane.getViewport().setBackground(new Color(40, 40, 40));
        ScrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 48)));
        PanelLista.add(ScrollPane, BorderLayout.CENTER);
        
        SplitPane.setLeftComponent(PanelLista);

        // Panel Registrar Gasto
        JPanel PanelRegistro = new JPanel(new BorderLayout(10, 10)); 
        PanelRegistro.setBackground(new Color(45, 45, 48));
        PanelRegistro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel EtiquetaTitulo = new JLabel("NUEVO GASTO", SwingConstants.CENTER);
        EtiquetaTitulo.setFont(FuenteTitulo);
        EtiquetaTitulo.setForeground(new Color(0, 120, 215));
        PanelRegistro.add(EtiquetaTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel PanelFormulario = new JPanel(new GridBagLayout());
        PanelFormulario.setBackground(new Color(45, 45, 48));
        GridBagConstraints Gbc = new GridBagConstraints();
        Gbc.insets = new Insets(15, 5, 15, 5);
        Gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Descripción
        Gbc.gridx = 0; Gbc.gridy = 0; Gbc.weightx = 0;
        PanelFormulario.add(CrearEtiqueta("Descripción:"), Gbc);
        Gbc.gridx = 1; Gbc.weightx = 1;
        CampoDescripcion = CrearCampoTexto();
        PanelFormulario.add(CampoDescripcion, Gbc);
        
        // Total
        Gbc.gridx = 0; Gbc.gridy = 1; Gbc.weightx = 0;
        PanelFormulario.add(CrearEtiqueta("Monto Total ($):"), Gbc);
        Gbc.gridx = 1; Gbc.weightx = 1;
        CampoTotal = CrearCampoTexto();
        PanelFormulario.add(CampoTotal, Gbc);

        // Panel contenedor para centrar verticalmente
        JPanel PanelCentro = new JPanel(new BorderLayout());
        PanelCentro.setBackground(new Color(45, 45, 48));
        PanelCentro.add(PanelFormulario, BorderLayout.NORTH);
        PanelRegistro.add(PanelCentro, BorderLayout.CENTER);
        
        // Botones
        JPanel PanelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        PanelBotones.setBackground(new Color(45, 45, 48));
        
        BotonGuardar = CrearBoton("Registrar Gasto", new Color(76, 175, 80)); // Verde
        BotonEliminar = CrearBoton("Eliminar Seleccionado", new Color(244, 67, 54)); // Rojo
        
        PanelBotones.add(BotonGuardar);
        PanelBotones.add(BotonEliminar);
        PanelRegistro.add(PanelBotones, BorderLayout.SOUTH);
        
        SplitPane.setRightComponent(PanelRegistro);
        
        SwingUtilities.invokeLater(() -> SplitPane.setDividerLocation(0.65));
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
        Boton.setPreferredSize(new Dimension(220, 50));
        return Boton;
    }
    
    private void ConfigurarTablaModelo() {
        String[] NombresColumnas = {"ID", "Descripción", "Monto", "Fecha"};
        ModeloTabla = new DefaultTableModel(NombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                return String.class;
            }
        };
        TablaGastos.setModel(ModeloTabla);
        
        // Ocultar ID
        TablaGastos.getColumnModel().getColumn(0).setMaxWidth(0);
        TablaGastos.getColumnModel().getColumn(0).setMinWidth(0);
        TablaGastos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Anchos
        TablaGastos.getColumnModel().getColumn(1).setPreferredWidth(300); 
        TablaGastos.getColumnModel().getColumn(2).setPreferredWidth(100); 
        TablaGastos.getColumnModel().getColumn(3).setPreferredWidth(150); 
        
        // Alinear Monto a la derecha
        DefaultTableCellRenderer RendererDerecha = new DefaultTableCellRenderer();
        RendererDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        RendererDerecha.setBackground(new Color(40, 40, 40));
        RendererDerecha.setForeground(Color.WHITE);
        TablaGastos.getColumnModel().getColumn(2).setCellRenderer(RendererDerecha);
    }

    private void ConfigurarEscuchadores() {
        CampoBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                CargarGastos(CampoBuscador.getText());
            }
        });
        
        BotonGuardar.addActionListener(e -> GuardarGasto());
        BotonEliminar.addActionListener(e -> EliminarGasto());
    }

    private void CargarGastos(String Filtro) {
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
                        RS.getInt("Clav_Gt"),
                        RS.getString("Des_Gt"),
                        FormatoMoneda.format(RS.getDouble("Tot_Gt")),
                        RS.getString("Fec_Gt") 
                    });
                }
            }
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar gastos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void GuardarGasto() {
        String Descripcion = CampoDescripcion.getText().trim();
        String MontoStr = CampoTotal.getText().trim();
        
        if (Descripcion.isEmpty() || MontoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double Monto = Double.parseDouble(MontoStr);
            if (Monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_INSERTAR + "(?, ?)}")) { 
                CS.setString(1, Descripcion);
                CS.setDouble(2, Monto);
                
                int Filas = CS.executeUpdate();
                if (Filas > 0) {
                    JOptionPane.showMessageDialog(this, "Gasto registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    CampoDescripcion.setText("");
                    CampoTotal.setText("");
                    CargarGastos(null); 
                }
            }
        } catch (NumberFormatException Ex) {
            JOptionPane.showMessageDialog(this, "El monto debe ser numérico.", "Error Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException Ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void EliminarGasto() {
        int Fila = TablaGastos.getSelectedRow();
        if (Fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un gasto de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int ClaveGasto = (int) ModeloTabla.getValueAt(Fila, 0);
        String Descripcion = (String) ModeloTabla.getValueAt(Fila, 1);
        
        int Confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar el gasto: '" + Descripcion + "'?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
        if (Confirm == JOptionPane.YES_OPTION) {
            try (CallableStatement CS = ConexionBD.prepareCall("{call " + SP_ELIMINAR + "(?)}")) { 
                CS.setInt(1, ClaveGasto);
                CS.executeUpdate();
                CargarGastos(CampoBuscador.getText());
                
            } catch (SQLException Ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + Ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
