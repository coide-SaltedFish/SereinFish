package sereinfish.bot.entity.sauceNAO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import sereinfish.bot.mlog.SfLog;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

@AllArgsConstructor
@Getter
public class SauceNao {
    //https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=16&url=http%3A%2F%2Fsaucenao.com%2Fimages%2Fstatic%2Fbanner.gif
    public static final int OUT_TYPE_JSON = 2;//json格式

    private int output_type = 2;
    private String api_key = "saucenao";//可为空
    private int testmode = 1;
    private int db = 5;
    @NonNull
    private int numres;//请求结果数
    private int dedupe = 2;
    @NonNull
    private String imageUrl;//图片链接

    /**
     * 生成链接
     * @return
     */
    public String getUrl() throws MalformedURLException {
        String url = "https://saucenao.com/search.php?";
        url += "db=" + db;
        if (api_key != null && !api_key.equals("")){
            url += "&api_key=" + api_key;
        }
        url += "&output_type=" + output_type;
        url += "&testmode=" + testmode;
        url += "&numres=" + numres;
        try {
            url += "&url=" + URLEncoder.encode(imageUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }

        return url;
    }
}
