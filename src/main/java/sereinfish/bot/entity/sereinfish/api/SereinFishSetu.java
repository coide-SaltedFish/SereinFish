package sereinfish.bot.entity.sereinfish.api;

import lombok.AllArgsConstructor;
import lombok.Setter;
import sereinfish.bot.entity.sereinfish.api.msg.re.ImageList;
import sereinfish.bot.entity.sereinfish.api.msg.re.Msg;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.BotUtils;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

public class SereinFishSetu {
    private static String api = "http://sereinfish.cc:19198/api/setu";

    public static ImageList getSetu(Request request) throws IOException {
        String json = OkHttpUtils.getStr(request.getUrl());
        return MyYuQ.toClass(json, ImageList.class);
    }

    /**
     * 涩图提交
     * @param pid
     * @param sender
     * @return
     */
    public static Msg postSetu(long pid, long sender) throws IOException {
        String json = OkHttpUtils.getStr("http://sereinfish.cc:19198/api/add?pid=" + pid + "&fromqq=" + sender);
        return MyYuQ.toClass(json, Msg.class);
    }

    @Setter
    public static class Request{
        public static final int TYPE_NO_R18 = 0;
        public static final int TYPE_R18 = 1;
        public static final int TYPE_ALL = 2;

        public static final int STATE_VAGUE = 0;//模糊匹配
        public static final int STATE_ACCURATE = 1;//精确匹配

        int num = 1;
        int type = TYPE_NO_R18;
        int state = STATE_VAGUE;
        String key = "";

        public String getUrl(){
            String api = SereinFishSetu.api;

            api += "?num=" + num;
            api += "&type=" + type;
            api += "&state=" + state;
            if (key != null && !key.equals("")){
                api += "&key=" + key;
            }

            return api;
        }
    }
}
