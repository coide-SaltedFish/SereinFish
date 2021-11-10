package sereinfish.bot.ui.panel.table.database.reply;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.entity.Reply;
import sereinfish.bot.database.service.ReplyService;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.dialog.FileChooseDialog;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.frame.database.insert.InsertFrame;
import sereinfish.bot.ui.frame.rain.RainCodeFrame;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.panel.MyEditorPanel;
import sereinfish.bot.ui.panel.table.GroupCellRenderer;
import sereinfish.bot.ui.panel.table.QQCellRenderer;
import sereinfish.bot.ui.panel.table.database.DBTableModel;

import javax.inject.Inject;
import javax.persistence.Column;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DBReplyPanel extends JPanel {
    private ReplyService replyService = MyYuQ.getReplyService();

    private JPanel contentPane;

    private GroupConf conf;
    private JTable table;
    private DBTableModel<Reply> model;

    private MyEditorPanel editorPanel;
    private JLabel label_select_tip;//选中提示

    private List<Reply> replies;
    private int[] tableSelection = new int[]{};//表格选中信息

    public DBReplyPanel(GroupConf conf){
        this.conf = conf;
        contentPane = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        add(contentPane);
        table = new JTable();
        table.setRowHeight(23);

        loadPanel();
    }

    private void loadPanel(){
        build();
        loadTable();
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

        editorPanel = new MyEditorPanel(MyEditorPanel.MODE_RAIN);//选中格子内容
        splitPane_bottom.setTopComponent(new JScrollPane(editorPanel));

        JPanel panel_bottom = new JPanel(new BorderLayout());
        splitPane_bottom.setBottomComponent(panel_bottom);

        //选中提示
        label_select_tip = new JLabel("未选中内容");
        JPanel panel_tip = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_tip.setBorder(BorderFactory.createTitledBorder(""));
        panel_tip.add(label_select_tip);
        panel_bottom.add(panel_tip,BorderLayout.SOUTH);

        //操作区域
        JButton btn_backups = new JButton("备份");
        btn_backups.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File backupsFile = new File(FileHandle.backupsPath, "replyBackups_" + conf.getGroup() + "_" + System.currentTimeMillis() + ".txt");

                    List<Reply> replies = replyService.findBySource(conf.getGroup());

                    JSONArray jsonArray = new JSONArray();

                    for (Reply reply:replies){
                        SfLog.getInstance().w(this.getClass(), "备份回复：" + reply.getId());


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("qq", reply.getQq());
                        jsonObject.put("key", reply.getReKey());
                        jsonObject.put("reply", reply.getReply());

                        jsonArray.add(jsonObject);
                    }

                    FileHandle.write(backupsFile, jsonArray.toJSONString());
                    SfLog.getInstance().d(this.getClass(), "备份完成");
                } catch (IOException ioException) {
                    SfLog.getInstance().e(this.getClass(), ioException);
                    new TipDialog(MainFrame.getMainFrame(), "错误", ioException.getMessage(), false);
                }
            }
        });

        //操作区域
        JButton btn_import = new JButton("导入");
        btn_import.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出文件选择框
                new FileChooseDialog("选择备份", "txt文件", new FileChooseDialog.FileChooseListener() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void option(File f) {
                        int reCode = JOptionPane.showOptionDialog(MainFrame.getMainFrame(),"是否导入文件“" + f.getName() + "”","提示",JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE, null,new String[]{"确定","取消"},null);
                        if (reCode == 0){
                            try {
                                List<Reply> list = new ArrayList<>();
                                JSONArray jsonArray = JSONArray.parseArray(FileHandle.read(f));
                                for (JSONObject jsonObject:jsonArray.toArray(new JSONObject[]{})){
                                    list.add(new Reply(jsonObject.getLong("qq"), conf.getGroup(),
                                            jsonObject.getString("key"), jsonObject.getString("reply")));
                                }

                                for (Reply reply:list){
                                    SfLog.getInstance().w(this.getClass(), "导入回复：" + reply.getUuid());
                                    MyYuQ.getReplyService().save(reply);
                                }
                                new TipDialog(MainFrame.getMainFrame(), "提示", String.format("导入完成，共导入 %d 条记录", list.size()), false);
                                update();
                            } catch (Exception exception) {
                                SfLog.getInstance().e(this.getClass(), exception);
                                new TipDialog(MainFrame.getMainFrame(), "错误", exception.getMessage(), false);
                            }
                        }
                    }

                    @Override
                    public void error() {
                        new TipDialog(MainFrame.getMainFrame(), "错误", "发生错误", false);
                    }
                }, "txt", "*");
            }
        });

        JButton btn_insert = new JButton("插入");
        btn_insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InsertFrame<Reply>("问答", Reply.class, new Reply(0,conf.getGroup(),"",""), new InsertFrame.InsertListener<Reply>() {
                    @Override
                    public void save(InsertFrame frame, Reply value) {
                        replyService.save(value);
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
                        SfLog.getInstance().w(this.getClass(), "删除回复：" + replies.get(i).getId());
                        replyService.delete(replies.get(i).getId());
                    }
                    SfLog.getInstance().d(this.getClass(), "删除完成");
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

        editorPanel.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (table == null || table.getModel() == null || table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()) == null){
                    return;
                }

                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!editorPanel.getText().equals(value));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (table == null || table.getModel() == null || table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()) == null){
                    return;
                }
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!editorPanel.getText().equals(value));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (table == null || table.getModel() == null || table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()) == null){
                    return;
                }

                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                btn_update.setEnabled(!editorPanel.getText().equals(value));
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
        panel_btn.add(btn_backups);
        panel_btn.add(btn_import);
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
                    editorPanel.setText(value);
                }else {
                    editorPanel.setText("");
                }
            }
        });
    }

    /**
     * 修改
     */
    private void valueUpdate(){
        DBTableModel model = (DBTableModel) table.getModel();
        String value = editorPanel.getText();

        Reply reply = (Reply) model.getRows(table.getSelectedRow());

        int i = 0;
        for(Field field:reply.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(Column.class)){
                if (i == table.getSelectedColumn()){
                    try {
                        field.set(reply, value);
                    } catch (IllegalAccessException e) {
                        field.setAccessible(true);

                        try {
                            field.set(reply, value);
                        } catch (IllegalAccessException illegalAccessException) {
                            SfLog.getInstance().e(this.getClass(), e);
                        }
                    }
                    break;
                }
                i++;
            }
        }

        replyService.saveOrUpdate(reply);
        new TipDialog(MainFrame.getMainFrame(), "提示", "完成", true);
        update();
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
            editorPanel.setText(value);
        }else {
            editorPanel.setText("");
        }
    }

    /**
     * 加载表格
     */
    private void loadTable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                replies = replyService.findBySource(conf.getGroup());
                if (model == null) {
                    //如果表格数据模型为null，就新建模型并应用

                    model = new DBTableModel<Reply>(Reply.class,replies);
                    table.setModel(model);
                } else {
                    //否则就更新数据模型的数据，并刷新
                    model.setData(replies);
                    model.fireTableStructureChanged();
                }
                //设置表格第2列的渲染方式，添加图标
                table.getColumnModel().getColumn(2).setCellRenderer(new QQCellRenderer(new CellManager()));
                table.getColumnModel().getColumn(3).setCellRenderer(new GroupCellRenderer(new CellManager()));

                //更新控件
                table.validate();
                table.repaint();

                if (tableSelection.length == 0) {
                    //如果没选择文件
                    label_select_tip.setText("共有" + replies.size() + "条记录");
                } else {
                    label_select_tip.setText("共有" + replies.size() + "条记录，选中" + tableSelection.length + "条记录");
                }
            }
        }).start();
    }
}