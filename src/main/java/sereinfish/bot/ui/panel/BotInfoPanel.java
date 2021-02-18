package sereinfish.bot.ui.panel;

import sereinfish.bot.file.ImageHandle;
import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.*;

public class BotInfoPanel extends JPanel {
    public BotInfoPanel(){
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
        add(label_botName,BorderLayout.SOUTH);

        return this;
    }
}
