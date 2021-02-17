package sereinfish.bot.event;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.table.WhiteList;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.log.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.tray.AppTray;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 初始化类
 */
@EventListener
public class InitEvent {
    @Inject
    private YuQ yuQ;
    @Inject
    private MessageItemFactory mif;

    /**
     * 软件启动事件
     * @param event
     */
    @Event
    public void initEvent(AppStartEvent event){
        //初始化MyYuQ
        MyYuQ.init(yuQ,mif);
        //初始化日志
        SfLog.init();
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
    }
}
