package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.superpowers.SuperPower;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.ArrayList;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "随机超能力")
public class SuperPowerController extends QQController {
    private int maxTime = 25000;

    @Before
    public void before(GroupConf groupConf){
        if (!groupConf.isSuperPowerEnable()){
            throw new DoNone();
        }
    }

    @Action("随机超能力")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "随机超能力", usage = "@Bot 随机超能力", description = "获得一个随机的超能力")
    public void power(Group group, Member sender, BotActionContext context){
        SuperPower superPower = SuperPower.read();

        SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(context);
        ArrayList<SFMessage.SFMessageEntity> sfMessages = SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact, superPower.msg(sender));
        MyYuQ.sendSFMessage(group, sfMessages);
    }

    @Action("添加超能力")
    @Synonym("超能力添加")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "添加超能力", usage = "@Bot 添加超能力", description = "给bot添加新的超能力选项")
    public String addEffect(GroupConf groupConf, Message message, Group group, Member sender, ContextSession session){
        if (!groupConf.isSuperPowerAddEnable()){
            throw new DoNone();
        }

        SuperPower superPower = SuperPower.read();

        if (superPower.isBlackList(sender.getId())){
            throw new DoNone();
        }

        Message message1 = MyYuQ.getMif().text("请输入超能力效果").toMessage();
        message1.setReply(message.getSource());
        reply(message1);

        try{
            String msg = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            return superPower.addEffect(group, sender, msg);
        }catch (WaitNextMessageTimeoutException e){
            return "已超时取消";
        }
    }

    @Action("添加超能力代价")
    @Synonym("超能力代价添加")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "添加超能力代价", usage = "@Bot 添加超能力代价", description = "给bot添加新的超能力代价选项")
    public String addDeBuff(GroupConf groupConf, Message message, Group group, Member sender, ContextSession session){
        if (!groupConf.isSuperPowerAddEnable()){
            throw new DoNone();
        }

        SuperPower superPower = SuperPower.read();

        if (superPower.isBlackList(sender.getId())){
            throw new DoNone();
        }

        Message message1 = MyYuQ.getMif().text("请输入超能力代价").toMessage();
        message1.setReply(message.getSource());
        reply(message1);

        try{
            String msg = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            return superPower.addDeBuff(group, sender, msg);
        }catch (WaitNextMessageTimeoutException e){
            return "已超时取消";
        }
    }
}
