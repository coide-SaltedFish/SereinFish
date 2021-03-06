package sereinfish.bot.event;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.IceCreamQAQ.Yu.util.DateUtil;
import com.IceCreamQAQ.Yu.util.Web;
import com.alibaba.fastjson.JSONArray;
import com.icecreamqaq.yuq.RainBot;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.sauceNAO.SauceNaoAPI;
import sereinfish.bot.event.group.repeater.RepeaterManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.account.AccountManager;
import sereinfish.bot.file.image.gif.AnimatedGifEncoder;
import sereinfish.bot.file.image.gif.GifDecoder;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.job.JobSFManager;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.rcon.RconManager;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.tray.AppTray;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;

/**
 * 初始化类
 */
@EventListener
public class InitEvent {
    @Inject
    private JobManager jobManager;
    @Inject
    private YuQ yuQ;
    @Inject
    private MessageItemFactory mif;
    @Inject
    private DateUtil dateUtil;
    @Inject
    private RainBot rainBot;
    @Inject
    private Web web;

    /**
     * 软件启动事件
     * @param event
     */
    @Event
    public void initEvent(AppStartEvent event){
        //初始化MyYuQ
        MyYuQ.init(yuQ,mif,jobManager,dateUtil,rainBot,web);
        //初始化日志
        SfLog.init();
        SfLog.getInstance().d(this.getClass(),"SfLog初始化完成");

        //初始化权限管理器
        try {
            AuthorityManagement.init();
            SfLog.getInstance().d(this.getClass(),"权限管理器初始化完成");
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"权限管理器初始化失败,应用退出",e);
            System.exit(-1);
        }
        //初始化缓存管理器
        CacheManager.init();
        SfLog.getInstance().d(this.getClass(),"缓存管理器初始化完成");
        //初始化数据库连接池
        DataBaseManager.init();
        SfLog.getInstance().d(this.getClass(),"数据库连接池管理器初始化完成");
        //初始化RCON
        RconManager.init();
        SfLog.getInstance().d(this.getClass(),"RCON管理器初始化完成");
        //初始化群配置管理器
        GroupConfManager.init();
        SfLog.getInstance().d(this.getClass(),"群配置管理器初始化完成");

        //初始化账号管理器
        try {
            AccountManager.init();
            SfLog.getInstance().d(this.getClass(),"账号管理器初始化完成");
        } catch (IllegalModeException e) {
            SfLog.getInstance().e(this.getClass(),"账号管理器初始化失败,应用退出",e);
            System.exit(-1);
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),"账号管理器初始化失败,应用退出",e);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            SfLog.getInstance().e(this.getClass(),"账号管理器初始化失败,应用退出",e);
            System.exit(-1);
        }

        //初始化群消息记录管理器
        try {
            GroupHistoryMsgDBManager.init();
            SfLog.getInstance().d(this.getClass(),"群消息记录管理器初始化完成");
        } catch (IllegalModeException e) {
            SfLog.getInstance().e(this.getClass(),"群消息记录管理器初始化失败,应用退出",e);
            System.exit(-1);
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),"群消息记录管理器初始化失败,应用退出",e);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            SfLog.getInstance().e(this.getClass(),"群消息记录管理器初始化失败,应用退出",e);
            System.exit(-1);
        }
        //初始化复读管理器
        RepeaterManager.init();
        SfLog.getInstance().d(this.getClass(),"复读管理器初始化完成");

        //初始化定时任务管理器
        JobSFManager.init();
        SfLog.getInstance().d(this.getClass(),"定时任务管理器初始化完成");

        //设置LookAndFeel
        lookAndFeel();
        SfLog.getInstance().d(this.getClass(),"LookAndFeel设置完成");

        //显示托盘
        try {
            AppTray.init(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("image/icon_16_16.jpg"))).buildMenu().setTray();
            SfLog.getInstance().d(this.getClass(),"托盘菜单初始化完成");
        } catch (AWTException e) {
            SfLog.getInstance().e(this.getClass(),"托盘菜单初始化失败，应用退出",e);
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),"托盘菜单初始化失败，应用退出",e);
            System.exit(-1);
        }
        //初始化消息队列
        MessageState.init();
        SfLog.getInstance().d(this.getClass(),"消息队列初始化完成");
        //显示主窗体
        MainFrame.getMainFrame().setVisible(true);
        SfLog.getInstance().d(this.getClass(),"主界面初始化完成");

        //bot开始处理消息
        MyYuQ.isEnable = true;
    }

    /**
     * 设置LookAndFeel
     */
    public static void lookAndFeel() {
        // Windows风格
        String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        //String lookAndFeel = "com.jgoodies.looks.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException e) {
            SfLog.getInstance().e(InitEvent.class,e);
        } catch (InstantiationException e) {
            SfLog.getInstance().e(InitEvent.class,e);
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(InitEvent.class,e);
        } catch (UnsupportedLookAndFeelException e) {
            SfLog.getInstance().e(InitEvent.class,e);
        }
    }
}
