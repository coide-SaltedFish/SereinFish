package sereinfish.bot.ui.panel;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 多个群的卡片面板
 */
public class GroupsCardPanel extends JPanel {
    private CardLayout cardLayout;
    private GroupListPanel groupListPanel;//群组列表面板
    private Map<Long,Long> groups = new LinkedHashMap<>();

    public GroupsCardPanel(GroupListPanel groupListPanel) {
        this.groupListPanel = groupListPanel;
        cardLayout = new CardLayout();
        build();
    }

    private void build(){
        setLayout(cardLayout);
        //加载各群面板
        for (Group group: MyYuQ.getGroups()){
            GroupPanel groupPanel = new GroupPanel(group.getId());
            add(groupPanel,group.getId() + "");
            groups.put(group.getId(),group.getId());
        }

        //列表监听
        groupListPanel.setGroupListListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //得到群
                Group group = (Group)groupListPanel.getGroupList().getModel().getElementAt(groupListPanel.getGroupList().getSelectedIndex());
                if (!groups.containsKey(group.getId())){
                    GroupPanel groupPanel = new GroupPanel(group.getId());
                    add(groupPanel,group.getId() + "");
                    groups.put(group.getId(),group.getId());
                }
                cardLayout.show(GroupsCardPanel.this,group.getId() + "");
            }
        });
    }

    public void show(long id){
        if (groups.containsKey(id)){
            cardLayout.show(GroupsCardPanel.this,id + "");
        }
    }

    public void addCard(JPanel panel,long id){
        add(panel,id+"");
        groups.put(id,id);
    }
}
