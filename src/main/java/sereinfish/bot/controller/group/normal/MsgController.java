package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 一些消息功能
 */
@GroupController
public class MsgController {

    private int rd = 20;

    @Action("好耶")
    public Message haoye(){
        if (MyYuQ.getRandom(1, 100) > rd){
            return new Message().lineQ().text("禁止好耶").getMessage();
        }
        throw new SkipMe();
    }

    @Action("\\^(禁止)+好耶$\\")
    public Message jinZhiHaoye(Message message){
        if (MyYuQ.getRandom(1, 100) > rd){
            return new Message().lineQ().text("禁止").getMessage().plus(message);
        }
        throw new SkipMe();
    }
}
