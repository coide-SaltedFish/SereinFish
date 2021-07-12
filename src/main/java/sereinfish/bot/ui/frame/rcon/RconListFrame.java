package sereinfish.bot.ui.frame.rcon;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.list.rcon.RconList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class RconListFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    public RconListFrame(){
        setTitle("Rcon列表");
        setBounds(100, 100, 315, 320);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //添加列表
        RconList list = new RconList();
        list.load();
        contentPane.add(list, BorderLayout.CENTER);

        //按钮面板
        JPanel panel_button = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(panel_button, BorderLayout.SOUTH);
        JButton button_reFlash = new JButton("刷新");
        JButton button_add = new JButton("添加");
        JButton button_delete = new JButton("删除");
        button_delete.setEnabled(false);

        panel_button.add(button_reFlash);
        panel_button.add(button_delete);
        panel_button.add(button_add);

        //按钮相关事件
        button_reFlash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button_reFlash.setEnabled(false);
                list.load();
                button_reFlash.setEnabled(true);
            }
        });

        button_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LinkRconFrame(new LinkRconFrame.LinkRconListener() {
                    @Override
                    public void success() {
                        list.load();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

        button_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int reCode = JOptionPane.showOptionDialog(MainFrame.getMainFrame(),"将会删除选中记录，是否继续","警告",JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null,new String[]{"确定","取消"},null);
                if (reCode == 0){
                    for(RconConf rconConf:list.getList().getSelectedValuesList()){
                        RconManager.getInstance().delete(rconConf.getID());
                    }
                    list.load();
                }
            }
        });

        //列表事件
        list.setListListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //删除按钮
                button_delete.setEnabled(list.getList().getSelectedValuesList().size() > 0);
            }
        });

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
