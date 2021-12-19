package sereinfish.bot.entity.netease.music.msg;

import lombok.Data;

@Data
public class Message {
    int code;
    String message;
    boolean hasMore;
    int songCount;
}
