package sereinfish.bot.entity.aSoul.asf.tool;

import lombok.Getter;
import sereinfish.bot.entity.bili.entity.info.follow.Follow;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 成分检查
 */
@Getter
public class IngredientChecking {
    private String api = "https://tools.asoulfan.com/api/cfj/?name=";
    private Data data;

    public IngredientChecking(String name) throws IOException {
        String json = OkHttpUtils.getStr(api + name);
        data = MyYuQ.toClass(json, Data.class);
    }

    @Getter
    public class Data{
        int code;
        String message;
        int ttl;

        Follow.Data.List data;

        @Getter
        public class List{
            sereinfish.bot.entity.bili.entity.info.Data[] list;
        }
    }
}
