package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;

/**
 * 消息相关命令处理
 */
@GroupController
public class MsgController {
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

        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action(".版本")
    @QMsg(reply = true)
    public Message version(){
        return MyYuQ.getMif().text(MyYuQ.getVersion()).toMessage();
    }

    @Action(".json")
    public Message json(){
        return MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard("桦木原","桦木原Harmoland 是一个正版 Minecraft Java Edition 服务器。我们以玩家们和谐友爱为宗旨，以纯净生存为核心玩法。",
                "https://harmo.redlnn.top/logo.png","https://harmo.redlnn.top")).toMessage();
    }
}
