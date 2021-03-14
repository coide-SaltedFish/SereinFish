package sereinfish.bot.ui.panel.global.panel;

import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.panel.table.GroupCellRenderer;
import sereinfish.bot.ui.panel.table.QQCellRenderer;
import sereinfish.bot.ui.panel.table.database.DBTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 历史消息面板
 */
public class GroupHistoryMsgPanel extends JPanel {
    private JLabel label_num = new JLabel("共有记录：0条");
    private JTable table;
    private JTextPane textPane;
    private DBTableModel<GroupHistoryMsg> msgDBTableModel;

    public GroupHistoryMsgPanel() {
        build();
    }

    private void build(){
        table = new JTable();
        table.setRowHeight(23);

        textPane = new JTextPane();
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
                String value = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
                textPane.setText(value);
            }
        });

        loadTable();
    }

    private void loadTable(){
        ArrayList<GroupHistoryMsg> msgs = null;
        try {
            msgs = GroupHistoryMsgDBManager.getInstance().query();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (InstantiationException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }
        if (msgs == null){
            return;
        }

        label_num.setText("共有记录：" + msgs.size() + "条");
        if (msgDBTableModel == null){
            table.setModel(new DBTableModel<GroupHistoryMsg>(GroupHistoryMsg.class, msgs));
        }else {
            msgDBTableModel.setData(msgs);
            msgDBTableModel.fireTableDataChanged();
        }
        //设置表格第2列的渲染方式，添加图标
        table.getColumnModel().getColumn(1).setCellRenderer(new GroupCellRenderer(new CellManager()));
        table.getColumnModel().getColumn(2).setCellRenderer(new QQCellRenderer(new CellManager()));

        //更新控件
        table.validate();
        table.repaint();
    }
}
