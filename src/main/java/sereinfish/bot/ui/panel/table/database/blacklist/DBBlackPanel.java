package sereinfish.bot.ui.panel.table.database.blacklist;

import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.database.ex.UpdateNoFindThrowable;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.database.table.Reply;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.frame.database.insert.InsertFrame;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.panel.table.GroupCellRenderer;
import sereinfish.bot.ui.panel.table.QQCellRenderer;
import sereinfish.bot.ui.panel.table.TimeCellRenderer;
import sereinfish.bot.ui.panel.table.database.DBTableModel;
import sereinfish.bot.ui.panel.table.database.reply.DBReplyPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DBBlackPanel extends JPanel {
    private JPanel contentPane;

    private BlackListDao blackListDao;
    private GroupConf conf;
    private JTable table;
    private DBTableModel<BlackList> model;

    private JTextArea textArea_value;
    private JLabel label_select_tip;//选中提示

    private ArrayList<BlackList> records;
    private int[] tableSelection = new int[]{};//表格选中信息

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
                blackListDao = new BlackListDao(DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID()));
            } catch (SQLException e) {
                contentPane.add(new JLabel("错误：" + e.getMessage()), BorderLayout.CENTER);
                SfLog.getInstance().e(this.getClass(),e);
                return;
            } catch (IllegalModeException e) {
                contentPane.add(new JLabel("错误：" + e.getMessage()), BorderLayout.CENTER);
                SfLog.getInstance().e(this.getClass(),e);
                return;
            } catch (ClassNotFoundException e) {
                contentPane.add(new JLabel("错误：" + e.getMessage()), BorderLayout.CENTER);
                SfLog.getInstance().e(this.getClass(),e);
                return;
            } catch (MarkIllegalLengthException e) {
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
        splitPane.setDividerLocation(300);
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
                new InsertFrame<BlackList>("黑名单", BlackList.class,new BlackList(new Date(),0,conf.getGroup(),"") , new InsertFrame.InsertListener<BlackList>() {
                    @Override
                    public void save(InsertFrame frame, BlackList value) {
                        try {
                            blackListDao.insert(value);
                        } catch (IllegalAccessException e) {
                            SfLog.getInstance().e(this.getClass(),e);
                        } catch (SQLException e) {
                            SfLog.getInstance().e(this.getClass(),e);
                        }
                        update();
                        frame.close();
                    }

                    @Override
                    public void cancel(InsertFrame frame) {
                        frame.close();
                    }
                }).setVisible(true);
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
                    update();
                }
            }
        });

        JButton btn_update = new JButton("修改");
        btn_update.setEnabled(false);
        btn_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_update.setEnabled(false);
                valueUpdate();
                btn_update.setEnabled(true);
            }
        });

        textArea_value.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!textArea_value.getText().equals(value));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!textArea_value.getText().equals(value));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!textArea_value.getText().equals(value));
            }
        });

        JButton btn_reLoad = new JButton("刷新");
        btn_reLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });

        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_btn.add(btn_insert);
        panel_btn.add(btn_delete);
        panel_btn.add(btn_update);
        panel_btn.add(btn_reLoad);

        panel_bottom.add(panel_btn,BorderLayout.CENTER);

        // 表格选中监听
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableSelection = table.getSelectedRows();//更新选中信息
            }
        });

        //表格点击事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //展示选中格子
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                if (value != null){
                    textArea_value.setText(value);
                }else {
                    textArea_value.setText("");
                }
            }
        });
    }

    /**
     * 修改
     */
    private void valueUpdate(){
        DBTableModel model = (DBTableModel) table.getModel();
        String tableValue = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
        String value = textArea_value.getText();
        String fieldName = "";
        try {
            fieldName = blackListDao.getFieldNames().get(table.getSelectedColumn());
        } catch (SQLException e) {
            SfLog.getInstance().e(DBReplyPanel.class, e);
            new TipDialog(MainFrame.getMainFrame(), "错误", "修改失败:" + e.getMessage(), true);
            return;
        }

        BlackList blackList = (BlackList) model.getRows(table.getSelectedRow());

        try {
            blackListDao.update(blackList, new String[]{fieldName}, new String[]{value});
            new TipDialog(MainFrame.getMainFrame(), "完成", fieldName + "[" + tableValue + "->" + value + "]", true);
            update();
        } catch (UpdateNoFindThrowable updateNoFindThrowable) {
            SfLog.getInstance().e(DBReplyPanel.class, updateNoFindThrowable);
            new TipDialog(MainFrame.getMainFrame(), "错误", "修改失败:" + updateNoFindThrowable.getMessage(), true);
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(DBReplyPanel.class, e);
            new TipDialog(MainFrame.getMainFrame(), "错误", "修改失败:" + e.getMessage(), true);
        } catch (SQLException e) {
            SfLog.getInstance().e(DBReplyPanel.class, e);
            new TipDialog(MainFrame.getMainFrame(), "错误", "修改失败:" + e.getMessage(), true);
        }
    }

    /**
     * 刷新
     */
    private void update(){
        loadTable();
        tableSelection = table.getSelectedColumns();
        if (tableSelection.length > 0){
            //展示选中格子
            String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
            if (value != null){
                textArea_value.setText(value);
            }else {
                textArea_value.setText("");
            }
        }else {
            textArea_value.setText("");
        }
    }

    /**
     * 加载表格
     */
    private void loadTable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    records = blackListDao.query(conf.getGroup());
                } catch (SQLException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                } catch (IllegalAccessException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                }
                if (model == null) {
                    //如果表格数据模型为null，就新建模型并应用

                    model = new DBTableModel<>(BlackList.class,records);
                    table.setModel(model);
                } else {
                    //否则就更新数据模型的数据，并刷新
                    model.setData(records);
                    model.fireTableStructureChanged();
                }
                //设置表格第2列的渲染方式，添加图标
                table.getColumnModel().getColumn(0).setCellRenderer(new TimeCellRenderer(new CellManager()));
                table.getColumnModel().getColumn(1).setCellRenderer(new QQCellRenderer(new CellManager()));
                table.getColumnModel().getColumn(2).setCellRenderer(new GroupCellRenderer(new CellManager()));

                //更新控件
                table.validate();
                table.repaint();

                if (tableSelection.length == 0) {
                    //如果没选择文件
                    label_select_tip.setText("共有" + records.size() + "条记录");
                } else {
                    label_select_tip.setText("共有" + records.size() + "条记录，选中" + tableSelection.length + "条记录");
                }
            }
        }).start();
    }
}
