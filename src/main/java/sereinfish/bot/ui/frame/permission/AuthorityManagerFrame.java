package sereinfish.bot.ui.frame.permission;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AuthorityManagerFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    public AuthorityManagerFrame() {
        setTitle("权限管理");
        setBounds(100, 100, 315, 320);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);



        //设置窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                dispose();
            }
        });
        setVisible(true);
    }
}
