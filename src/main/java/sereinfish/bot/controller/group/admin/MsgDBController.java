package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.database.entity.GroupHistoryMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;

/**
 * 操作消息数据库的命令
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "消息数据库相关", permissions = Permissions.ADMIN)
public class MsgDBController extends QQController {

    @Inject
    GroupHistoryMsgService groupHistoryMsgService;

    private int maxTime = 25000;

    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();

            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("\\^[!！.]读消息$\\")
    @MenuItem(name = "读消息", usage = "[!！.]读消息", description = "返回对应消息的Rain码格式", permission = Permissions.ADMIN)
    public Message readMsg(Group group, Message message){
        if(message.getReply() == null){
            return MyYuQ.getMif().text("消息为空").toMessage();
        }

        GroupHistoryMsg groupHistoryMsg = groupHistoryMsgService.findByGroupAndMid(group.getId(), message.getReply().getId());

        if (groupHistoryMsg == null){
            return MyYuQ.getMif().text("找不到该消息").toMessage();
        }
        return MyYuQ.getMif().text(groupHistoryMsg.getRainCodeMsg()).toMessage();
    }

    @Action("\\^[!！.]查消息$\\ {id}")
    @MenuItem(name = "查消息", usage = "[!！.]查消息 {group} {id}", description = "在数据库中查找对应消息", permission = Permissions.ADMIN)
    public Message readMsg(@PathVar(2) String groupStr, long group, int id){
        if (groupStr != null && !groupStr.equals("")){
            try{
                group = Long.decode(groupStr);
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(), "数据转换失败：" + groupStr);
            }
        }

        GroupHistoryMsg groupHistoryMsg = groupHistoryMsgService.findByGroupAndMid(group, id);
        return MyYuQ.getMif().text(groupHistoryMsg.getRainCodeMsg()).toMessage();
    }

    @Action("\\^[!！.]发消息$\\")
    @MenuItem(name = "发消息", usage = "[!！.]发消息", description = "Bot会读取消息内容并Rain码解析后发出", permission = Permissions.ADMIN)
    public Message sendMsg(Message message,ContextSession session){
        Message message1 = MyYuQ.getMif().text("请输入消息内容").toMessage();
        message1.setReply(message.getSource());
        reply(message1);

        try{
            String reMsg = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            return Message.Companion.toMessageByRainCode(reMsg);
        }catch (WaitNextMessageTimeoutException e){
            return MyYuQ.getMif().text("已超时取消").toMessage();
        }
    }

    @Action("\\^[!！.]发消息$\\ {group}")
    @MenuItem(name = "发消息", usage = "[!！.]发消息 {group}", description = "Bot会读取消息内容并Rain码解析后发出到指定群聊", permission = Permissions.ADMIN)
    public Message sendMsg(long group,Message message,ContextSession session){
        Group g = MyYuQ.getYuQ().getGroups().get(group);
        if (g != null){
            Message message1 = MyYuQ.getMif().text("请输入消息内容").toMessage();
            message1.setReply(message.getSource());
            reply(message1);

            try{
                Message reMSg = session.waitNextMessage(maxTime);
                if(g.sendMessage(reMSg).getId() > 0){
                    Message message2 = MyYuQ.getMif().text("发送成功").toMessage();
                    message2.setReply(message.getReply());
                    return message2;
                }
            }catch (WaitNextMessageTimeoutException e){
                return MyYuQ.getMif().text("已超时取消").toMessage();
            }

        }
        Message message2 = MyYuQ.getMif().text("找不到群[" + group + "]").toMessage();
        message2.setReply(message.getReply());
        return message2;
    }

    @Action("\\^[!！.]最近消息$\\ {qq}")
    @MenuItem(name = "最近消息", usage = "[!！.]最近消息", description = "获取指定对象的最近消息", permission = Permissions.ADMIN)
    public Message newMsg(Group group, long qq){
        GroupHistoryMsg groupHistoryMsg = groupHistoryMsgService.findLastByGroupAndQQ(group.getId(), qq);
        if(groupHistoryMsg == null){
            return MyYuQ.getMif().text("找不到消息记录").toMessage();
        }
        String msg = "时间：" + Time.dateToString(new Date(groupHistoryMsg.getTime()),Time.LOG_TIME) + "\nQQ:" + groupHistoryMsg.getQq() + "\nID:" + groupHistoryMsg.getId() +
                "\n内容如下：";
        group.sendMessage(Message.Companion.toMessageByRainCode(msg));
        return Message.Companion.toMessageByRainCode(groupHistoryMsg.getRainCodeMsg());
    }
}
