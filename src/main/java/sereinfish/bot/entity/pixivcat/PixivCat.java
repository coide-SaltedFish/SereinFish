package sereinfish.bot.entity.pixivcat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.*;
import sereinfish.bot.entity.aSoul.ASoulCnKi;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;

import static sereinfish.bot.utils.OkHttpUtil.JSON;

@Getter
public class PixivCat {
    private boolean success;
    private long id;
    private String id_str;
    private boolean multiple;
    private String[] original_urls;
    private String original_url;
    private String[] original_urls_proxy;
    private String original_url_proxy;

    /**
     * 得到PixivCat
     * @return
     */
    public static void getPixivCat(String url, Callback callback){
        String api = "https://api.pixiv.cat/v1/generate";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON , MyYuQ.toJson(new Request(url), Request.class));

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(api)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    @AllArgsConstructor
    private static class Request{
        String p;
    }
}
