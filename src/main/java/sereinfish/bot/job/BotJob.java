package sereinfish.bot.job;

import com.IceCreamQAQ.Yu.annotation.Cron;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;

import java.io.File;
import java.util.ArrayList;

/**
 * 定时任务
 */
@JobCenter
public class BotJob {

    /***
     * 定时任务。
     * value 必须按规范写成组合
     * 以 At:: 开头。
     * 如果匹配每天的第几个小时的第几分钟，则接下来写 d。
     * 如果匹配某小时的第几分钟则写 h。
     * 接下来写分隔符 ::
     * 如果上一步写的是 d，则写 小时:分钟（这里只有一个 : ）（二十四小时制）。例如: 12:00
     * 如果上一步写的是 h，则直接写第几分钟。例如: 00
     * 所有的冒号均是英文半角。
     *
     * 本实例在每个小时刚开始触发。
     *
     * 定时任务方法不接受任何参数，也不接受任何返回值。
     *
     * At::d::8:15 (每天的八点十五
     *
     * At::h::30 (每个小时的第30分钟
     */

    /**
     * 每天1点更新qq头像及群头像
     */
    @Cron("At::d::1:00")
    public void updateQQHandImage() {
        SfLog.getInstance().d(this.getClass(),"定时任务：头像缓存更新");
        //获取更新列表
        ArrayList<Long> memberHeads = new ArrayList<>();
        ArrayList<Long> groupHeads = new ArrayList<>();

        for(File file: FileHandle.memberHeadCachePath.listFiles()){
            try{
                memberHeads.add(Long.valueOf(file.getName()));
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(),"QQ头像缓存目录未知文件", e);
            }
        }

        for (File file:FileHandle.groupHeadCachePath.listFiles()){
            try{
                groupHeads.add(Long.valueOf(file.getName()));
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(),"群头像缓存目录未知文件", e);
            }
        }

        //开始更新
        int i = 0;
        for(long qq:memberHeads){
            if(i % 10 == 0){
                SfLog.getInstance().d(this.getClass(),"头像缓存更新进度：" + i + "/" + (memberHeads.size() + groupHeads.size()));
            }
            CacheManager.getMemberNetHeadImage(qq);
            i++;
        }
        for(long group:groupHeads){
            if(i % 10 == 0){
                SfLog.getInstance().d(this.getClass(),"头像缓存更新进度：" + i + "/" + (memberHeads.size() + groupHeads.size()));
            }
            CacheManager.getGroupNetHeadImage(group);
        }

        SfLog.getInstance().d(this.getClass(),"头像缓存更新完成");
    }
}
