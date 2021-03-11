package sereinfish.bot.ui.panel.table.database.blacklist;

import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.panel.table.GroupCellRenderer;
import sereinfish.bot.ui.panel.table.QQCellRenderer;
import sereinfish.bot.ui.panel.table.database.DBTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBBlackPanel extends JPanel {
    private JPanel contentPane;

    private BlackListDao blackListDao;
    private GroupConf conf;
    private JTable table;
    private DBTableModel<BlackList> model;

    private JTextArea textArea_value;
    private JLabel label_select_tip;//选中提示

    private ArrayList<BlackList> records;
    private int[] tableSelection;//表格选中信息

    public DBBlackPanel(GroupConf conf){
        this.conf = conf;
        contentPane = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        add(contentPane);

        table = new JTable();
        table.setRowHeight(23);

        loadPanel();
    }

    private void loadPanel(){
        contentPane.removeAll();

        if (conf.isDataBaseEnable()){
            try {
                blackListDao = new BlackListDao(conf.getDataBase());
            } catch (SQLException e) {
                contentPane.add(new JLabel("错误：" + e.getMessage()), BorderLayout.CENTER);
                SfLog.getInstance().e(this.getClass(),e);
                return;
            }
            build();
            loadTable();
        }else {
            contentPane.add(new JLabel("未连接数据库",JLabel.CENTER), BorderLayout.CENTER);

            JButton btn_reLoad = new JButton("重载");
            btn_reLoad.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (conf.isDataBaseEnable()) {
                        loadPanel();
                    }
                }
            });
            contentPane.add(btn_reLoad,BorderLayout.SOUTH);
        }
    }

    private void build(){
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(250);
        contentPane.add(splitPane,BorderLayout.CENTER);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        splitPane.setTopComponent(new JScrollPane(table));

        JSplitPane splitPane_bottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane_bottom.setDividerSize(2);
        splitPane_bottom.setDividerLocation(130);
        splitPane.setBottomComponent(splitPane_bottom);

        textArea_value = new JTextArea();//选中格子内容
        splitPane_bottom.setTopComponent(new JScrollPane(textArea_value));

        JPanel panel_bottom = new JPanel(new BorderLayout());
        splitPane_bottom.setBottomComponent(panel_bottom);

        //选中提示
        label_select_tip = new JLabel("未选中内容");
        JPanel panel_tip = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_tip.setBorder(BorderFactory.createTitledBorder(""));
        panel_tip.add(label_select_tip);
        panel_bottom.add(panel_tip,BorderLayout.SOUTH);

        //操作区域
        JButton btn_insert = new JButton("插入");
        btn_insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JButton btn_delete = new JButton("删除");
        btn_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int reCode = JOptionPane.showOptionDialog(MainFrame.getMainFrame(),"将会删除选中记录，是否继续","警告",JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null,new String[]{"确定","取消"},null);
                if (reCode == 0){
                    for (int i:tableSelection){
                        try {
                            blackListDao.delete(records.get(i).getGroup(),records.get(i).getQq());
                        } catch (SQLException e) {
                            SfLog.getInstance().e(this.getClass(),e);
                        }
                    }
                    loadTable();
                }
            }
        });

        JButton btn_reLoad = new JButton("刷新");
        btn_reLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable();
                tableSelection = table.getSelectedColumns();
                if (tableSelection.length > 0){
                    //展示选中格子
                    String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                    textArea_value.setText(value);
                }else {
                    textArea_value.setText("");
                }
                loadSelectTip();
            }
        });

        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_btn.add(btn_insert);
        panel_btn.add(btn_delete);
        panel_btn.add(btn_reLoad);

        panel_bottom.add(panel_btn,BorderLayout.CENTER);

        // 表格选中监听
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableSelection = table.getSelectedRows();//更新选中信息
                loadSelectTip();//更新下面的文字
            }
        });

        //表格点击事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //展示选中格子
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                textArea_value.setText(value);
            }
        });
    }

    /**
     * 加载表格
     */
    private void loadTable(){
        try {
            records = blackListDao.query();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (InstantiationException e) {
            SfLog.getInstance().e(this.getClass(), e);
        }
        if (model == null) {
            //如果表格数据模型为null，就新建模型并应用

            model = new DBTableModel<>(getTitle(),records);
            table.setModel(model);
        } else {
            //否则就更新数据模型的数据，并刷新
            model.setData(records);
            model.fireTableStructureChanged();
        }
        //设置表格第2列的渲染方式，添加图标
        table.getColumnModel().getColumn(1).setCellRenderer(new QQCellRenderer(new CellManager()));
        table.getColumnModel().getColumn(2).setCellRenderer(new GroupCellRenderer(new CellManager()));

        //更新控件
        table.validate();
        table.repaint();
    }

    /**
     * 加载最下面的提示文本
     */
    private void loadSelectTip() {
        if (tableSelection.length == 0) {
            //如果没选择文件
            label_select_tip.setText("共有" + records.size() + "条记录");
        } else {
            label_select_tip.setText("共有" + records.size() + "条记录，选中" + tableSelection.length + "条记录");
        }
    }

    /**
     * 得到标题
     * @return
     */
    private String[] getTitle(){
        ArrayList<String> list = new ArrayList<>();
        for (Field field:blackListDao.getType().getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                sereinfish.bot.database.dao.annotation.Field S_field = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                list.add(S_field.name());
            }
        }
        return list.toArray(new String[0]);
    }
}
