package sereinfish.bot.log;

import sereinfish.bot.myYuq.time.Time;

import java.util.ArrayList;
import java.util.Date;

/**
 * 日志系统
 */
public class SfLog {
    public final static int LOG_INFO = 0;//普通日志
    public final static int LOG_WARN = 1;   //警告日志
    public final static int LOG_ERROR = 2;  //错误日志

    private ArrayList<String> logList = new ArrayList<>();//日志记录列表
    private static SfLog sfLog = null;
    private SfLog(){}

    public static SfLog getInstance() {
        if (sfLog == null){
            throw new NullPointerException("当前日志未初始化");
        }
        return sfLog;
    }

    public static SfLog init() {
        sfLog = new SfLog();
        return sfLog;
    }

    /**
     * 日志记录添加
     * @param style
     * @param str
     */
    private void add(int style,String str){
        StringBuilder stringBuilder = new StringBuilder(Time.dateToString(new Date(),Time.LOG_TIME));
        stringBuilder.append(">" + getLogTag(style) + ">");
        stringBuilder.append(str);
        logList.add(stringBuilder.toString());
        //TODO:检测列表长度保存为文件
        System.out.println(stringBuilder.toString());
    }



    /**
     * 普通日志
     * @param clazz
     * @param message
     */
    public void d(Class clazz,Object message){
        add(LOG_INFO,clazz.getSimpleName() + "::" + message);
    }

    /**
     * 警告日志
     * @param clazz
     * @param message
     */
    public void w(Class clazz,Object message){
        add(LOG_WARN,clazz.getSimpleName() + "::" + message);
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     */
    public void e(Class clazz,Object message){
        add(LOG_ERROR,clazz.getSimpleName() + "::" + message);
    }

    /**
     * 错误日志
     * @param clazz
     * @param e
     */
    public void e(Class clazz,Exception e){
        add(LOG_ERROR,clazz.getSimpleName() + "::" + e.getLocalizedMessage());
        e.printStackTrace();
    }

    /**
     * 错误日志
     * @param clazz
     * @param e
     */
    public void e(Class clazz,Throwable e){
        add(LOG_ERROR,clazz.getSimpleName() + "::" + e.getLocalizedMessage());
        e.printStackTrace();
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     * @param e
     */
    public void e(Class clazz,Object message,Exception e){
        add(LOG_ERROR,clazz.getSimpleName() + "::" + message + "\n" + e.getLocalizedMessage());
        e.printStackTrace();
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     * @param e
     */
    public void e(Class clazz,Object message,Throwable e){
        add(LOG_ERROR,clazz.getSimpleName() + "::" + message + "\n" + e.getLocalizedMessage());
        e.printStackTrace();
    }

    /**
     * 返回消息类型
     * @param style
     * @return
     */
    private String getLogTag(int style){
        switch (style){
            case LOG_INFO:
                return "Info";
            case LOG_WARN:
                return "Warn";
            case LOG_ERROR:
                return "Error";
            default:
                return "null";
        }
    }

}
