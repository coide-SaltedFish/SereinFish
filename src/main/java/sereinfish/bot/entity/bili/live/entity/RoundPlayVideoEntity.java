package sereinfish.bot.entity.bili.live.entity;

import lombok.Getter;

/**
 * 直播间轮播视频信息
 * https://api.live.bilibili.com/live/getRoundPlayVideo?room_id=5555734
 * {
 *     "code": 0,
 *     "msg": "ok",
 *     "message": "ok",
 *     "data": {
 *         "cid": 328118259,
 *         "play_time": 18,
 *         "sequence": 99,
 *         "aid": 972692871,
 *         "title": "BV1Ap4y1b7UC-《明日方舟》游戏概念宣传PV-3-P1",
 *         "pid": 1,
 *         "bvid_url": "https://www.bilibili.com/video/BV1Ap4y1b7UC",
 *         "bvid": "BV1Ap4y1b7UC",
 *         "play_url": "https://interface.bilibili.com/v2/playurl?appkey=fb06a25c6338edbc&buvid=&cid=328118259&otype=json&platform=live&qn=80&sign=2ece60392619b458df70f2257c173c39"
 *     }
 * }
 */
@Getter
public class RoundPlayVideoEntity {
    private int code;
    private String msg;
    private String message;
    private Data data;

    @Getter
    class Data{
        private long cid;//0 直播间关闭且无轮播 -2 正在直播
        private int play_time;
        private int sequence;//序列
        private long aid;
        private String title;
        private int pid;
        private String bvid_url;//轮播视频url
        private String bvid;//BV号
        private String play_url;//轮播视频信息链接
    }
}
