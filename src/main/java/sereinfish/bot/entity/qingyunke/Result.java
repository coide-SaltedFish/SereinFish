package sereinfish.bot.entity.qingyunke;

import com.icecreamqaq.yuq.message.Message;
import lombok.Getter;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 返回的结果
 */
@Getter
public class Result {
    public static final int SUCCESS = 0;

    private int result;
    private String content;

    public Message getMsg(){
        String msg = content.replace("{br}", "\n");
        msg = msg.replace("菲菲", MyYuQ.getBotName());
        //表情检查
        Pattern pattern = Pattern.compile("\\{face:[0-9]+}",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()){
            String s = matcher.group();
            String num = s.split(":")[1];
            num = num.substring(0, num.length() - 1);

            msg = msg.replace(s, "<Rain:Face:" + num + ">");
        }
        //<Rain:Face:179>
        return Message.Companion.toMessageByRainCode(msg);
    }
}
