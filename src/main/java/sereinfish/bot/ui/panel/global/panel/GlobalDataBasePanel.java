package sereinfish.bot.ui.panel.global.panel;

import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.DataBaseListCellRenderer;
import sereinfish.bot.ui.list.model.DataBaseListModel;
import sereinfish.bot.ui.panel.global.table.DBTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 全局数据库操作界面
 */
public class GlobalDataBasePanel extends JPanel {
    private JPanel contentPane;


    private JList<DataBase> dataBaseList;//数据库列表
    private JList<String> dataBaseTableList;//数据库数据表列表
    private JTable table;//数据表
    private DBTableModel model;//数据表model
    private JTextArea textArea_cellInfo;//表格内容

    private DataBase dataBaseActivity;//当前活动数据库
    private String tableNameActivity;

    public GlobalDataBasePanel(){
        setLayout(new BorderLayout());
        contentPane = build();
        add(contentPane,BorderLayout.CENTER);
    }

    private JPanel build(){
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        dataBaseList = new JList<>();
        dataBaseList.setSelectedIndex(0);
        dataBaseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table = new JTable();

        //中间分开
        JSplitPane splitPane = new JSplitPane();
        panel.add(splitPane);
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
                loadDataBaseList();
            }
        });
        panel_dataBaseListPanel.add(btn_DBListUpdate,BorderLayout.NORTH);
        panel_dataBaseListPanel.add(new JScrollPane(dataBaseList), BorderLayout.CENTER);
        splitPane.setLeftComponent(panel_dataBaseListPanel);

        //数据表和操作界面在右边
        splitPane.setRightComponent(getCenterPanel());

        //加载数据库列表
        loadDataBaseList();

        //设置数据库列表监听
        dataBaseList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                dataBaseActivity = dataBaseList.getSelectedValue();
                loadDataBaseTableList();
            }
        });

        //
        dataBaseTableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                super.mouseClicked(event);
                tableNameActivity = dataBaseTableList.getSelectedValue();
                loadTable(tableNameActivity);
            }
        });

        //
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //展示选中格子
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                textArea_cellInfo.setText(value);
            }
        });

        return panel;
    }

    /**
     * 加载数据库列表
     */
    private void loadDataBaseList(){
        dataBaseList.setModel(new DataBaseListModel(DataBaseManager.getInstance().getDataBases()));
        dataBaseList.setCellRenderer(new DataBaseListCellRenderer(new CellManager()));
        dataBaseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * 加载数据库表列表
     */
    private void loadDataBaseTableList(){
        if (dataBaseActivity == null){
            return;
        }
        try {
            dataBaseTableList.setListData(dataBaseActivity.getTableNames());
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 加载表格
     * @param tableName
     * @throws SQLException
     */
    private void loadTable(String tableName) {
        try {
            if(tableName != null && !tableName.trim().equals("")){
                String sql = "SELECT * FROM " + tableName;//命令
                PreparedStatement preparedStatement = dataBaseActivity.getConnection().prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                ArrayList<String> colNames = new ArrayList<>();//表字段
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++){
                    colNames.add(resultSetMetaData.getColumnName(i));
                }

                ArrayList<ArrayList<String>> datas = new ArrayList<>();//数据
                while (resultSet.next()){
                    ArrayList<String> colData = new ArrayList<>();
                    for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++){
                        colData.add(resultSet.getString(i));
                    }
                    datas.add(colData);
                }
                table.setModel(new DBTableModel(colNames.toArray(new String[0]), datas));
                table.validate();
                table.repaint();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 加载表格
     * @param resultSet
     * @throws SQLException
     */
    private void loadTable(ResultSet resultSet) {
        try{
            ArrayList<String> colNames = new ArrayList<>();//表字段
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++){
                colNames.add(resultSetMetaData.getColumnName(i));
            }

            ArrayList<ArrayList<String>> datas = new ArrayList<>();//数据
            while (resultSet.next()){
                ArrayList<String> colData = new ArrayList<>();
                for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++){
                    colData.add(resultSet.getString(i));
                }
                datas.add(colData);
            }
            table.setModel(new DBTableModel(colNames.toArray(new String[0]), datas));
            table.validate();
            table.repaint();
        }catch (SQLException e){
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 得到右边的面板
     * @return
     */
    private JPanel getCenterPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane sp = new JSplitPane();
        sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sp.setDividerSize(3);
        sp.setDividerLocation(170);
        sp.setContinuousLayout(true);
        panel.add(sp,BorderLayout.CENTER);
        //左边放数据库表列表
        JPanel panel_dataBaseTableList = new JPanel(new BorderLayout());
        JButton btn_dataBaseTableListUpdate = new JButton("刷新");
        btn_dataBaseTableListUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataBaseTableList();
            }
        });
        dataBaseTableList = new JList<>();
        panel_dataBaseTableList.add(btn_dataBaseTableListUpdate,BorderLayout.NORTH);
        panel_dataBaseTableList.add(dataBaseTableList,BorderLayout.CENTER);
        sp.setLeftComponent(panel_dataBaseTableList);
        //
        JSplitPane splitPane = new JSplitPane();//上下分开
        sp.setRightComponent(splitPane);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(270);
        splitPane.setContinuousLayout(true);
        //上面放表格
        splitPane.setTopComponent(new JScrollPane(table));

        //下面放操作界面
        JPanel panel_operation = new JPanel(new BorderLayout());
        splitPane.setBottomComponent(panel_operation);
        textArea_cellInfo = new JTextArea();

        JSplitPane splitPane_panel_operation = new JSplitPane();
        splitPane_panel_operation.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane_panel_operation.setDividerSize(3);
        splitPane_panel_operation.setDividerLocation(100);
        splitPane_panel_operation.setContinuousLayout(true);
        panel_operation.add(splitPane_panel_operation,BorderLayout.CENTER);

        splitPane_panel_operation.setTopComponent(new JScrollPane(textArea_cellInfo));
        //语句执行
        JPanel panel_exec = new JPanel(new BorderLayout());
        splitPane_panel_operation.setBottomComponent(panel_exec);
        JTextPane textPane_sql = new JTextPane();
        panel_exec.add(new JScrollPane(textPane_sql),BorderLayout.CENTER);
        JButton btn_exec = new JButton("执行");
        btn_exec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = textPane_sql.getText();
                if (dataBaseActivity != null){
                    try {
                        PreparedStatement preparedStatement = dataBaseActivity.getConnection().prepareStatement(sql);
                        if(preparedStatement.execute()){
                            loadTable(preparedStatement.getResultSet());
                        }
                    } catch (SQLException throwables) {
                        SfLog.getInstance().e(this.getClass(),throwables);
                        JOptionPane.showMessageDialog(null,"SQL错误：" + throwables.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
        panel_exec.add(btn_exec,BorderLayout.SOUTH);

        //按钮
        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btn_frame = new JButton("显示此面板为窗口");
        panel_btn.add(btn_frame);
        btn_frame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame(tableNameActivity);
                frame.setLocationRelativeTo(null);
                frame.setBounds(100, 100, 1000, 500);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                });
                frame.setContentPane(new GlobalDataBasePanel());
                frame.setVisible(true);
            }
        });

        JButton btn_update = new JButton("刷新");
        panel_btn.add(btn_update);
        btn_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable(tableNameActivity);
            }
        });

        panel_operation.add(new JScrollPane(panel_btn),BorderLayout.SOUTH);

        return panel;
    }
}
