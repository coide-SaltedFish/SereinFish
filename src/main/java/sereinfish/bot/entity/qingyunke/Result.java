package sereinfish.bot.entity.qingyunke;

import lombok.Getter;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 返回的结果
 */
@Getter
public class Result {
    public static final int SUCCESS = 0;

    private int result;
    private String content;

    public String getMsg(){
        String msg = content.replace("{br}", "\n");
        msg = msg.replace("菲菲", MyYuQ.getBotName());
        return msg;
    }
}
