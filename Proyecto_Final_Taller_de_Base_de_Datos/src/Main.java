import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;

public class Main extends javax.swing.JFrame {

    private javax.swing.JDesktopPane jDsPPrincipal;
    private javax.swing.JMenuBar jMenuBar1;
    private static final String CORTE_DIA = "ObtenerCorteDia";
    private Connection Conectar;

    public Main(Connection Conexion) {
        this.Conectar = Conexion;
        initComponents();
        this.setSize(1200, 800);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Punto de Venta - Carniceria");
        mostrarVentas();
    }

    private void initComponents() {
        jDsPPrincipal = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Panel Titulo
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(0, 120, 215));
        panelTop.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        JLabel labelTitulo = new JLabel("Punto de Venta - Carniceria", JLabel.CENTER);
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTop.add(labelTitulo, BorderLayout.CENTER);
        getContentPane().add(panelTop, BorderLayout.NORTH);

        // Panel Menu
        JPanel panelLateral = new JPanel();
        panelLateral.setBackground(new Color(50, 50, 55));
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelLateral.setPreferredSize(new Dimension(200, 0));

        JButton botonVentas = crearBotonMenu("Venta");
        JButton botonProductos = crearBotonMenu("Productos");
        JButton botonCredito = crearBotonMenu("Credito");
        JButton botonHistorial = crearBotonMenu("Gastos");
        JButton botonSalir = crearBotonMenu("Salir");

        panelLateral.add(botonVentas);
        panelLateral.add(Box.createVerticalStrut(10));
        panelLateral.add(botonProductos);
        panelLateral.add(Box.createVerticalStrut(10));
        panelLateral.add(botonCredito);
        panelLateral.add(Box.createVerticalStrut(10));
        panelLateral.add(botonHistorial);
        panelLateral.add(Box.createVerticalGlue());
        panelLateral.add(botonSalir);

        // Acciones de botones
        botonVentas.addActionListener(e -> mostrarVentas());
        botonProductos.addActionListener(e -> mostrarProductos());
        botonCredito.addActionListener(e -> mostrarCredito());
        botonHistorial.addActionListener(e -> mostrarGastos());
        botonSalir.addActionListener(e -> mostrarResumenCierre());

        getContentPane().add(panelLateral, BorderLayout.WEST);

        // Panel Desktop
        jDsPPrincipal.setBackground(new java.awt.Color(60, 60, 65));
        jDsPPrincipal.setBorder(null);
        getContentPane().add(jDsPPrincipal, BorderLayout.CENTER);

