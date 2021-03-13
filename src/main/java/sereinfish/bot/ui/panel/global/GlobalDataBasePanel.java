package sereinfish.bot.ui.panel.global;

import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.DataBaseListCellRenderer;
import sereinfish.bot.ui.list.model.DataBaseListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 全局数据库操作界面
 */
public class GlobalDataBasePanel extends JPanel {
    private JList<DataBase> dataBaseList;//数据库列表
    private JList<String> dataBaseTableList;//数据库数据表列表
    private JTable table;//数据表

    public GlobalDataBasePanel(){
        build();
    }

    private void build(){
        setLayout(new BorderLayout());

        dataBaseList = new JList<>();
        table = new JTable();

        //中间分开
        JSplitPane splitPane = new JSplitPane();
        add(splitPane);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(200);
        splitPane.setContinuousLayout(true);

        //列表在左边
        JPanel panel_dataBaseListPanel = new JPanel(new BorderLayout());
        JButton btn_DBListUpdate = new JButton("刷新");
        btn_DBListUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadList();
            }
        });
        panel_dataBaseListPanel.add(btn_DBListUpdate,BorderLayout.NORTH);
        panel_dataBaseListPanel.add(new JScrollPane(dataBaseList), BorderLayout.CENTER);
        splitPane.setLeftComponent(new JScrollPane(panel_dataBaseListPanel));

        //数据表和操作界面在右边
        splitPane.setRightComponent(getCenterPanel());

        //加载数据库列表
        loadList();

    }

    /**
     * 加载数据库列表
     */
    private void loadList(){
        dataBaseList.setModel(new DataBaseListModel(DataBaseManager.getInstance().getDataBases()));
        dataBaseList.setCellRenderer(new DataBaseListCellRenderer(new CellManager()));
        dataBaseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * 得到右边的面板
     * @return
     */
    private JPanel getCenterPanel(){
        JPanel panel = new JPanel();

        return panel;
    }
}
