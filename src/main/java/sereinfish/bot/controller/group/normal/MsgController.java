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
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.msg.LeavingMessage;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.permissions.Permissions;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 一些消息功能
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "消息")
public class MsgController extends QQController {
    private int maxTime = 25 * 1000;
    private int rd = 60;

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

    @Action("图片url")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "获取QQ消息图片链接", usage = "[!！.]图片url", description = "Bot会读取数据库本群消息内容并解析后发出", permission = Permissions.ADMIN)
    public String getImageURL(ContextSession session, Message message, Group group){
        Image image = null;
        //检查本消息是否包含
        if (message.getReply() == null){
            for (MessageItem messageItem:message.getBody()){
                if (messageItem instanceof Image){
                    image = (Image) messageItem;
                    break;
                }
            }
        }else {
            //检查是否回复
            GroupHistoryMsg groupHistoryMsg = null;
            try {
                groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(), message.getReply().getId());
                if (groupHistoryMsg == null){
                    return "找不到该消息，可能是消息未被记录:" + message.getReply().getId();
                }
                Message replayMsg = groupHistoryMsg.getMessage();
                for (MessageItem messageItem:replayMsg.getBody()){
                    if (messageItem instanceof Image){
                        image = (Image) messageItem;
                        break;
                    }
                }
            } catch (SQLException e) {
                SfLog.getInstance().e(this.getClass(),e);
                group.sendMessage("发生错误了：" + e.getMessage());
            }
        }

        if (image == null){
            //要求发送
            reply("请发送图片");
            Message msg = session.waitNextMessage(maxTime);

            for (MessageItem messageItem : msg.getBody()){
                if (messageItem instanceof Image){
                    image = (Image) messageItem;
                    return "http://gchat.qpic.cn/gchatpic_new/0/-0-"
                            + image.getId().substring(0, image.getId().lastIndexOf(".")).toUpperCase() + "/0";
                }
            }

            return "未发现图片";
        }else {
            return "http://gchat.qpic.cn/gchatpic_new/0/-0-"
                    + image.getId().substring(0, image.getId().lastIndexOf(".")).toUpperCase() + "/0";
        }
    }
}
