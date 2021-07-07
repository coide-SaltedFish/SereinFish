package sereinfish.bot.ui.frame.rcon;

import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.ui.list.rcon.RconList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RconListFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    public RconListFrame(){
        setTitle("Rcon列表");
        setBounds(100, 100, 315, 320);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //添加列表
        RconList list = new RconList(RconManager.readConf());
        list.load();
        contentPane.add(list, BorderLayout.CENTER);

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
