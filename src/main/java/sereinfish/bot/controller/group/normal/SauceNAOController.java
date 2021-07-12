package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.sauceNAO.SauceNaoAPI;
import sereinfish.bot.entity.sauceNAO.sauce.SauceNAO;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

@GroupController
public class SauceNAOController extends QQController {
    private int maxTime = 25 * 1000;//超时

    @Before
    public void before(Group group){
        //判断SauceNAO是否启用
        GroupConf conf = GroupConfManager.getInstance().get(group.getId());
        if (!(Boolean) conf.getControl(GroupControlId.CheckBox_SauceNAOEnable).getValue()){
            throw new DoNone();
        }
    }

    @Action("\\[.!！]图片搜索$\\")
    public Message SauceNAO(Group group, ContextSession session){
        GroupConf conf = GroupConfManager.getInstance().get(group.getId());

        reply("请发送图片，多张算作一张");
        com.icecreamqaq.yuq.message.Message msg = session.waitNextMessage(maxTime);
        boolean flag = true;

        String url = "";

        for (MessageItem messageItem : msg.getBody()){
            String msg_str = messageItem.toPath();
            if (Pattern.matches("img_\\{[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}}\\..{3}",msg_str)){
                flag = false;
                String uuid = msg_str.split("img_\\{|}")[1].replace("-","");
                url = "http://gchat.qpic.cn/gchatpic_new/0/-0-" + uuid + "/0";
            }
        }

        if (flag || url.trim().equals("")){
            return MyYuQ.getMif().text("未发现图片").toMessage();
        }
        SauceNaoAPI sauceNaoAPI = new SauceNaoAPI((String) conf.getControl(GroupControlId.Edit_SauceNAOApiKey).getValue(),url);

        SauceNAO sauceNAO = null;
        try {
            sauceNAO = sauceNaoAPI.search();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(sauceNAO.toString()).toMessage();
    }

    @Action("\\[.!！]搜图$\\ {img}")
    public Message SauceNAO_1(Group group, Image img){
        GroupConf conf = GroupConfManager.getInstance().get(group.getId());

        SauceNaoAPI sauceNaoAPI = new SauceNaoAPI((String) conf.getControl(GroupControlId.Edit_SauceNAOApiKey).getValue(),img.getUrl());

        SauceNAO sauceNAO = null;
        try {
            sauceNAO = sauceNaoAPI.search();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
        return MyYuQ.getMif().text(sauceNAO.toString()).toMessage();
    }

    @Catch(error = SocketTimeoutException.class)
    public void socketTimeoutException(Group group, SocketTimeoutException socketTimeoutException){
        group.sendMessage(MyYuQ.getMif().text("错误：" + socketTimeoutException).toMessage());
    }

    @Catch(error = SocketException.class)
    public void socketException(Group group, SocketException socketException){
        group.sendMessage(MyYuQ.getMif().text("错误：" + socketException).toMessage());
    }
}
