package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.rcon.Rcon;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.net.mc.rcon.ex.AuthenticationException;

import java.io.IOException;

@GroupController
public class McRconCmd extends QQController {

    private int maxTime = 15000;

    @Before
    public GroupConf before(Message message, Member sender, Group group){
        //权限判断
        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.OP)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
        return GroupConfManager.getInstance().get(group.getId());
    }

    @Action("\\[.!！][rR][cC][Ee]$\\ {var}")
    public String rconCmdExecute(GroupConf groupConf, String var){
        //前置检查
        if ((Boolean) groupConf.getControl(GroupControlId.CheckBox_EnableRcon).getValue()){
            if (!(Boolean) groupConf.getControl(GroupControlId.CheckBox_EnableRconCMD).getValue()){
                throw new DoNone();
            }
        }else {
            throw new DoNone();
        }
        //得到Rcon
        Rcon rcon = null;
        if (groupConf.getControl(GroupControlId.SelectRcon).getValue() != null){
            RconConf rconConf = MyYuQ.toClass((String) groupConf.getControl(GroupControlId.SelectRcon).getValue(), RconConf.class);
            if (rconConf != null){
                rcon = RconManager.getInstance().getRcon(rconConf.getID());
            }
        }
        //命令执行
        if (rcon != null){
            try {
                return "命令返回值如下：\n" + rcon.cmd(var).replaceAll("(§.)", "");
            } catch (IOException e) {
                return "命令执行失败：\n" + e.getMessage();
            } catch (AuthenticationException e) {
                return "Rcon连接异常：\n" + e.getMessage();
            }
        }
        return "本群Rcon未连接";
    }

    /**
     * 长命令执行
     * @param groupConf
     * @return
     */
    @Action("\\[.!！][rR][cC][Ee][Ss]$\\")
    public String rconsCmdExecute(ContextSession session, Member sender, GroupConf groupConf){
        //前置检查
        if ((Boolean) groupConf.getControl(GroupControlId.CheckBox_EnableRcon).getValue()){
            if (!(Boolean) groupConf.getControl(GroupControlId.CheckBox_EnableRconCMD).getValue()){
                throw new DoNone();
            }
        }else {
            throw new DoNone();
        }
        reply(MyYuQ.getMif().at(sender).plus(MyYuQ.getMif().text("请输入命令")));

        try{
            String reMsg = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            //得到Rcon
            Rcon rcon = null;
            if (groupConf.getControl(GroupControlId.SelectRcon).getValue() != null){
                RconConf rconConf = MyYuQ.toClass((String) groupConf.getControl(GroupControlId.SelectRcon).getValue(), RconConf.class);
                if (rconConf != null){
                    rcon = RconManager.getInstance().getRcon(rconConf.getID());
                }
            }
            //命令执行
            if (rcon != null){
                try {
                    return "命令返回值如下：\n" + rcon.cmd(reMsg).replaceAll("(§.)", "");
                } catch (IOException e) {
                    return "命令执行失败：\n" + e.getMessage();
                } catch (AuthenticationException e) {
                    return "Rcon连接异常：\n" + e.getMessage();
                }
            }
            return "本群Rcon未连接";
        }catch (WaitNextMessageTimeoutException e) {
            return "已超时取消";
        }
    }
}
