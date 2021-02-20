package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;

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

    @Action(".读消息")
    public void readMsg(Message message){
        GroupHistoryMsg groupHistoryMsg = null;

        if(message.getReply() == null){
            return;
        }

        try {
            //TODO:更改发送者为回复的消息的发送者
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),sender.getId(),message.getReply().getId());
            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("找不到该消息").toMessage());
                return;
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage());
            return;
        }
        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage());
    }

    @Action(".读消息 {group} {qq} {id}")
    public void readMsg(long group, long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group,qq,id);
            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("找不到该消息").toMessage());
                return;
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage());
            return;
        }
        MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage());
        return;
    }

    @Action(".读消息 {qq} {id}")
    public void readMsg(long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),qq,id);
            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("找不到该消息").toMessage());
                return;
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage());
            return;
        }
        MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text(groupHistoryMsg.getMsg()).toMessage());
        return;
    }

    @Action(".发消息")
    public void sendMsg(Message message,ContextSession session){
        Message message1 = MyYuQ.getMif().text("请输入消息内容").toMessage();
        message1.setReply(message.getSource());
        reply(message1);

        try{
            Message reMsg = session.waitNextMessage(maxTime);
            MyYuQ.sendGroupMessage(group,reMsg);
        }catch (Exception e){
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("已超时取消").toMessage());
        }
    }

    @Action(".发消息 {group}")
    public void sendMsg(long group,Message message,ContextSession session){
        Group g = MyYuQ.getYuQ().getGroups().get(group);
        if (g != null){
            Message message1 = MyYuQ.getMif().text("请输入消息内容").toMessage();
            message1.setReply(message.getSource());
            reply(message1);

            try{
                Message reMSg = session.waitNextMessage(maxTime);
                if(MyYuQ.sendGroupMessage(g,reMSg)){
                    Message message2 = MyYuQ.getMif().text("发送成功").toMessage();
                    message2.setReply(message.getReply());
                    MyYuQ.sendGroupMessage(this.group,message2);
                }
            }catch (Exception e){
                MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("已超时取消").toMessage());
            }

        }else {
            Message message2 = MyYuQ.getMif().text("找不到群[" + group + "]").toMessage();
            message2.setReply(message.getReply());
            MyYuQ.sendGroupMessage(this.group,message2);
        }

    }

    @Action(".发消息 {group} {qq} {id}")
    public void sendMsg(long group, long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group,qq,id);
            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("找不到该消息").toMessage());
                return;
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage());
            return;
        }
        MyYuQ.sendGroupMessage(this.group,Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg()));
    }

    @Action(".发消息 {qq} {id}")
    public void sendMsg(long qq, int id){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),qq,id);
            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("找不到该消息").toMessage());
                return;
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage());
            return;
        }
        MyYuQ.sendGroupMessage(this.group,Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg()));
    }

    @Action(".图片url")
    public void getImageURL(Message message){

        //TODO:等下周吧
        message.getReply().getSender();

        GroupHistoryMsg groupHistoryMsg = null;
        //groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),qq,message.getReply().getId());
        if (groupHistoryMsg == null){
            //MyYuQ.sendGroupMessage(this.group,MyYuQ.getMif().text("找不到该消息").toMessage());
            return;
        }
        //Message msg = group;
    }
}
