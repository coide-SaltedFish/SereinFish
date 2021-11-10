package sereinfish.bot.ui.menu;

import sereinfish.bot.ui.frame.permission.AuthorityManagerFrame;
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
