package sereinfish.bot.entity.sauceNAO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.entity.lolicon.sf.Response;
import sereinfish.bot.entity.sauceNAO.sauce.SauceNAO;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 查询图片的接口
 */
@Getter
@Setter
public class SauceNaoAPI {
    private String key;//api key
    private String url;//图片链接

    private int db = 999;
    private int testmode = 1;
    private int numres = 10;//结果数

    public SauceNaoAPI(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public SauceNaoAPI() {
    }

    /**
     * 登录
     * @param ac
     * @param psw
     * @return
     */
    public SauceNaoAPI login(String ac, String psw){
        return this;
    }

    /**
     * 开始搜索
     * @return
     */
    public SauceNAO search() throws IOException {
//        String u = "https://saucenao.com/search.php" +
//                "?db=" + db + "&output_type=2"+"&numres="+numres + ((key != null && !key.trim().equals(""))?"&api_key=" + key:"") + "&url="+URLEncoder.encode(url);
//        String u = "https://saucenao.com/search.php" +
//                "?output_type=2" + ((key != null && !key.trim().equals(""))?"&api_key=" + key:"") + "&url="+URLEncoder.encode(url);

        String u = "https://saucenao.com/search.php?url=" + url + "&output_type=2&api_key=" + key;
        SfLog.getInstance().d(this.getClass(),"SauceNAO：" + u);
        JSONObject json = OkHttpUtils.getJson(u);
        String res = json.toJSONString();
        return MyYuQ.toClass(res,SauceNAO.class);
    }
}
