package sereinfish.bot.entity.qingyunke;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 请求
 */
@Setter
@Getter
public class Request {

    private String key = "free";
    private long appid = 0;
    private String msg;

    public Request(String msg) {
        this.msg = msg;
    }

    /**
     * 返回url
     * @return
     */
    public String getUrl(){
        String api = "http://api.qingyunke.com/api.php?";

        try {
            api += "key=" + URLEncoder.encode(key, "utf-8");
            api += "&appid=" + appid;
            api += "&msg=" + URLEncoder.encode(msg, "utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(), e);
        }

        return api;
    }
}
