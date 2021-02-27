package sereinfish.bot.ui.panel;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 多个群的卡片面板
 */
public class GroupsCardPanel extends JPanel {
    private CardLayout cardLayout;
    private GroupListPanel groupListPanel;//群组列表面板

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
        }

        //列表监听
        groupListPanel.setGroupListListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //得到群
                Group group = (Group)groupListPanel.getGroupList().getModel().getElementAt(groupListPanel.getGroupList().getSelectedIndex());
                cardLayout.show(GroupsCardPanel.this,group.getId() + "");
            }
        });
    }
}
