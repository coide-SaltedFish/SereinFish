package sereinfish.bot.ui.panel.table;

import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Date;

public class TimeCellRenderer implements TableCellRenderer {
    private CellManager cellManager;

    public TimeCellRenderer(CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String id = MyYuQ.stringToMD5(value.toString() + row + "::" + column);
        if (cellManager.exist(id)){
            JPanel panel = cellManager.get(id);
            //选中颜色设置
            if(isSelected) {
                panel.setBackground(SystemColor.textHighlight);
            }else {
                panel.setBackground(Color.WHITE);
            }
            return panel;
        }
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        Date date = new Date(Long.valueOf(value.toString()));
        JLabel label = new JLabel(Time.dateToString(date,Time.LOG_TIME));

        panel.add(label);
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
