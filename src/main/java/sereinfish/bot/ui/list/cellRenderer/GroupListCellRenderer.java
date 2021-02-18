package sereinfish.bot.ui.list.cellRenderer;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.file.ImageHandle;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GroupListCellRenderer implements ListCellRenderer{
    private CellManager cellManager;

    public GroupListCellRenderer(CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Group group = (Group)value;//得到群组对象

        if (cellManager.exist(index)){
            JPanel jPanel = cellManager.get(index);
            //选中颜色设置
            jPanel.setBackground(SystemColor.control);

            if(isSelected) {
                jPanel.setBackground(SystemColor.textHighlight);
            }else {
                jPanel.setBackground(SystemColor.control);
            }
            //设置群组显示的名字
            String name = group.getName();
            //TODO:群组配置
//            if (!groupConf.isEnable()){
//                name += "[未启用]";
//            }
            JLabel label = (JLabel) cellManager.getControl(index,0);
            label.setText(name);

            return jPanel;
        }

        JPanel jPanel = new JPanel();

        jPanel.setLayout(new BorderLayout(0, 0));

        JLabel lbl_group_head = new JLabel();
        lbl_group_head.setOpaque(false);
        lbl_group_head.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_group_head.setPreferredSize(new Dimension(60,60));
        //加载群头像
        new Thread(new Runnable() {
            @Override
            public void run() {
                lbl_group_head.setIcon(new ImageIcon(ImageHandle.getGroupHeadImage(group.getId(),60)));
                list.repaint();
            }
        }).start();


        jPanel.add(lbl_group_head, BorderLayout.WEST);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        jPanel.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        String name = group.getName();
        //群组配置
//        if (!groupConf.isEnable()){
//            name += "[未启用]";
//        }
        JLabel lbl_group_name = new JLabel(name);
        lbl_group_name.setName("name");
        lbl_group_name.setOpaque(false);
        panel.add(lbl_group_name, BorderLayout.CENTER);

        JLabel lbl_group_id = new JLabel(group.getId() + "");
        lbl_group_id.setOpaque(false);
        panel.add(lbl_group_id, BorderLayout.SOUTH);

        JPanel panel_1 = new JPanel();
        panel_1.setPreferredSize(new Dimension(jPanel.getWidth(),1));
        panel_1.setBackground(Color.LIGHT_GRAY);
        jPanel.add(panel_1, BorderLayout.SOUTH);

        //选中颜色设置
        jPanel.setBackground(SystemColor.control);

        if(isSelected) {
            jPanel.setBackground(SystemColor.textHighlight);
        }else {
            jPanel.setBackground(SystemColor.control);
        }

        cellManager.addControl(index,0l,lbl_group_name);
        cellManager.add(index,jPanel);
        return jPanel;
    }
}
