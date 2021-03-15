package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作消息数据库的命令
 */
@GroupController
public class MsgDBController extends QQController {
    private Group group;
    private Member sender;
    private Message message;

    private int maxTime = 15000;

    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        this.group = group;
        this.sender = sender;
        this.message = message;

        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();

            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("\\[!！.]读消息\\")
    public Message readMsg(Message message){
        GroupHistoryMsg groupHistoryMsg = null;

        if(message.getReply() == null){
            return MyYuQ.getMif().text("消息为空").toMessage();
        }

        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),message.getReply().getId());
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage();
    }

    @Action("\\[!！.]读消息\\ {group} {qq} {id}")
    public Message readMsg(long group, long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group,qq,id);
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage();
    }

    @Action("\\[!！.]读消息\\ {id}")
    public Message readMsg(int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),id);
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage();
    }

    @Action("\\[!！.]读消息\\ {qq} {id}")
    public Message readMsg(long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),qq,id);
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage();
    }

    @Action("\\[!！.]发消息\\")
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

    @Action("\\[!！.]发消息\\ {group}")
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

    @Action("\\[!！.]发消息\\ {group} {qq} {id}")
    public Message sendMsg(long group, long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group,qq,id);
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg());
    }

    @Action("\\[!！.]发消息\\ {qq} {id}")
    public Message sendMsg(long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),qq,id);
            if (groupHistoryMsg == null){
                return MyYuQ.getMif().text("找不到该消息").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        return Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg());
    }

    @Action("\\[!！.]图片url\\")
    public Message getImageURL(Message message, ContextSession session){
        reply("请发送图片");
        Message msg = session.waitNextMessage(maxTime);
        boolean flag = true;

        for (MessageItem messageItem : msg.getBody()){
            String msg_str = messageItem.toPath();
            if (Pattern.matches("img_\\{.*}.jpg",msg_str)){
                flag = false;
                String uuid = msg_str.split("img_\\{|\\}.jpg")[1].replace("-","");
                return MyYuQ.getMif().text("http://gchat.qpic.cn/gchatpic_new/0/-0-" + uuid + "/0").toMessage();
            }
        }

        if (flag){
            return MyYuQ.getMif().text("未发现图片").toMessage();
        }
        throw new DoNone();
    }

    @Action("\\[!！.]最近消息\\ {qq}")
    public Message newMsg(long qq){
        GroupHistoryMsg groupHistoryMsg = null;
        try{
           groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().queryLast(group.getId(), qq);

           if(groupHistoryMsg == null){
               return MyYuQ.getMif().text("找不到消息记录").toMessage();
           }

        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
        }
        String msg = "时间：" + Time.dateToString(new Date(groupHistoryMsg.getTime()),Time.LOG_TIME) + "\nQQ:" + groupHistoryMsg.getQq() + "\nID:" + groupHistoryMsg.getId() +
                "\n内容如下：";
        group.sendMessage(Message.Companion.toMessageByRainCode(msg));
        return Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg());
    }
}
