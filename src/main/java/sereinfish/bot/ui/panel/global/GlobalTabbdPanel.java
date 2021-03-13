package sereinfish.bot.ui.panel.global;

import javax.swing.*;

public class GlobalTabbdPanel extends JTabbedPane {
    public GlobalTabbdPanel(){
        build();
    }

    private void build(){
        setBorder(BorderFactory.createTitledBorder("全局配置"));

        add("数据库",new GlobalDataBasePanel());
    }
}
