package sereinfish.bot.ui.frame;

import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private static MainFrame mainFrame;

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
