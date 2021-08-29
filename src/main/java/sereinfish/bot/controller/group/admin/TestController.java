package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;

@GroupController
public class TestController {

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
}
