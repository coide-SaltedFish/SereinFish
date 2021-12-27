package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.alibaba.fastjson.JSONObject;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SendMessageFailedByCancel;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.ffmpeg.AudioHandle;
import sereinfish.bot.entity.netease.music.msg.SearchMessage;
import sereinfish.bot.entity.netease.music.song.Song;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.performance.MyPerformance;
import top.yumbo.util.music.musicImpl.netease.NeteaseCloudMusicInfo;
import ws.schild.jave.EncoderException;

import java.io.File;

@GroupController
public class NeteaseCloudMusicController extends QQController {
    @Action("点歌")
    @QMsg(mastAtBot = true)
    public void neteaseCloudMusic(Group group, Message message, Member sender, ContextSession session) throws Exception {
        //获取歌曲名称
        String name = message.getCodeStr().substring(message.getCodeStr().indexOf("点歌") + 2).trim();

        NeteaseCloudMusicInfo neteaseCloudMusicInfo = new NeteaseCloudMusicInfo();// 得到封装网易云音乐信息的工具类
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keywords", name);
        JSONObject result = neteaseCloudMusicInfo.search(jsonObject);

        SearchMessage searchMessage = MyYuQ.toClass(result.toString(), SearchMessage.class);

        if (searchMessage.getCode() != 200){
            group.sendMessage(MyYuQ.getMif().text("API连接异常").toMessage());
        }

        //展示音乐列表
        MessageLineQ musicList = new Message().lineQ().at(sender).textLine("").textLine("请选择要播放的音乐(回复序号即可):");
        for (int i = 0; i < 5 && i < searchMessage.getResult().getSongs().length; i++){
            musicList.textLine(i + "." + searchMessage.getResult().getSongs()[i].getName() + " - " + searchMessage.getResult().getSongs()[i].getAllArtistName());
        }
        musicList.text("(15s)");
        Message musicListMsg = musicList.getMessage();

        boolean enableReCall = true;
        try {
            reply(musicListMsg);
        }catch (SendMessageFailedByCancel e){
            enableReCall = false;
            SfLog.getInstance().w(this.getClass(), e.getMessage());
        }

        try{
            int index = Integer.decode(Message.Companion.toCodeString(session.waitNextMessage(15000)));

            if (enableReCall){
                musicListMsg.recall();
            }
            Song song = searchMessage.getResult().getSongs()[index];
            NetHandle.neteaseCloudMusicDownload(group, song, new NetworkLoader.NetworkLoaderListener() {
                @Override
                public void start(long len) {
                    group.sendMessage("开始获取，请稍后(" + MyPerformance.unitConversion(len) + "):\n" +
                            searchMessage.getResult().getSongs()[index].getName() + " - " + searchMessage.getResult().getSongs()[index].getAllArtistName());
                }

                @Override
                public void success(File file) {
                    File target = new File(FileHandle.neteaseCloudMusicCachePath, song.getId() + "_amr");
                    if (target.exists() && target.isFile()){
                         group.sendMessage(MyYuQ.getMif().voiceByFile(target).toMessage());
                    }

                    try {
                        AudioHandle.mp3ToAmr(file, target);
                    } catch (EncoderException e) {
                        group.sendMessage("音频转换错误：" + e.getMessage());
                    }
                    group.sendMessage(MyYuQ.getMif().voiceByFile(target).toMessage());
                }

                @Override
                public void fail(Exception e) {
                    group.sendMessage(MyYuQ.getMif().text("文件下载失败：" + e.getMessage()).toMessage());
                }

                @Override
                public void progress(long pro, long len, long speed) {

                }
            });
        }catch (WaitNextMessageTimeoutException e){
            musicListMsg.recall();
            group.sendMessage(MyYuQ.getMif().text("已超时取消").toMessage());
        }catch (NumberFormatException e){
            musicListMsg.recall();
            group.sendMessage(MyYuQ.getMif().text("输入格式错误").toMessage());
        }
    }
}
