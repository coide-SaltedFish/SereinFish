package sereinfish.bot.ui.frame.rcon;

import javax.swing.*;
import java.awt.*;

public class SelectRconFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    private JTextField textField_ip;
    private JTextField textField_port;
    private JPasswordField passwordField;

    public SelectRconFrame(){
        setBounds(100, 100, 315, 320);
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
    }
}
