package sereinfish.bot.ui.panel.table;

import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class GroupCellRenderer implements TableCellRenderer {
    private CellManager cellManager;

    public GroupCellRenderer(CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String id = MyYuQ.stringToMD5(value.toString() + row + ":::" + column);
        if (cellManager.exist(id)){
            JPanel jPanel = cellManager.get(id);
            //选中颜色设置
            if(isSelected) {
                jPanel.setBackground(SystemColor.textHighlight);
            }else {
                jPanel.setBackground(Color.WHITE);
            }
            return jPanel;
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label_image = new JLabel();//头像
        label_image.setIcon(new ImageIcon(ImageHandle.getGroupHeadImage(Long.valueOf(value.toString()),15)));
        JLabel label_name = new JLabel(value.toString());//文字

        panel.add(label_image);
        panel.add(label_name);

        //选中颜色设置
        panel.setBackground(Color.WHITE);

        if(isSelected) {
            panel.setBackground(SystemColor.textHighlight);
        }else {
            panel.setBackground(Color.WHITE);
        }

        cellManager.add(id,panel);
        return panel;
    }
}
