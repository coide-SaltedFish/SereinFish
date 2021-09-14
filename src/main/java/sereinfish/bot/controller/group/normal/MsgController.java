package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.msg.LeavingMessage;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一些消息功能
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "消息")
public class MsgController extends QQController {
    private int maxTime = 25 * 1000;
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

    @Action("留言 {sb}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "给某人留言", usage = "@Bot 留言 {指定对象}", description = "给某人留言，将在他下一次冒泡时发出消息")
    public String leavingMessage(Group group, Member sender, Member sb, ContextSession session){
        if (sender.getId() == sb.getId()){
            return "自己不能给自己留言哦";
        }

        if (sb.getId() == MyYuQ.getYuQ().getBotId()){
            return MyYuQ.getBotName() + "就在这里哦";
        }

        reply(new Message().lineQ().at(sender).textLine("").text("请输入消息内容(" + (maxTime / 1000) + "s)").getMessage());
        try{
            Message waitMsg = session.waitNextMessage(maxTime);
            try {
                LeavingMessage leavingMessage = LeavingMessage.get(group.getId());
                return leavingMessage.add(group.getId(),
                        new LeavingMessage.Msg(
                                new Date().getTime(),
                                sender.getId(),
                                sb.getId(),
                                Message.Companion.toCodeString(waitMsg)
                        ));

            } catch (IOException e) {
                return "失败：" + e.getMessage();
            }
        }catch (WaitNextMessageTimeoutException e){
            return "已超时取消";
        }
    }

    @Action("删留言 {sb}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "删留言", usage = "@Bot 删留言 {指定对象}", description = "删除给某人留言")
    public String leavingMessageDel(Group group, Member sender, Member sb){
        try {
            LeavingMessage leavingMessage = LeavingMessage.get(group.getId());
            return leavingMessage.delete(group.getId(), sender.getId(), sb.getId());

        } catch (IOException e) {
            return "失败：" + e.getMessage();
        }
    }

    @Action("晚安")
    @QMsg(mastAtBot = true, reply = true)
    public String goodNight(Group group, Member sender){
        try {
            //判断时间
            Date startTime = new SimpleDateFormat(Time.DAY_TIME).parse("23:00:00");
            Date endTime = new Date(startTime.getTime() + 60 * 60 * 8 * 1000);

            if (Time.isEffectiveDate(new Date(), startTime, endTime)){
                long banTime = endTime.getTime() - new Date().getTime();

                if (group.getBot().isAdmin() || group.getBot().isOwner()){
                    group.sendMessage("晚安哦~");
                    if (!sender.isOwner() && !sender.isAdmin()){
                        sender.ban((int) banTime);
                    }
                    return "要保持充足的睡眠哦";
                }else {
                    return "晚安";
                }
            }else {
                return "这个功能在晚上11点到第二天早上7点才启用哦";
            }
        } catch (ParseException e) {
            return "出现了一点错误：" + e.getMessage();
        }
    }
}
