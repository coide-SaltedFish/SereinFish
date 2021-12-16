package sereinfish.bot.entity.bili.entity.info;

import lombok.Getter;
import sereinfish.bot.entity.bili.entity.info.vip.Vip;
import sereinfish.bot.entity.bili.entity.live.LiveRoom;

/**
 * 用户数据
 */
@Getter
public class Data {
    long mid;
    String name;
    String uname;
    String sex;
    String face;
    String sign;
    int rank;
    int level;
    int jointime;
    int moral;
    int silence;
    int coins;
    boolean fans_badge;
    FansMedal fans_medal;
    Official official;
    Vip vip;
    LiveRoom live_room;

    @Getter
    public class Official{
        int role;
        String title;
        String desc;
        int type;
    }

    @Getter
    public class FansMedal{
        boolean show;
        boolean wear;
        Medal medal;

        @Getter
        public class Medal{
            long uid;
            long target_id;
            long medal_id;
            int level;
            String medal_name;
            long medal_color;
            int intimacy;
            int next_intimacy;
            int day_limit;
            long medal_color_start;
            long medal_color_end;
            long medal_color_border;
            int is_lighted;
            int light_status;
            int wearing_status;
            int score;
        }
    }
}
