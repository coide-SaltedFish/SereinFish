package sereinfish.bot.ui.panel.global;

import javax.swing.*;
import java.awt.*;

/**
 * 全局配置面板
 */
public class GlobalConfPanel extends JPanel {
    public GlobalConfPanel(){
        build();
    }

    private void build(){
        setLayout(new BorderLayout());

        add(new GlobalTabbdPanel(),BorderLayout.CENTER);
    }
}
