package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.log.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;

@GroupController
public class AdminController {
    private Group group;
    private Member sender;
    private Message message;

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
    public String readMsg(Message message){
        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group,sender.getId(),message.getReply().getId());
            if (groupHistoryMsg == null){
                return "找不到该消息";
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return "操作失败：" + e.getMessage();
        }
        return groupHistoryMsg.getMsg();
    }
}
