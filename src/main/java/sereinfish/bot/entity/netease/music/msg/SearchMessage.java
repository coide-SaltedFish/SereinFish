package sereinfish.bot.entity.netease.music.msg;

import lombok.Data;
import sereinfish.bot.entity.netease.music.song.Song;

@Data
public class SearchMessage {
    int code;
    Result result;

    @Data
    public class Result{
        boolean hasMore;
        int songCount;
        Song[] songs;
    }
}
