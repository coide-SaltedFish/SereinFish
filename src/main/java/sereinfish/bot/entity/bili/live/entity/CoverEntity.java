package sereinfish.bot.entity.bili.live.entity;

import lombok.Getter;

/**
 * 直播间封面
 * https://api.live.bilibili.com/room/v1/Cover/get_list?room_id=3579351
 * {
 *     "code": 0,
 *     "msg": "ok",
 *     "message": "ok",
 *     "data": [
 *         {
 *             "id": 938511,
 *             "audit_status": 1,
 *             "audit_reason": "",
 *             "url": "https://i0.hdslb.com/bfs/live/7858205655589d066b7f43f68dbabae2a2272225.jpg",
 *             "select_status": 1,
 *             "type": "cover"
 *         }
 *     ]
 * }
 */
@Getter
public class CoverEntity {
    private int code;
    private String msg;
    private String message;

    @Getter
    class Data{
        private long id;
        private int audit_status;//审核状态
        private int audit_reason;//审核原因
        private String url;//封面链接
        private int select_status;
        private String type;
    }
}
