package sereinfish.bot.entity.bili.live.entity.info.vip;

import lombok.Getter;

/**
 * 大会员信息
 */
@Getter
public class Vip {
    int type;
    int status;
    long due_date;
    int vip_pay_type;
    int theme_type;

    Label label;

    int avatar_subscript;
    String nickname_color;
    int role;
    String avatar_subscript_url;


    @Getter
    public class Label{
        String path;
        String text;
        String label_theme;
        String text_color;
        int bg_style;
        String bg_color;
        String border_color;
    }
}
