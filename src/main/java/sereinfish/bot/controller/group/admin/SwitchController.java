package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.ArrayList;

/**
 * 配置开关命令
 */
@GroupController
//TODO：支持私聊
public class SwitchController {

    /**
     * 权限检查
     */
    @Before
    public GroupConf before(Group group, Member sender){
        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.ADMIN)) { //权限检查
//            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
//            msg.setReply(message.getSource());
//            throw msg.toThrowable();
            throw new DoNone();
        }

        return GroupConfManager.getInstance().get(group.getId());
    }

    @Action("\\[Bb]ot\\ {state}")
    @QMsg(mastAtBot = true, reply = true)
    public Message enableBot(GroupConf groupConf, Member sender, boolean  state){
        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.MASTER)) { //权限检查
            throw new DoNone();
        }
        groupConf.setEnable(state);
        GroupConfManager.getInstance().put(groupConf);
        return MyYuQ.getMif().text("Bot启用：" + state).toMessage();
    }

    @Action("开关控制 {groupName} {name} {state}")
    @QMsg(mastAtBot = true, reply = true)
    public Message switchController(GroupConf groupConf , String groupName, String name, boolean state){
        GroupConf.Control control = groupConf.getControl(groupName,name);
        if (control == null){
            return MyYuQ.getMif().text("[" + groupName + "][" + name + "]未找到").toMessage();
        }else {
            if (control.getValue() instanceof Boolean){
                if(groupConf.setControlValue(groupName, name, state)){
                    return MyYuQ.getMif().text("成功,[" + groupName + "]->[" + name + "]设置为[" + state + "]").toMessage();
                }else {
                    return MyYuQ.getMif().text("失败：[" + groupName + "]->[" + name + "]设置为[" + state + "]").toMessage();
                }

            }else {
                return MyYuQ.getMif().text("失败：" + control.getValue().getClass().getSimpleName() + " No Boolean").toMessage();
            }
        }
    }

    @Action("开关控制 ?")
    @Synonym("开关控制 ？")
    @QMsg(mastAtBot = true, reply = true)
    public Message switchControllerGroupNameHelp(GroupConf groupConf){
        ArrayList<String> l = groupConf.getGroupNames();
        int line = 6;
        int page = 1;
        int maxPage = l.size() / line + 1;

        ArrayList<String> list = new ArrayList<>();
        for (int i = (page - 1) * line; i < l.size() && i < page * line; i++){
            list.add(l.get(i));
        }

        return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("SereinFish 开关列表[" + page + "/" + maxPage + "]", "[" + page + "/" + maxPage + "]开关列表",
                "http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640", list.toArray(new String[0]))).toMessage();
    }

    @Action("开关控制 ? {pageStr}")
    @Synonym("开关控制 ？ {pageStr}")
    @QMsg(mastAtBot = true, reply = true)
    public Message switchControllerGroupNameHelp(GroupConf groupConf, String pageStr){
        int page;
        try {
            page = Integer.valueOf(pageStr);
        }catch (Exception e){
            return Message.Companion.toMessageByRainCode("页数错误：" + pageStr);
        }

        ArrayList<String> l = groupConf.getGroupNames();
        int line = 6;
        int maxPage = l.size() / line + 1;

        if (maxPage < page){
            return Message.Companion.toMessageByRainCode("页数过大，最大页数：" + maxPage);
        }
        if (page < 1){
            return Message.Companion.toMessageByRainCode("页数过小，最小页数：" + 1);
        }

        ArrayList<String> list = new ArrayList<>();
        for (int i = (page - 1) * line; i < l.size() && i < page * line; i++){
            list.add(l.get(i));
        }

        return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("SereinFish 开关列表[" + page + "/" + maxPage + "]", "[" + page + "/" + maxPage + "]开关列表",
                "http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640", list.toArray(new String[0]))).toMessage();
    }

    @Action("开关控制 {groupName} ?")
    @Synonym("开关控制 {groupName} ？")
    @QMsg(mastAtBot = true, reply = true)
    public Message switchControllerHelp(GroupConf groupConf, String groupName){
        ArrayList<String> l = groupConf.getGroupControlNames(groupName);
        if (l == null){
            return MyYuQ.getMif().text("未找到开关组：" + groupName).toMessage();
        }

        int line = 6;
        int page = 1;
        int maxPage = l.size() / line + 1;

        ArrayList<String> list = new ArrayList<>();
        for (int i = (page - 1) * line; i < l.size() && i < page * line; i++){
            list.add(l.get(i));
        }

        return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("SereinFish [" + groupName + "]开关列表[" + page + "/" + maxPage + "]",
                        "[" + page + "/" + maxPage + "][" + groupName + "]",
                "http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640", list.toArray(new String[0]))).toMessage();
    }

    @Action("开关控制 {groupName} ? {pageStr}")
    @Synonym("开关控制 {groupName} ？ {pageStr}")
    @QMsg(mastAtBot = true, reply = true)
    public Message switchControllerHelp(GroupConf groupConf, String groupName, String pageStr){
        int page;
        try {
            page = Integer.valueOf(pageStr);
        }catch (Exception e){
            return Message.Companion.toMessageByRainCode("页数错误：" + pageStr);
        }

        ArrayList<String> l = groupConf.getGroupControlNames(groupName);
        if (l == null){
            return MyYuQ.getMif().text("未找到开关组：" + groupName).toMessage();
        }

        int line = 6;
        int maxPage = l.size() / line + 1;

        if (maxPage < page){
            return Message.Companion.toMessageByRainCode("页数过大，最大页数：" + maxPage);
        }
        if (page < 1){
            return Message.Companion.toMessageByRainCode("页数过小，最小页数：" + 1);
        }

        ArrayList<String> list = new ArrayList<>();
        for (int i = (page - 1) * line; i < l.size() && i < page * line; i++){
            list.add(l.get(i));
        }

        return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("SereinFish ["  + groupName + "]开关列表[" + page + "/" + maxPage + "]",
                        "[" + page + "/" + maxPage + "][" + groupName + "]",
                "http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640", list.toArray(new String[0]))).toMessage();
    }
}
