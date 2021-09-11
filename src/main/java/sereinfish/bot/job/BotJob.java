package sereinfish.bot.job;

import com.IceCreamQAQ.Yu.annotation.Cron;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.bili.live.BiliManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;

import java.io.File;
import java.util.ArrayList;

/**
 * bot自身所带的定时任务
 */
@JobCenter
public class BotJob {

    /**
     * B站关注更新检测
     */
    @Cron("40s")
    public void biliFollow(){
        SfLog.getInstance().d(this.getClass(),"定时任务：B站关注更新检测");
        BiliManager.getInstance().check();
    }

    @Cron("At::h::00")
    public void update(){
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
    }public void updateQQHeadImage(){
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
