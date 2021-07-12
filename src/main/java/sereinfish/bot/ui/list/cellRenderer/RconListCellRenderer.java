package sereinfish.bot.ui.list.cellRenderer;

import lombok.AllArgsConstructor;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import java.awt.*;

@AllArgsConstructor
public class RconListCellRenderer implements ListCellRenderer {
    private CellManager cellManager;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        RconConf rconConf = (RconConf) value;//得到rcon配置对象
        if (cellManager.exist(rconConf.getID())){
            JPanel jPanel = cellManager.get(rconConf.getID());
            //选中颜色设置
            jPanel.setBackground(SystemColor.control);

            if(isSelected) {
                jPanel.setBackground(SystemColor.textHighlight);
            }else {
                jPanel.setBackground(SystemColor.control);
            }
            return jPanel;
        }

        JPanel jPanel = new JPanel(new BorderLayout());
        //名称
        JPanel panel_name = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel_name.setOpaque(false);
        JLabel label_name = new JLabel();
        label_name.setOpaque(false);
        if (rconConf.getName().equals("")){
            label_name.setText("未命名");
        }else {
            label_name.setText(rconConf.getName());
        }
        panel_name.add(label_name);
        jPanel.add(panel_name, BorderLayout.CENTER);

        //地址端口
        JPanel panel_addr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_addr.setOpaque(false);
        JLabel label_addr = new JLabel(rconConf.getIp() + ":" + rconConf.getPort());
        label_addr.setOpaque(false);
        panel_addr.add(label_addr);
        jPanel.add(panel_addr, BorderLayout.SOUTH);

        //选中颜色设置
        jPanel.setBackground(SystemColor.control);

        if(isSelected) {
            jPanel.setBackground(SystemColor.textHighlight);
        }else {
            jPanel.setBackground(SystemColor.control);
        }

        cellManager.add(rconConf.getID(),jPanel);
        return jPanel;
    }
}
