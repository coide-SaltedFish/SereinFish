package sereinfish.bot.ui.tray;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;

/**
 * 软件托盘
 */
public class AppTray {
    private TrayIcon trayIcon;
    private static AppTray appTray;

    private AppTray(Image image){
        trayIcon = new TrayIcon(image);
        // 为托盘添加鼠标适配器
        trayIcon.addMouseListener(new MouseAdapter() {
            // 鼠标事件
            public void mouseClicked(MouseEvent e) {
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2) {
                    MainFrame.getMainFrame().setVisible(true);
                }
            }
        });
    }

    public static AppTray init(Image image){
        appTray = new AppTray(image);
        return appTray;
    }

    public static AppTray getInstance(){
        if (appTray == null){
            throw new NullPointerException("托盘尚未初始化");
        }
        return appTray;
    }

    /**
     * 构建菜单
     * @return
     */
    public AppTray buildMenu() throws UnsupportedEncodingException {
        //使用JDialog 作为JPopupMenu载体
        JDialog popWindow = new JDialog();
        popWindow.setUndecorated(true);
        //popWindow作为JPopupMenu载体不需要多大的size
        popWindow.setSize(1, 1);

        // 创建弹出菜单
        JPopupMenu popupMenu = new JPopupMenu(){
            @Override
            public void firePopupMenuWillBecomeInvisible() {
                popWindow.setVisible(false);
            }
        };
        popupMenu.setSize(100, 30);

        /******主界面*******/
        JMenuItem mainUI_menu = new JMenuItem("打开主界面");
        mainUI_menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getMainFrame().setVisible(true);
            }
        });
        popupMenu.add(mainUI_menu);

        /******分割线*******/
        popupMenu.addSeparator();

        /********退出********/
        JMenuItem exit_menu = new JMenuItem("退出");
        exit_menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popupMenu.add(exit_menu);

        // 为托盘图标加鼠标事件监听
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 2){
                    MainFrame.getMainFrame().setVisible(true);
                }

                if (e.getButton() == 3 && e.isPopupTrigger()) {
                    //右键点击弹出JPopupMenu绑定的载体以及JPopupMenu
                    popWindow.setLocation(e.getX() + 5, e.getY() - 5 - 30);
                    popWindow.setVisible(true);
                    popupMenu.show(popWindow, 0, 0);
                }
            }
        });
        return appTray;
    }

    /**
     * 设置托盘提示
     * @param s
     */
    public void setTip(String s){
        trayIcon.setToolTip(s);
    }

    /**
     * 设置托盘
     */
    public void setTray() throws AWTException {
        SystemTray systemTray = SystemTray.getSystemTray();
        systemTray.add(trayIcon);
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }
}
