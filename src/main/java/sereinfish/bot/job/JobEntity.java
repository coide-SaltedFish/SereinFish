package sereinfish.bot.job;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;

import java.io.File;
import java.util.ArrayList;

public class JobEntity {
    /**
     * 头像更新任务
     */
    public static class updateQQHeadImage implements Runnable{
        @Override
        public void run(){
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

    /**
     * 定时消息发送
     */
    @AllArgsConstructor
    public static class SendMsgJob implements Runnable{
        private Contact contact;
        private Message[] messages;

        @Override
        public void run() {
            SfLog.getInstance().d(this.getClass(), "定时任务执行，消息发送");
            for (Message message:messages){
                contact.sendMessage(message);
            }
        }
    }
}
