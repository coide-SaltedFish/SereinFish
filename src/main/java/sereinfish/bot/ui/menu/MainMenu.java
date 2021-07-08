package sereinfish.bot.ui.menu;

import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.ui.frame.authority.AuthorityManagerFrame;
import sereinfish.bot.ui.frame.database.SignInDataBaseFrame;
import sereinfish.bot.ui.frame.rcon.LinkRconFrame;
import sereinfish.bot.ui.frame.rcon.RconListFrame;

import javax.swing.*;
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
        add(getDataBaseMenu());//数据库
        add(getAuthorityMenu());//权限
        add(getRconMenu());//Rcon
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
        menu.add(menuItem_link);
        menuItem_link.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignInDataBaseFrame("连接到新的数据库").build().setVisible(true);
            }
        });

        return menu;
    }

    /**
     * 权限
     * @return
     */
    public JMenu getAuthorityMenu(){
        JMenu menu = new JMenu("权限");

        /*********权限**********/
        JMenuItem menuItem_authorityManager = new JMenuItem("权限管理器");
        menu.add(menuItem_authorityManager);
        menuItem_authorityManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AuthorityManagerFrame();
            }
        });

        return menu;
    }

    /**
     * Rcon相关
     * @return
     */
    public JMenu getRconMenu(){
        JMenu menu = new JMenu("Rcon");

        /*********Rcon**********/
        JMenuItem menuItem_linkRcon = new JMenuItem("添加Rcon");
        menu.add(menuItem_linkRcon);
        menuItem_linkRcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LinkRconFrame();
            }
        });


        JMenuItem menuItem_rconList = new JMenuItem("Rcon列表");
        menu.add(menuItem_rconList);
        menuItem_rconList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RconListFrame();
            }
        });

        return menu;
    }
}
