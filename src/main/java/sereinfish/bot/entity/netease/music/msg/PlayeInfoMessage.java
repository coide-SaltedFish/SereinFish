package sereinfish.bot.entity.netease.music.msg;

import lombok.Data;

@Data
public class PlayeInfoMessage {
    int code;
    Data[] data;

    @lombok.Data
    public class Data{
        long id;
        String url;
        int br;
        int size;
        String md5;
        int code;
        String type;

    }
}
