package sereinfish.bot.entity.qingyunke;

import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 青云客聊天api
 */
public class QingYunKeApi {

    /**
     * 聊天
     * @param msg
     * @return
     */
    public static Result chat(String msg) throws IOException {
        Request request = new Request(msg);
        String json = OkHttpUtils.getStr(request.getUrl());
        return MyYuQ.toClass(json, Result.class);
    }
}
