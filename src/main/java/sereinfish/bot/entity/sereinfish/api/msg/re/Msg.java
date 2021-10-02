package sereinfish.bot.entity.sereinfish.api.msg.re;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Msg {
    public static final int SUCCESS = 0;
    public static final int FAIL = 404;

    int code;
    String message;
}
