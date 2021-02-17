package sereinfish.bot.ui.frame;

import sereinfish.bot.file.ImageHandle;
import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private static MainFrame mainFrame;
    private JPanel contentPane;

    private MainFrame(String title){
        setTitle(title);//设置窗体名称


        buildFrame();
    }

    public static MainFrame getMainFrame(){
        if (mainFrame == null){
            mainFrame = new MainFrame(MyYuQ.appName + " " + MyYuQ.versionName);
        }
        return mainFrame;
    }

    /**
     * 创建窗体
     */
    public void buildFrame(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1120, 630);
        setLocationRelativeTo(null);
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //分割面板
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(200);
        splitPane.setContinuousLayout(true);

        contentPane.add(splitPane,BorderLayout.CENTER);

        //左半边视图
        JSplitPane splitPane_left = new JSplitPane();
        splitPane_left.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane_left.setDividerSize(2);
        splitPane_left.setDividerLocation(200);
        splitPane_left.setContinuousLayout(true);

        splitPane.setLeftComponent(splitPane_left);
        //左边视图上半部分
        JPanel panel_botInfo = new JPanel(new BorderLayout());
        splitPane_left.setTopComponent(panel_botInfo);
        JLabel label_botHead = new JLabel();//bot头像
        label_botHead.setHorizontalAlignment(SwingConstants.CENTER);
        label_botHead.setIcon(new ImageIcon(ImageHandle.getMemberHeadImage(MyYuQ.getYuQ().getBotId(),70)));

        JLabel label_botName = new JLabel(MyYuQ.getYuQ().getBotInfo().getName());//bot名称
        label_botName.setHorizontalAlignment(SwingConstants.CENTER);

        panel_botInfo.add(label_botHead,BorderLayout.CENTER);
        panel_botInfo.add(label_botName,BorderLayout.SOUTH);


        //设置窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });
    }

    /**
     * 设置窗体的显示与否
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    /**
     * 窗体关闭
     */
    public void close(){
        setVisible(false);
        dispose();
        mainFrame = null;
    }
}
