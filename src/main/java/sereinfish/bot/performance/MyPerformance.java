package sereinfish.bot.performance;

import com.sun.management.OperatingSystemMXBean;
import net.mamoe.mirai.IMirai;
import net.mamoe.mirai.Mirai;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;

/**
 * 得到服务器相关信息
 */
public class MyPerformance {
    private static final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final Runtime runtime = Runtime.getRuntime();
    private static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    /**
     * 获取物理内存总大小
     *
     * @return
     */
    public static String getTotalPhysicalMemorySize() {
        return unitConversion(systemMXBean.getTotalPhysicalMemorySize());
    }

    /**
     * 获取物理内存剩余大小
     *
     * @return
     */
    public static String getFreePhysicalMemorySize() {
        return unitConversion(systemMXBean.getFreePhysicalMemorySize());
    }

    /**
     * 获取物理内存已使用大小
     *
     * @return
     */
    public static String getUsedPhysicalMemorySize() {
        return unitConversion(systemMXBean.getTotalPhysicalMemorySize() - systemMXBean.getFreePhysicalMemorySize());
    }

    /**
     * 获取 Swap 总大小
     *
     * @return
     */
    public static String getTotalSwapSpaceSize() {
        return unitConversion(systemMXBean.getTotalSwapSpaceSize());
    }

    /**
     * 获取 Swap 剩余大小
     *
     * @return
     */
    public static String getFreeSwapSpaceSize() {
        return unitConversion(systemMXBean.getFreeSwapSpaceSize());
    }

    /**
     * 获取 Swap 已使用大小
     *
     * @return
     */
    public static String getUsedSwapSpaceSize() {
        return unitConversion(systemMXBean.getTotalSwapSpaceSize() - systemMXBean.getFreeSwapSpaceSize());
    }

    /**
     * 获取 JVM 最大内存
     *
     * @return
     */
    public static String getJvmMaxMemory() {
        return unitConversion(runtime.maxMemory());
    }

    /**
     * 获取 JVM 内存总大小
     *
     * @return
     */
    public static String getJvmTotalMemory() {
        return unitConversion(runtime.totalMemory());
    }

    /**
     * 获取 JVM 内存剩余大小
     *
     * @return
     */
    public static String getJvmFreeMemory() {
        return unitConversion(runtime.freeMemory());
    }

    /**
     * 获取 JVM 内存已使用大小
     *
     * @return
     */
    public static String getJvmUsedMemory() {
        return unitConversion(runtime.totalMemory() - runtime.freeMemory());
    }

    /**
     * 获取系统 CPU 使用率
     *
     * @return
     */
    public static double getSystemCpuLoad() {
        return systemMXBean.getSystemCpuLoad();
    }

    /**
     * 获取 JVM 进程 CPU 使用率
     *
     * @return
     */
    public static double getProcessCpuLoad() {
        return systemMXBean.getProcessCpuLoad();
    }

    /**
     * 得到处理器核心数量
     * @return
     */
    public static int getCoresNum(){
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 得到程序运行时间
     * @return
     */
    public static String getRunTime(){
        long runTime = runtimeMXBean.getUptime();

        long day = runTime / (1000 * 60 * 60 * 24);
        long h = runTime % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
        long min = runTime % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) / (1000 * 60);
        long s = runTime % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) % (1000 * 60) / 1000;

        return day + "天" + h + "时" + min + "分" + s + "秒";
    }

    /**
     * 得到操作系统名称
     * @return
     */
    public static String getOSName(){
        return System.getProperty("os.name");
    }

    /**
     * 得到Java版本
     * @return
     */
    public static String getJavaVersion(){
        return System.getProperty("java.version");
    }

    /**
     * 得到程序开始运行时间
     * @return
     */
    public static String getStartTime(){
        //程序启动时间
        long startTime = runtimeMXBean.getStartTime();
        Date startDate = new Date(startTime);
        return Time.dateToString(startDate,Time.LOG_TIME);
    }

    /**
     * 获取进程号，适用于windows与linux
     * @return
     */
    public static String getPid(){
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            return name.split("@")[0];
        } catch (NumberFormatException e) {
            SfLog.getInstance().e(MyPerformance.class,"无法获取进程Id");
            return "无法获取进程id";
        }
    }

    /**
     * 内存单位换算
     * @param s
     * @return
     */
    private static String unitConversion(long s){
        if (s < 1024){
            return s + "B";
        }else if (s < 1024 * 1024){
            return String.format("%.2f",s / 1024.0) + "KB";
        }else if(s < 1024 * 1024 * 1024){
            return String.format("%.2f",s / (1024.0 * 1024)) + "MB";
        }
        return String.format("%.2f",s / (1024.0 * 1024 * 1024)) + "GB";
    }
}
