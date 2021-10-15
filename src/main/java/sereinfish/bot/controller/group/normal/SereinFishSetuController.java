package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.sereinfish.api.SereinFishSetu;
import sereinfish.bot.entity.sereinfish.api.msg.ImageItem;
import sereinfish.bot.entity.sereinfish.api.msg.re.ImageList;
import sereinfish.bot.entity.sereinfish.api.msg.re.Msg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;

import java.io.IOException;

@GroupController
public class SereinFishSetuController {
    @Before(except = "postSetu")
    public void before(Group group, GroupConf groupConf, Member sender){
        //权限检查
        int authority = groupConf.getLoliconPermissions();
        if (!Permissions.getInstance().authorityCheck(group, sender, authority)){
            String tipMsg = "权限不足,所需权限（" + Permissions.getInstance().getAuthorityName(authority) + "）";
            group.sendMessage(new Message().lineQ().at(sender).text("\n").text(tipMsg));
            SfLog.getInstance().w(this.getClass(), tipMsg);
            throw new DoNone();
        }
        //检查群功能是否启用
        if (!groupConf.isLoliconEnable()){
            throw new DoNone();
        }
    }

    @Action("涩图")
    @QMsg(mastAtBot = true)
    public void setu(GroupConf groupConf, Group group){
        SereinFishSetu.Request request = new SereinFishSetu.Request();

        //r18
        int type = SereinFishSetu.Request.TYPE_NO_R18;
        if (groupConf.isPlainAndR18Enable()){
            type = SereinFishSetu.Request.TYPE_ALL;
        }
        if (groupConf.isSetuR18Enable()){
            type = SereinFishSetu.Request.TYPE_R18;
        }
        request.setType(type);

        setu(request, group, groupConf);
    }

    @Action("涩图 {num}")
    @QMsg(mastAtBot = true)
    public void setu(GroupConf groupConf, Group group, int num){
        SereinFishSetu.Request request = new SereinFishSetu.Request();
        request.setNum(num);
        //r18
        int type = SereinFishSetu.Request.TYPE_NO_R18;
        if (groupConf.isPlainAndR18Enable()){
            type = SereinFishSetu.Request.TYPE_ALL;
        }
        if (groupConf.isSetuR18Enable()){
            type = SereinFishSetu.Request.TYPE_R18;
        }
        request.setType(type);

        setu(request, group, groupConf);
    }

    @Action("涩图查询 {key}")
    @QMsg(mastAtBot = true, reply = true)
    public Message setu(GroupConf groupConf, Group group, String key, @PathVar(3) String numStr){
        int num = 1;

        try{
            num = Integer.decode(numStr);
        }catch (NumberFormatException e){
            SfLog.getInstance().e(this.getClass(), e);
            return Message.Companion.toMessageByRainCode(numStr + "?不认识\n<Rain:Image:5D6083D0459F5596CB995088E949B71D.jpg>");
        }

        if (num < 1){
            group.sendMessage(new Message().lineQ().text(MyYuQ.getBotName() + "发现了一个错误，已自动纠正：[" + numStr + "]->[" + num + "]"));
        }

        if (num > 10){
            group.sendMessage(Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>"));
        }

        SereinFishSetu.Request request = new SereinFishSetu.Request();
        request.setKey(key);
        //r18
        int type = SereinFishSetu.Request.TYPE_NO_R18;
        if (groupConf.isPlainAndR18Enable()){
            type = SereinFishSetu.Request.TYPE_ALL;
        }
        if (groupConf.isSetuR18Enable()){
            type = SereinFishSetu.Request.TYPE_R18;
        }
        request.setType(type);
        request.setNum(num);

        setu(request, group, groupConf);

        return new Message().lineQ().text("にゃ～").getMessage();
    }

    @Action("涩图提交 {pid}")
    @Synonym("提交涩图 {pid}")
    @QMsg(mastAtBot = true, reply = true)
    public String postSetu(Member sender, long pid){
        try {
            Msg msg = SereinFishSetu.postSetu(pid, sender.getId());
            if (msg.getCode() == Msg.SUCCESS){
                return "成功：" + msg.getMessage();
            }else {
                return "失败：" + msg.getMessage();
            }
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return "出现了错误:" + e.getMessage();
        }
    }

    /**
     * 发图
     * @param request
     * @param group
     * @param groupConf
     */
    public void setu(SereinFishSetu.Request request, Group group, GroupConf groupConf){
        try {
            ImageList imageList = SereinFishSetu.getSetu(request);
            if (imageList.getCode() == ImageList.SUCCESS){
                for (ImageItem imageItem:imageList.getImageItems()){
                    Message message = Message.Companion.toMessageByRainCode("<Rain:Image:" + imageItem.getMd5() + ".png>");
                    message.setRecallDelay((long) groupConf.getSetuReCallTime() * 1000);
                    group.sendMessage(message);
                }
            }else {
                group.sendMessage(new Message().lineQ().text("出现了错误:" + imageList.getMessage()).getMessage());
            }
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            group.sendMessage(new Message().lineQ().text("出现了错误:" + e.getMessage()).getMessage());
        }
    }
}
