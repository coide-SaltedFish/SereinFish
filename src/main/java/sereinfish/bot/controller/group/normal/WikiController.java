package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Wiki相关指令
 */
@GroupController
public class WikiController {

    @Action(".wiki {key}")
    public Message wiki(String key){
        String url = "https://minecraft-zh.gamepedia.com/index.php?search=";
        try {
            url += URLEncoder.encode(key,"utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),"[" + key + "] 查询失败",e);
            return MyYuQ.getMif().text("[" + key + "] 查询失败").toMessage();
        }
        return MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard("Wiki：" + key,key + " - Minecraft Wiki，最详细的官方我的世界百科",
                "https://images.wikia.com/minecraft_zh_gamepedia/images/b/bc/Wiki.png",url)).toMessage();
    }

}
