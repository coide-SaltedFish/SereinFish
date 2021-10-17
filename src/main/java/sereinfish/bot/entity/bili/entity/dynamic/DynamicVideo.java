package sereinfish.bot.entity.bili.entity.dynamic;

import lombok.Getter;

@Getter
public class DynamicVideo {
    long aid;
    long ctime;
    long cid;
    String desc;//视频简介
    String dynamic;//动态内容
    String pic;//封面
    String share_subtitle;//观看次数
    String short_link;//视频短链
    String title;//标题
}
