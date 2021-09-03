package sereinfish.bot.entity.pixiv;

import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.entity.pixiv.entity.Rank;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;
import java.util.Date;

public class Pixiv {

    //排行榜模式
    public static final String RANK_MODE_DAY = "day";
    public static final String RANK_MODE_WEEK = "week";
    public static final String RANK_MODE_MONTH = "month";

    /**
     * 抓tag
     * @return
     */
    public static PixivEntity getIllust(long id) throws IOException {
        String api = "https://api.obfs.dev/api/pixiv/illust?id=" + id;
        String json = OkHttpUtils.getStr(api);
        PixivEntity pixivEntity = MyYuQ.toClass(json, PixivEntity.class);

        return pixivEntity;
    }

    /**
     * 获取pixiv排行榜
     * @param mode
     * @param page
     * @param size
     * @throws IOException
     */
    public static Rank getRank(String mode, int page, int size) throws IOException {
        String api = "https://api.obfs.dev/api/pixiv/rank?mode=" + mode
                + "&date=" + Time.dateToString(new Date().getTime() - (24 * 60 * 60 * 1000), "yyyy-MM-dd")
                + "&page=" + page
                + "&size=" + size;
        SfLog.getInstance().d(Pixiv.class, "开始获取数据：" + api);
        String json = OkHttpUtils.getStr(api);
        return MyYuQ.toClass(json, Rank.class);
    }

    /**
     * 判断是否包含r18标签
     * @param tags
     * @return
     */
    public static boolean isR18(Illust.Tag tags[]){
        for (Illust.Tag tag:tags){
            if (tag.getName() != null && tag.getName().equals("R-18")){
                return true;
            }
            if (tag.getTranslated_name() != null && tag.getTranslated_name().equals("R-18")){
                return true;
            }
        }
        return false;
    }
}
