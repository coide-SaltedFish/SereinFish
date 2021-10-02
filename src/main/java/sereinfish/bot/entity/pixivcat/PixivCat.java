package sereinfish.bot.entity.pixivcat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.*;
import sereinfish.bot.entity.aSoul.ASoulCnKi;
import sereinfish.bot.entity.pixiv.entity.Illust;
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
    private String error;

    @AllArgsConstructor
    private static class Request{
        String p;
    }
}
