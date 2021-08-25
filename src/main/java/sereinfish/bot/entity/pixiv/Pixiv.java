package sereinfish.bot.entity.pixiv;

import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

public class Pixiv {

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
