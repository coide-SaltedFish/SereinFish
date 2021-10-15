package sereinfish.bot.entity.sauceNAO.sauce;

import lombok.Getter;
import sereinfish.bot.mlog.SfLog;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * results项
 */
@Getter
public class Result {
    Header header;
    Data data;

    @Getter
    public class Header{
        String similarity;//相似性,百分比
        String thumbnail;//缩略图，链接
        int index_id;//分类序号
        String index_name;//序号名称
        int dupes;//不知道是啥，被这搜索结果迷惑的人？

        public int getPage(){
            Pattern pattern=Pattern.compile("_p[0-9]+",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(index_name);
            if (matcher.find()){
                String pStr = matcher.group();
                pattern=Pattern.compile("[0-9]+",Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(pStr);
                if (matcher.find()){
                    pStr = matcher.group();
                    try {
                        return Integer.decode(pStr);
                    }catch (NumberFormatException e){
                        SfLog.getInstance().w(this.getClass(), "页数转换错误：" + pStr);
                        return 1;
                    }
                }
            }

            return 1;
        }
    }

    @Getter
    public class Data{
        String[] ext_urls;//外部链接，结果的
        String title = "无";//标题
        long pixiv_id = 0;//
        String member_name = "无";
        long member_id = 0;

        Object creator;//创造者
        String material;//素材
        String original;//原稿
        String characters;//特征
        String source;
        long danbooru_id;
        long gelbooru_id;

        String eng_name;
        String jp_name;

        long da_id;
        String author_name;
        String author_url;

        long md_id;
        long mu_id;
        long mal_id;
        String part;
        String artist;
        String author;

        long fa_id;

        long yandere_id;
    }
}
