package sereinfish.bot.entity.sauceNAO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.entity.lolicon.sf.Response;
import sereinfish.bot.entity.sauceNAO.sauce.Result;
import sereinfish.bot.entity.sauceNAO.sauce.SauceNAO;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtil;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询图片的接口
 */
public class SauceNaoAPI {
    public static SauceNAO search(SauceNao sauceNao) throws IOException {
        SfLog.getInstance().d(SauceNaoAPI.class, "SauceNaoAPI>>开始搜索>>" + sauceNao.getImageUrl());
        String json = OkHttpUtils.getStr(sauceNao.getUrl());
        SauceNAO sauceNAO = MyYuQ.toClass(json, SauceNAO.class);

        return sauceNAO;
    }

}
