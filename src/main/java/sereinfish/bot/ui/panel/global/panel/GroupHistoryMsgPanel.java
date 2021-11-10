package sereinfish.bot.ui.panel.global.panel;

import sereinfish.bot.database.entity.GroupHistoryMsg;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.context.edit.TextAndImageEdit;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.panel.table.GroupCellRenderer;
import sereinfish.bot.ui.panel.table.QQCellRenderer;
import sereinfish.bot.ui.panel.table.TimeCellRenderer;
import sereinfish.bot.ui.panel.table.database.DBTableModel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 历史消息面板
 */
public class GroupHistoryMsgPanel extends JPanel {
    private GroupHistoryMsgService groupHistoryMsgService = MyYuQ.getGroupHistoryMsgService();

    private JLabel label_num = new JLabel("共有记录：0条");
    private JTable table;
    private TextAndImageEdit textPane;
    private DBTableModel<GroupHistoryMsg> msgDBTableModel;
    private RowSorter<DBTableModel<GroupHistoryMsg>> sorter;

    public GroupHistoryMsgPanel() {
        build();
    }

    private void build(){
        table = new JTable();
        table.setRowHeight(23);

        textPane = new TextAndImageEdit();
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(300);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(splitPane,BorderLayout.CENTER);

        JPanel panel_table = new JPanel(new BorderLayout());
        splitPane.setTopComponent(panel_table);
        panel_table.add(label_num,BorderLayout.NORTH);
        panel_table.add(new JScrollPane(table),BorderLayout.CENTER);

        JSplitPane splitPane_bottom = new JSplitPane();
        splitPane_bottom.setDividerSize(2);
        splitPane_bottom.setDividerLocation(180);
        splitPane_bottom.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBottomComponent(splitPane_bottom);

        splitPane_bottom.setTopComponent(new JScrollPane(textPane));

        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btn_update = new JButton("刷新");
        panel_btn.add(btn_update);
        btn_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable();
            }
        });
        splitPane_bottom.setBottomComponent(panel_btn);

        //表格点击事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //展示选中格子
                String value = table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),table.getSelectedColumn()).toString();
                if (value != null){
                    textPane.setText(value);
                }else {
                    textPane.setText("");
                }
            }
        });

        loadTable();
    }

    private void loadTable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<GroupHistoryMsg> msgs = groupHistoryMsgService.findAll();

                label_num.setText("共有记录：" + msgs.size() + "条");
                if (msgDBTableModel == null){
                    msgDBTableModel = new DBTableModel<>(GroupHistoryMsg.class, msgs);
                    table.setModel(msgDBTableModel);
                    sorter = new TableRowSorter<>(msgDBTableModel);
                    table.setRowSorter(sorter);

                }else {
                    msgDBTableModel.setData(msgs);
                    msgDBTableModel.fireTableDataChanged();
                }
                //设置表格第2列的渲染方式，添加图标
                table.getColumnModel().getColumn(0).setCellRenderer(new TimeCellRenderer(new CellManager()));
                table.getColumnModel().getColumn(1).setCellRenderer(new GroupCellRenderer(new CellManager()));
                table.getColumnModel().getColumn(2).setCellRenderer(new QQCellRenderer(new CellManager()));

                //更新控件
                table.validate();
                table.repaint();
            }
        }).start();
    }
}
