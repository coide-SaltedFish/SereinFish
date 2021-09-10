package sereinfish.bot.entity.bili.live.entity.live;

import lombok.Getter;
import lombok.var;

/**
 * 直播间信息
 */
@Getter
public class LiveRoom {
    public static final int ROOM_STATUS_OPEN = 1;

    public static final int LIVE_STATUS_OPEN = 1;
    public static final int LIVE_STATUS_CLOSE = 0;

    public static final int ROUND_STATUS_OPEN = 1;
    public static final int ROUND_STATUS_CLOSE = 0;

    int roomStatus;
    int liveStatus;

    String url;
    String title;
    String cover;
    int online;
    long roomid;
    int roundStatus;
    int broadcast_type;

}
