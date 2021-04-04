package sereinfish.bot.entity.sauceNAO.sauce;

import lombok.Getter;

import java.util.Map;

/**
 * results项
 */
@Getter
public class Result {
    Header header;
    Map<String,Object> data;

    @Getter
    public class Header{
        String similarity;//相似性,百分比
        String thumbnail;//缩略图，链接
        int index_id;//分类序号
        String index_name;//序号名称
        int dupes;//不知道是啥，被这搜索结果迷惑的人？
    }

//    public class Data{
//        String[] ext_urls;//外部链接，结果的
//        String title;//标题
//        String da_id;//
//        String author_name;
//        String author_url;
//    }


    @Override
    public String toString() {
        String s =  "Result:" +
                "\n相似度：" + header.getSimilarity() +
                "\n缩略图：" + header.getThumbnail();
        for (Map.Entry<String,Object> entry:data.entrySet()){
            s += "\n" + entry.getKey() + ":" + entry.getValue();
        }
        return s;
    }
}
