import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Login {
    
    public Connection Conexion;
    public JTextField Usuario = new JTextField();
    public JPasswordField Contrasena = new JPasswordField();
    public boolean Existe = false;

    public boolean Iniciar() {
        // Dialog Modal
        JDialog VLogin = new JDialog((Frame) null, "Iniciar Sesion", true);
        VLogin.setSize(500, 400);
        VLogin.setLocationRelativeTo(null);
        VLogin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        VLogin.setResizable(false);
        
        // Panel Principal
        JPanel PPrincipal = new JPanel();
        PPrincipal.setBackground(new Color(45, 45, 48));
        PPrincipal.setLayout(new GridLayout(2, 1, 0, 0));
        
        // Subpanel 1 - Titulo
        JPanel SubPanel1 = new JPanel();
        SubPanel1.setBackground(new Color(0, 120, 215));
        SubPanel1.setLayout(new BorderLayout());
        JLabel titulo = new JLabel("INICIAR SESION");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(JLabel.CENTER);
        SubPanel1.add(titulo, BorderLayout.CENTER);
        SubPanel1.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        PPrincipal.add(SubPanel1);
        
        // Subpanel 2 - Formulario
        JPanel SubPanel2 = new JPanel();
        SubPanel2.setBackground(new Color(60, 60, 65));
        SubPanel2.setLayout(new GridLayout(4, 2, 15, 15));
        SubPanel2.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Etiqueta Usuario
        JLabel labelUsuario = new JLabel("Usuario:");
        labelUsuario.setFont(new Font("Arial", Font.PLAIN, 24));
        labelUsuario.setForeground(Color.WHITE);
        SubPanel2.add(labelUsuario);
        
        // Campo Usuario
        Usuario.setFont(new Font("Arial", Font.PLAIN, 16));
        Usuario.setBackground(new Color(220, 220, 220));
        SubPanel2.add(Usuario);
        
        // Etiqueta Contrasena
        JLabel labelContrasena = new JLabel("Contrasena:");
        labelContrasena.setFont(new Font("Arial", Font.PLAIN, 24));
        labelContrasena.setForeground(Color.WHITE);
        SubPanel2.add(labelContrasena);
        
        // Campo Contrasena
        Contrasena.setFont(new Font("Arial", Font.PLAIN, 16));
        Contrasena.setBackground(new Color(220, 220, 220));
        SubPanel2.add(Contrasena);

        // Espacio vacio para el estilo
        SubPanel2.add(new JLabel());
        
        // Boton Iniciar
        JButton BtnIniciar = new JButton("Iniciar Sesion");
        BtnIniciar.setFont(new Font("Arial", Font.BOLD, 20));
        BtnIniciar.setBackground(new Color(0, 120, 215));
        BtnIniciar.setForeground(Color.WHITE);
        BtnIniciar.setFocusPainted(false);
        BtnIniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BtnIniciar.addActionListener(e -> ExisteUsuario(VLogin));
        
        SubPanel2.add(BtnIniciar);
        PPrincipal.add(SubPanel2);
        VLogin.add(PPrincipal);
        VLogin.setVisible(true);
        
        return Existe;
    }

    
    private void ExisteUsuario(JDialog AuxLogin) {
        String AuxUsuario = Usuario.getText().trim();
        String AuxContra = new String(Contrasena.getPassword());
        ConectarBD Conectar = new ConectarBD();
        
        if (AuxUsuario.isEmpty() || AuxContra.isEmpty()){
            JOptionPane.showMessageDialog(AuxLogin, "No debe haber campos vacios", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Conexion = Conectar.Conexion();
        
        if(Conexion != null){
            if(Comprobar(AuxUsuario, AuxContra)){
                Existe = true;
                AuxLogin.dispose();
            }else{
                JOptionPane.showMessageDialog(AuxLogin, "Usuario o contraseña incorrecta", "Error", JOptionPane.WARNING_MESSAGE);
                Contrasena.setText("");
            }
            try{
                if (Conexion != null && !Conexion.isClosed()) {
                    Conexion.close();
                }
            }catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }

        }else{
            JOptionPane.showMessageDialog(AuxLogin, "No se pudo conectar a la base de datos.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            Contrasena.setText("");
        }
    }

    public boolean Comprobar(String AuxUsu, String AuxCon) {
        try {
            String call = "{CALL ValidarLogin(?, ?, ?)}"; 
            try (CallableStatement CSt = Conexion.prepareCall(call)) {
                CSt.setString(1, AuxUsu);
                CSt.setString(2, AuxCon);
                CSt.registerOutParameter(3, Types.INTEGER); 
                CSt.execute();
                int resultado = CSt.getInt(3); 
                return resultado == 1; 
            }
        } catch (SQLException e) {
            System.err.println("Error al llamar al procedimiento ValidarLogin: " + e.getMessage());
            return false;
        }
    }
}