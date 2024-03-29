package sereinfish.bot.event;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.event.events.AppStopEvent;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.IceCreamQAQ.Yu.util.DateUtil;
import com.IceCreamQAQ.Yu.util.Web;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.YuQInternalBotImpl;
import com.icecreamqaq.yuq.YuQVersion;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
//import org.apdplat.word.WordSegmenter;
import opennlp.maxent.Main;
import sereinfish.bot.Start;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.database.service.BlackListService;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.database.service.ReplyService;
import sereinfish.bot.database.service.WhiteListService;
import sereinfish.bot.entity.bili.BiliManager;
import sereinfish.bot.entity.bili.entity.vtbs.VtbsInfo;
import sereinfish.bot.entity.calendar.holiday.HolidayManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.ClassManager;
import sereinfish.bot.entity.mc.JsonColor;
import sereinfish.bot.event.group.repeater.RepeaterManager;
import sereinfish.bot.job.JobSFManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.tray.AppTray;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Locale;

/**
 * 初始化类
 */
@EventListener
public class InitEvent{
    @Inject
    private HolidayManager holidayManager;

    @Inject
    private BlackListService blackListService;

    @Inject
    private GroupHistoryMsgService groupHistoryMsgService;

    @Inject
    private ReplyService replyService;

    @Inject
    private WhiteListService whiteListService;

    @Inject
    private JobManager jobManager;
    @Inject
    private YuQ yuQ;
    @Inject
    private MessageItemFactory mif;
    @Inject
    private DateUtil dateUtil;
    @Inject
    private YuQInternalBotImpl rainBot;
    @Inject
    private Web web;
    @Inject
    YuQVersion rainVersion;

    @Config("YuQ.bot.name")
    private String name;

    @Config("YuQ.NoUI")
    private String noUIStr;

    private boolean noUI = false;

    @Event
    public void exitEvent(AppStopEvent event){
        SystemTray systemTray = SystemTray.getSystemTray();
        systemTray.remove(AppTray.getInstance().getTrayIcon());
        SfLog.getInstance().w(AppTray.class, "程序退出");
    }

    /**
     * 软件启动事件
     * @param event
     */
    @Event
    public void initEvent(AppStartEvent event){
        //初始化配置
        if (noUIStr != null && !noUIStr.equals("")){
            noUI = MyYuQ.toClass(noUIStr, Boolean.class);
        }

        Locale.setDefault(Locale.CHINA);
        //初始化MyYuQ
        if (name == null || name.equals("")){
            name = yuQ.getBotInfo().getName();
        }
        MyYuQ.init(yuQ,mif,jobManager,dateUtil,rainBot,web, name);
        MyYuQ.setRainVersion(rainVersion);
        MyYuQ.setGroupHistoryMsgService(groupHistoryMsgService);
        MyYuQ.setReplyService(replyService);
        MyYuQ.setBlackListService(blackListService);
        MyYuQ.setWhiteListService(whiteListService);
        MyYuQ.setHolidayManager(holidayManager);
        //初始化日志
        SfLog.init();
        SfLog.getInstance().d(this.getClass(),"SfLog初始化完成");
        //初始化类管理器
        try {
            ClassManager.init();
            SfLog.getInstance().d(this.getClass(),"类管理器初始化完成");
        } catch (Exception e) {
            SfLog.getInstance().e(this.getClass(), "类管理器初始化失败，启动失败", e);
            System.exit(-1);
        }

        //初始化Mirai-console-terminal
//        MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(new MiraiConsoleImplementationTerminal());
//        SfLog.getInstance().d(this.getClass(),"Mirai-console-terminal 启动");

        //初始化mirai事件
        GlobalEventChannel.INSTANCE.registerListenerHost(new MiraiEvent());//事件注册
        SfLog.getInstance().d(this.getClass(),"Mirai事件响应初始化完成");

        //初始化权限管理器
        try {
            Permissions.init();
            SfLog.getInstance().d(this.getClass(),"权限管理器初始化完成");
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"权限管理器初始化失败,应用退出",e);
            System.exit(-1);
        }
        //初始化缓存管理器
        CacheManager.init();
        NetworkLoader.init();
        SfLog.getInstance().d(this.getClass(),"缓存管理器初始化完成");
//        //初始化数据库连接池
//        DataBaseManager.init();
//        SfLog.getInstance().d(this.getClass(),"数据库连接池管理器初始化完成");
        //初始化RCON
        RconManager.init();
        SfLog.getInstance().d(this.getClass(),"RCON管理器初始化完成");
        //初始化群配置管理器
        ConfManager.init();
        SfLog.getInstance().d(this.getClass(),"群配置管理器初始化完成");

        //初始化MC颜色映射表
        JsonColor.initColorMap();
        SfLog.getInstance().d(this.getClass(),"MC颜色映射表初始化完成");

        //初始化复读管理器
        RepeaterManager.init();
        SfLog.getInstance().d(this.getClass(),"复读管理器初始化完成");
        //初始化撤回管理器
        GroupReCallMessageManager.init();
        SfLog.getInstance().d(this.getClass(),"群消息撤回管理器初始化完成");

        //初始化定时任务管理器
        JobSFManager.init();
        SfLog.getInstance().d(this.getClass(),"定时任务管理器初始化完成");

        //初始化B站管理器
        BiliManager.init();
        SfLog.getInstance().d(this.getClass(),"Bili管理器初始化完成");

        //初始化Vtb列表
//        try {
//            VtbsInfo.init();
//            SfLog.getInstance().d(this.getClass(),"Vtb列表初始化完成");
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(),"Vtb列表初始化失败", e);
//        }

        //分词
//        WordSegmenter.seg("");
//        SfLog.getInstance().d(this.getClass(),"分词工具初始化完成");

        //设置LookAndFeel
        lookAndFeel();
        SfLog.getInstance().d(this.getClass(),"LookAndFeel设置完成");


        //初始化消息队列
        MessageState.getInstance();
        SfLog.getInstance().d(this.getClass(),"消息队列初始化完成");

        //显示主窗体
        SfLog.getInstance().d(this.getClass(),"主界面初始化中");
        MainFrame.getMainFrame();
        if (!noUI){
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
            //显示主窗体
            MainFrame.getMainFrame().setVisible(true);
            SfLog.getInstance().d(this.getClass(),"主界面初始化完成");

//            SfLog.getInstance().w(this.getClass(), "启动参数添加 NoUI 可取消UI界面");
        }else {
            SfLog.getInstance().w(this.getClass(), "NO UI MODE");
        }

//        //输出
//        BufferedImage bufferedImage = new BufferedImage(MainFrame.getMainFrame().getWidth(), MainFrame.getMainFrame().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
//        MainFrame.getMainFrame().paint(bufferedImage.createGraphics());
//
//        try {
//            ImageIO.write(bufferedImage, "PNG",new File(FileHandle.imageCachePath, "111.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
