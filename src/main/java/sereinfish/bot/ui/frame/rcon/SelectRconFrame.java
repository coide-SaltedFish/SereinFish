package sereinfish.bot.ui.frame.rcon;

import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.ui.list.rcon.RconList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SelectRconFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    /**
     * 显示已连接的Rcon
     * @param title
     */
    public SelectRconFrame(String title, SelectListener listener){
        setTitle(title);

        setBounds(100, 100, 315, 320);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //添加列表
        RconList list = new RconList();
        list.load();
        contentPane.add(list, BorderLayout.CENTER);
        //设置单选
        list.getList().setSelectedIndex(0);
        list.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //按钮
        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(panel_btn, BorderLayout.SOUTH);
        JButton button_ok = new JButton("确定");
        JButton button_clean = new JButton("清除");
        JButton button_cancel = new JButton("取消");

        panel_btn.add(button_clean);
        panel_btn.add(button_cancel);
        panel_btn.add(button_ok);
        //按钮事件
        button_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RconConf conf = list.getList().getSelectedValue();
                if (listener != null){
                    listener.select(conf);
                }
                SelectRconFrame.this.setVisible(false);
                SelectRconFrame.this.dispose();
            }
        });

        button_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null){
                    listener.clean();
                }
                SelectRconFrame.this.setVisible(false);
                SelectRconFrame.this.dispose();
            }
        });

        button_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectRconFrame.this.setVisible(false);
                SelectRconFrame.this.dispose();
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

    public interface SelectListener{
        public void select(RconConf conf);
        public void clean();
    }
}