        // Panel Footer
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBackground(new Color(30, 30, 33));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel labelFooter = new JLabel(
                "Proyecto Taller de Base de Datos - Estudiante: Cristopher Axel Venegas Venegas", JLabel.LEFT);
        labelFooter.setForeground(Color.LIGHT_GRAY);
        labelFooter.setFont(new Font("Arial", Font.PLAIN, 12));
        panelBottom.add(labelFooter, BorderLayout.WEST);
        getContentPane().add(panelBottom, BorderLayout.SOUTH);
        setJMenuBar(jMenuBar1);
    }

    private static JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);

        // Estilos del botón
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 150, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 120, 215));
            }
        });
        return btn;
    }

    // Mostrar el Internal Frame de Ventas
    private void mostrarVentas() {
        cerrarFrameActivo();
        IF_Venta frameVentas = new IF_Venta(this.Conectar, "Punto de Venta");
        frameVentas.setSize(jDsPPrincipal.getSize());
        jDsPPrincipal.add(frameVentas);
        frameVentas.setVisible(true);
        try {
            frameVentas.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }

        jDsPPrincipal.setSelectedFrame(frameVentas);
    }

    // Mostrar el Internal Frame de Productos
    private void mostrarProductos() {
        cerrarFrameActivo();
        IF_Producto frameProductos = new IF_Producto(this.Conectar);
        frameProductos.setSize(jDsPPrincipal.getSize());
        jDsPPrincipal.add(frameProductos);
        frameProductos.setVisible(true);
        try {
            frameProductos.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }

        jDsPPrincipal.setSelectedFrame(frameProductos);
    }

    // Mostrar el Internal Frame de Clientes
    private void mostrarCredito() {
        cerrarFrameActivo();
        IF_Credito frameCredito = new IF_Credito(this.Conectar);
        frameCredito.setSize(jDsPPrincipal.getSize());
        jDsPPrincipal.add(frameCredito);
        frameCredito.setVisible(true);
        try {
            frameCredito.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }

        jDsPPrincipal.setSelectedFrame(frameCredito);
    }

    // Muestra el Internal Frame de Reportes
    private void mostrarGastos() {
        cerrarFrameActivo();
        IF_Gastos frameGastos = new IF_Gastos(this.Conectar);
        frameGastos.setSize(jDsPPrincipal.getSize());
        jDsPPrincipal.add(frameGastos);
        frameGastos.setVisible(true);
        try {
            frameGastos.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }

        jDsPPrincipal.setSelectedFrame(frameGastos);
    }

    private void mostrarResumenCierre() {
        double VentaTotal = 0.0;
        double GastoTotal = 0.0;
        double Utilidad = 0.0;
        double TotalCaja = 0.0;
        double FondoBase = 3000.00;

        // Obtener datos del SP (Ahora recibe 4 parámetros de salida)
        try (CallableStatement CS = Conectar.prepareCall("{call " + CORTE_DIA + "(?, ?, ?, ?)}")) {
            CS.registerOutParameter(1, Types.DECIMAL);
            CS.registerOutParameter(2, Types.DECIMAL);
            CS.registerOutParameter(3, Types.DECIMAL);
            CS.registerOutParameter(4, Types.DECIMAL);

            CS.execute();

            VentaTotal = CS.getDouble(1);
            GastoTotal = CS.getDouble(2);
            Utilidad = CS.getDouble(3);
            TotalCaja = CS.getDouble(4);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener corte: " + e.getMessage());
            int confirm = JOptionPane.showConfirmDialog(this, "¿Salir sin ver el corte?", "Error BD",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION)
                System.exit(0);
            return;
        }

        // 2. Crear JDialog Personalizado
        JDialog DialogoCierre = new JDialog(this, "Corte del Día", true);
        DialogoCierre.setSize(450, 420); // Un poco más alto para la nueva fila
        DialogoCierre.setLocationRelativeTo(this);
        DialogoCierre.setLayout(new BorderLayout());
        DialogoCierre.setUndecorated(true);

        // Formateador
        DecimalFormat DF = new DecimalFormat("$#,##0.00");

        JLabel LblTitulo = new JLabel("CORTE DE CAJA", SwingConstants.CENTER);
        LblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        LblTitulo.setForeground(Color.WHITE);
        LblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        LblTitulo.setOpaque(true);
        LblTitulo.setBackground(new Color(45, 45, 48)); // Fondo título

        JPanel PanelDatos = new JPanel(new GridLayout(5, 1, 5, 8));
        PanelDatos.setBackground(new Color(45, 45, 48));
        PanelDatos.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        PanelDatos.add(crearFilaResumen("Ventas Totales:", DF.format(VentaTotal), new Color(76, 175, 80)));
        PanelDatos.add(crearFilaResumen("Gastos Totales:", "- " + DF.format(GastoTotal), new Color(244, 67, 54)));

        PanelDatos.add(new JSeparator(SwingConstants.HORIZONTAL));
        PanelDatos.add(crearFilaResumen("Caja Inicial:", "+ " + DF.format(FondoBase), new Color(255, 193, 7)));

        // Total Final en Caja
        JPanel PnlTotal = crearFilaResumen("TOTAL EN CAJA:", DF.format(TotalCaja), Color.WHITE);
        PnlTotal.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0, 120, 215)));
        PanelDatos.add(PnlTotal);

        // Botón Aceptar
        JButton BotonAceptar = new JButton("Cerrar y salir");
        BotonAceptar.setBackground(new Color(0, 120, 215));
        BotonAceptar.setForeground(Color.WHITE);
        BotonAceptar.setFont(new Font("Arial", Font.BOLD, 16));
        BotonAceptar.setFocusPainted(false);
        BotonAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BotonAceptar.setPreferredSize(new Dimension(200, 50));
        BotonAceptar.addActionListener(e -> System.exit(0));

        JPanel PanelBoton = new JPanel(new FlowLayout());
        PanelBoton.setBackground(new Color(45, 45, 48));
        PanelBoton.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        PanelBoton.add(BotonAceptar);

        // Estilo del borde del Dialogo
        ((JPanel) DialogoCierre.getContentPane()).setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 3));

        DialogoCierre.add(LblTitulo, BorderLayout.NORTH);
        DialogoCierre.add(PanelDatos, BorderLayout.CENTER);
        DialogoCierre.add(PanelBoton, BorderLayout.SOUTH);

        DialogoCierre.setVisible(true);
    }

    private JPanel crearFilaResumen(String titulo, String valor, Color colorValor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(45, 45, 48));

        JLabel lblT = new JLabel(titulo);
        lblT.setForeground(Color.LIGHT_GRAY);
        lblT.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel lblV = new JLabel(valor);
        lblV.setForeground(colorValor);
        lblV.setFont(new Font("Arial", Font.BOLD, 22));
        lblV.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(lblT, BorderLayout.WEST);
        p.add(lblV, BorderLayout.EAST);
        return p;
    }

    private void cerrarFrameActivo() {
        JInternalFrame[] frames = jDsPPrincipal.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            boolean ok = login.Iniciar();
            if (!ok) {
                System.exit(0);
            }
            ConectarBD Conexion = new ConectarBD();
            Connection Con = Conexion.Conexion();
            Main ventanaPrincipal = new Main(Con);
            ventanaPrincipal.setVisible(true);
        });
    }
}