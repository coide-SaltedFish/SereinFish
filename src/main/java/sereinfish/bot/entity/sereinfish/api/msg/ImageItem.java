package sereinfish.bot.entity.sereinfish.api.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.entity.pixiv.entity.Illust;

@AllArgsConstructor
@Getter
@Setter
public class ImageItem {
    String title;
    String caption;

    long uid;
    long pid;

    boolean isR18;
    String md5;
    String url;

    Illust.Tag[] tags;
}
