package sereinfish.bot.entity.bili.live.entity.info;

import lombok.Getter;

/**
 * 用户主页信息
 *
 * https://api.bilibili.com/x/space/acc/info?mid=uid&jsonp=jsonp
 */
@Getter
public class UserInfo {
    int code;
    String message;
    int ttl;
    Data data;
}
