package sereinfish.bot.entity.pixiv.entity;

import lombok.Getter;

@Getter
public class PixivEntity {
    private Error error;
    private Illust illust;

    @Getter
    public class Error{
        private String user_message;
        private String message;
        private String reason;
    }

    public boolean isError(){
        return error != null;
    }
}
