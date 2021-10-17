package sereinfish.bot.entity.lolicon.sf;

import lombok.AllArgsConstructor;
import lombok.Data;
import sereinfish.bot.mlog.SfLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@AllArgsConstructor
@Data
public class Request {
    String api = "";
    int num = 1;
    int isR18 = 0;
    String keyWord = "";
    String apikey = "";
    String proxy = "";

    public String getUrl(){
        String url = api;
        url += "?";
        url += "num=" + num;
        url += "&isR18=" + isR18;
        if (proxy != null && !proxy.equals("")){
            try {
                url += "&proxy=" + URLEncoder.encode(proxy,"utf-8");
            } catch (UnsupportedEncodingException e) {
                SfLog.getInstance().e(this.getClass(),e);
            }
        }
        if (keyWord != null && !keyWord.trim().equals("")){
            try {
                url += "&keyWord=" + URLEncoder.encode(keyWord,"utf-8");
            } catch (UnsupportedEncodingException e) {
                SfLog.getInstance().e(this.getClass(),e);
            }
        }

        return url;
    }
}
