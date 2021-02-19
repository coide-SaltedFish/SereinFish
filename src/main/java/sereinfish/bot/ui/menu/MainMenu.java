package sereinfish.bot.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主界面菜单栏
 */
public class MainMenu extends JMenuBar {
    public MainMenu(){
        build();
    }

    /**
     * 菜单栏构建
     * @return
     */
    public MainMenu build(){
        add(getFileMenu());//文件
        return this;
    }

    /**
     * 文件菜单栏
     * @return
     */
    public JMenu getFileMenu(){
        JMenu menu = new JMenu("文件");

        menu.addSeparator();

        /********退出***********/
        JMenuItem menuItem_exit = new JMenuItem("退出");
        menuItem_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem_exit);

        return menu;
    }

    /**
     * 数据库菜单栏
     * @return
     */
    public JMenu getDataBaseMenu(){
        JMenu menu = new JMenu("数据库");

        /*******连接************/
        JMenuItem menuItem_link = new JMenuItem("连接到数据库");
        menuItem_link.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        return menu;
    }
}
