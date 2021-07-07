package sereinfish.bot.ui.frame.rcon;

import javax.swing.*;
import java.awt.*;

public class SelectRconFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    /**
     * 显示已连接的Rcon
     * @param title
     */
    public SelectRconFrame(String title){
        setTitle(title);

        setBounds(100, 100, 315, 320);
        contentPane = new JPanel(new BorderLayout());
        //


        setContentPane(contentPane);
    }
}
