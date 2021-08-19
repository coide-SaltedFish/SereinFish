package sereinfish.bot.ui.panel.job;

import sereinfish.bot.data.conf.entity.GroupConf;

import javax.swing.*;

/**
 * 定时任务面板
 */
public class JobPanel extends JPanel {
    private GroupConf conf;

    public JobPanel(GroupConf conf) {
        this.conf = conf;
    }
}
