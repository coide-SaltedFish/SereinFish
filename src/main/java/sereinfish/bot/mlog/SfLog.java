package sereinfish.bot.mlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志系统
 */
public class SfLog {
    private Logger logger = LoggerFactory.getLogger(getClass());

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
     * 普通日志
     * @param clazz
     * @param message
     */
    public void d(Class clazz,Object message){
        logger.debug(clazz.getSimpleName() + "::" + message);
    }

    /**
     * 警告日志
     * @param clazz
     * @param message
     */
    public void w(Class clazz,Object message){
        logger.warn(clazz.getSimpleName() + "::" + message);
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     */
    public void e(Class clazz,Object message){
        logger.error(clazz.getSimpleName() + "::" + message);
    }

    /**
     * 错误日志
     * @param clazz
     * @param e
     */
    public void e(Class clazz,Exception e){
        logger.error(clazz.getSimpleName() + "::" + e.getLocalizedMessage(),e);
    }

    /**
     * 错误日志
     * @param clazz
     * @param e
     */
    public void e(Class clazz,Throwable e){
        logger.error(clazz.getSimpleName() + "::" + e.getLocalizedMessage(),e);
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     * @param e
     */
    public void e(Class clazz,Object message,Exception e){
        logger.error(clazz.getSimpleName() + "::" + message + "\n" + e.getLocalizedMessage(),e);
    }

    /**
     * 错误日志
     * @param clazz
     * @param message
     * @param e
     */
    public void e(Class clazz,Object message,Throwable e){
        logger.error(clazz.getSimpleName() + "::" + message + "\n" + e.getLocalizedMessage(),e);
    }
}
