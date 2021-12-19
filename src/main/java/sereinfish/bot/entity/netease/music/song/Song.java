package sereinfish.bot.entity.netease.music.song;

import lombok.Data;
import sereinfish.bot.entity.netease.music.msg.PlayeInfoMessage;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

@Data
public class Song {
    Album album;
    int fee;
    int duration;
    int rtype;
    int ftype;
    Artist[] artists;
    int copyrightId;
    int mvid;
    String name;
    String[] alias;
    long id = 0;
    long mark;
    long status;

    /**
     * 得到播放链接
     * @return
     */
    public PlayeInfoMessage getPlayerInfo() throws IOException {
        String json = OkHttpUtils.getStr("http://music.163.com/api/song/enhance/player/url?id=" + id + "&ids=[" + id + "]&br=3200000");
        return MyYuQ.toClass(json, PlayeInfoMessage.class);
    }

    /**
     * 得到所有作家名称
     * @return
     */
    public String getAllArtistName(){
        String names = "";
        for (Artist artist:artists){
            if (names.equals("")){
                names += artist.getName();
            }else {
                names += "/" + artist.getName();
            }
        }
        return names;
    }
}
