package sereinfish.bot.ui.list.cellRenderer;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.ui.layout.VFlowLayout;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import java.awt.*;

public class DataBaseListCellRenderer implements ListCellRenderer {

    private CellManager cellManager;

    public DataBaseListCellRenderer(CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        DataBase dataBase = (DataBase) value;
        if (cellManager.exist(index)){
            JPanel panel = cellManager.get(index);
            //选中颜色设置
            panel.setBackground(SystemColor.control);

            if(isSelected) {
                panel.setBackground(SystemColor.textHighlight);
            }else {
                panel.setBackground(SystemColor.control);
            }
            return panel;
        }
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label_dataBaseName = new JLabel("数据库名：" + dataBase.getDataBaseConfig().getBaseName());
        label_dataBaseName.setFont(new Font("微软雅黑",Font.BOLD,15));
        JLabel label_dataBaseState = new JLabel(getDataBaseName(dataBase.getDataBaseConfig().getState()));
        JLabel label_account = new JLabel("账号：" + dataBase.getDataBaseConfig().getAccount());
        JLabel label_ip = new JLabel(dataBase.getDataBaseConfig().getIp() + ":" + dataBase.getDataBaseConfig().getPort());

        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        JPanel panel_center = new JPanel(vFlowLayout);
        panel_center.setOpaque(false);

        JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_south.setOpaque(false);


        panel_center.add(label_dataBaseName);
        panel_center.add(label_account);

        panel_south.add(label_ip);

        panel.add(label_dataBaseState,BorderLayout.NORTH);//
        panel.add(panel_center,BorderLayout.CENTER);
        panel.add(panel_south,BorderLayout.SOUTH);
        //选中颜色设置
        panel.setBackground(SystemColor.control);

        if(isSelected) {
            panel.setBackground(SystemColor.textHighlight);
        }else {
            panel.setBackground(SystemColor.control);
        }
       return panel;
    }

    /**
     * 得到数据库名称
     * @param state
     * @return
     */
    private String getDataBaseName(int state){
        if (state == DataBaseConfig.MY_SQL){
            return "MySQL";
        }
        if (state == DataBaseConfig.SQL_SERVER){
            return "SQL SERVER";
        }
        if (state == DataBaseConfig.SQLITE){
            return "SQLite";
        }

        return "未知数据库类型：" + state;
    }
}
