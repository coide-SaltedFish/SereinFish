package sereinfish.bot.ui.panel.global;

import sereinfish.bot.ui.panel.global.panel.GlobalDataBasePanel;
import sereinfish.bot.ui.panel.global.panel.GroupHistoryMsgPanel;

import javax.swing.*;

public class GlobalTabbdPanel extends JTabbedPane {
    public GlobalTabbdPanel(){
        build();
    }

    private void build(){
        setBorder(BorderFactory.createTitledBorder("全局配置"));

        add("数据库",new GlobalDataBasePanel());
        add("消息记录",new GroupHistoryMsgPanel());
    }
}
