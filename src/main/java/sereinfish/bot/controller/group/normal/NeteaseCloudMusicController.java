package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.alibaba.fastjson.JSONObject;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
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
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import top.yumbo.util.music.musicImpl.netease.NeteaseCloudMusicInfo;

import java.io.File;

@GroupController
public class NeteaseCloudMusicController extends QQController {
    @Action("点歌")
    @QMsg(mastAtBot = true)
    public Message neteaseCloudMusic(Message message, Member sender, ContextSession session) throws Exception {
        //获取歌曲名称
        String name = message.getCodeStr().substring(message.getCodeStr().indexOf("点歌") + 2).trim();

        NeteaseCloudMusicInfo neteaseCloudMusicInfo = new NeteaseCloudMusicInfo();// 得到封装网易云音乐信息的工具类
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keywords", name);
        JSONObject result = neteaseCloudMusicInfo.search(jsonObject);

        SearchMessage searchMessage = MyYuQ.toClass(result.toString(), SearchMessage.class);

        if (searchMessage.getCode() != 200){
            return MyYuQ.getMif().text("API连接异常").toMessage();
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

            reply("正在获取，请稍后");

            Song song = searchMessage.getResult().getSongs()[index];
            File musicFile = NetHandle.neteaseCloudMusicDownload(song);
            File target = new File(FileHandle.neteaseCloudMusicCachePath, song.getId() + "_amr");
            if (target.exists() && target.isFile()){
                return MyYuQ.getMif().voiceByFile(target).toMessage();
            }

            AudioHandle.mp3ToAmr(musicFile, target);
            return MyYuQ.getMif().voiceByFile(target).toMessage();
        }catch (WaitNextMessageTimeoutException e){
            musicListMsg.recall();
            return MyYuQ.getMif().text("已超时取消").toMessage();
        }catch (NumberFormatException e){
            musicListMsg.recall();
            return MyYuQ.getMif().text("输入格式错误").toMessage();
        }
    }
}
