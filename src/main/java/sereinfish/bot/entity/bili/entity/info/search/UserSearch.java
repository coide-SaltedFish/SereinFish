package sereinfish.bot.entity.bili.entity.info.search;

import lombok.Getter;
import sereinfish.bot.entity.bili.entity.info.Data;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 用户搜索
 */
@Getter
public class UserSearch {
    private SearchRes searchRes;
    private int page = 1;

    public UserSearch(String name) throws IOException {
        searchRes = MyYuQ.toClass(OkHttpUtils.getStr("https://api.bilibili.com/x/web-interface/search/type?search_type=bili_user&keyword=" + name + "&page=" + page), SearchRes.class);
    }

    @Getter
    public class SearchRes{
        int code;
        String message;
        int tll;

        Data data;

        @Getter
        public class Data{
            String seid;
            int page;
            int pagesize;
            int numResults;
            int numPages;
            String suggest_keyword;
            String rqt_type;
            sereinfish.bot.entity.bili.entity.info.Data[] result;
        }
    }
}
