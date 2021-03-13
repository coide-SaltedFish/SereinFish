package sereinfish.bot.ui.panel;

import sereinfish.bot.file.ImageHandle;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import java.awt.*;

public class BotInfoPanel extends JPanel {

    JButton btn_global;
    public BotInfoPanel(JButton btn_global){
        this.btn_global = btn_global;
        build();
    }

    public BotInfoPanel build(){
        setLayout(new BorderLayout());
        JLabel label_botHead = new JLabel();//bot头像
        label_botHead.setHorizontalAlignment(SwingConstants.CENTER);
        label_botHead.setIcon(new ImageIcon(ImageHandle.getMemberHeadImage(MyYuQ.getYuQ().getBotId(),70)));

        JLabel label_botName = new JLabel(MyYuQ.getYuQ().getBotInfo().getName());//bot名称
        label_botName.setHorizontalAlignment(SwingConstants.CENTER);

        add(label_botHead,BorderLayout.CENTER);

        JPanel panel_bottom = new JPanel(new VFlowLayout());
        panel_bottom.add(label_botName);
        panel_bottom.add(btn_global);

        add(panel_bottom,BorderLayout.SOUTH);

        return this;
    }
}
