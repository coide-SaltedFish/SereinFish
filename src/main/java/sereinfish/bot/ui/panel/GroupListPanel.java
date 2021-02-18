package sereinfish.bot.ui.panel;

import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.GroupListCellRenderer;
import sereinfish.bot.ui.list.model.GroupListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class GroupListPanel extends JPanel {
    private JList groupList;

    public GroupListPanel(){
        build();
    }

    public GroupListPanel build(){
        setLayout(new BorderLayout());

        JPanel panel_GroupList_update = new JPanel(new FlowLayout(FlowLayout.RIGHT));//刷新按钮所在面板
        add(panel_GroupList_update,BorderLayout.NORTH);

        JButton btn_update = new JButton("刷新");
        panel_GroupList_update.add(btn_update);
        btn_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadList();
            }
        });

        groupList = new JList();

        JScrollPane scrollPane = new JScrollPane(groupList);
        add(scrollPane,BorderLayout.CENTER);

        loadList();
        return this;
    }

    /**
     * 加载列表
     */
    public void loadList(){
        groupList.setModel(new GroupListModel(MyYuQ.getGroups()));
        groupList.setCellRenderer(new GroupListCellRenderer(new CellManager()));
        groupList.setSelectedIndex(0);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * 设置列表监听事件
     * @param mouseAdapter
     */
    public void setGroupListListener(MouseAdapter mouseAdapter){
        groupList.addMouseListener(mouseAdapter);
    }
}
