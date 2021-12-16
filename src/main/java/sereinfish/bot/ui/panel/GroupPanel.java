package sereinfish.bot.ui.panel;

import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.ui.panel.job.JobPanel;
import sereinfish.bot.ui.panel.table.database.blacklist.DBBlackPanel;
import sereinfish.bot.ui.panel.table.database.reply.DBReplyPanel;

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
        GroupConf conf = ConfManager.getInstance().get(group);

        add("群配置",new GroupConfPanel(conf));
        add("计划任务", new JobPanel(conf));
        add("自动回复",new DBReplyPanel(conf));
        add("进群信息", new JPanel());
        add("黑名单",new DBBlackPanel(conf));
    }
}
