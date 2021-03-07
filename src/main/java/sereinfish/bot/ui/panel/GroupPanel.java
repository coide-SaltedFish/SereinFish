package sereinfish.bot.ui.panel;

import sereinfish.bot.entity.conf.GroupConfManager;

import javax.swing.*;

/**
 * 群组面板
 * 选项卡
 */
public class GroupPanel extends JTabbedPane {
    private long group;

    public GroupPanel(long group) {
        super(JTabbedPane.TOP);
        this.group = group;
        build();
    }

    private void build(){
        setBorder(BorderFactory.createTitledBorder("群[" + group + "]"));

        add("群配置",new GroupConfPanel(GroupConfManager.getInstance().get(group)));
        add("数据库",new JPanel());//TODO:
    }
}
