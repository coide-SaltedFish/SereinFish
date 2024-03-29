package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.controller.ActionContext;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.lolicon.Lolicon;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.entity.lolicon.sf.Request;
import sereinfish.bot.entity.lolicon.sf.Response;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.CallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@GroupController
//@Menu(type = Menu.Type.ALL, name = "Lolicon")
public class LoliconController {
    private static boolean isSending = false;//是否正在发送中

    @Before
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

    @Action("\\img_B407F708A2C6A506342098DF7CAC4A57\\")
    @Synonym({"\\[!！.]setu$\\", "\\[!！.]色图$\\","\\img_049231702ACB5E94ECCD953F46E6CAB9\\"})
    //@MenuItem(name = "涩图", usage = "[!！.]setu | [!！.]色图", description = "要一张涩图")
    public void getST(Group group, Message message, GroupConf groupConf){
        getLoliconMsg(group, message, true, groupConf, null, null, 1, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

    @Action("\\img_7CF98559280FD216C5C48AA3D22A8815\\")
    @QMsg(mastAtBot = true)
    public void getST_2(Group group, Message message, GroupConf groupConf){
        getLoliconMsg(group, message, true, groupConf, null, null, 1, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

//    @Action(".来点{key}色图")
//    @Synonym(".来点{key}涩图")
//    public void setu(MySender mySender, String key){
//        //判断是否启用Sf
//        if (mySender.getContact() instanceof Group){
//            Group group = (Group) mySender.getContact();
//            GroupConf conf = GroupConfManager.getInstance().get(group.getId());
//
//            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
//                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),key,1);
//            }
//        }
//    }

    @Action("来{strNum}张{key}色图")
    @Synonym({"来{strNum}张{key}涩图","来{strNum}份{key}涩图"})
    @MenuItem(name = "涩图且指定数量和tag", usage = "@Bot 来{strNum}[张份]{key}[色涩]图", description = "要指定数量和tag的涩图")
    @QMsg(mastAtBot = true)
    public void setuAtBotKeyWord(Group group, Message message, GroupConf groupConf, String strNum, String key){
        int num;
        try {
            if (strNum.equals("")){
                strNum = "1";
            }
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                group.sendMessage(Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:5D6083D0459F5596CB995088E949B71D.jpg>"));
                return;
            }else {
                group.sendMessage(Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:53AF664AB93AAFF6290632025A1B2787.jpg>"));
                return;
            }
        }

        if (num > 2){
            group.sendMessage(new Message().lineQ().text("正在获取~"));
        }

        ArrayList<Message> messages = new ArrayList<>();
        getLoliconMsg(group, message, true, groupConf, null, key, num, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

    @Action("来点{key}色图")
    @Synonym({"来点{key}涩图","来张{key}涩图","来张{key}色图","{key}涩图摩多摩多","{key}色图摩多摩多","{key}涩图摩多","{key}色图摩多"})
    @MenuItem(name = "来点涩图且指定tag", usage = "@Bot 来[点张]{key}[涩色]图 | {key}[色涩]图摩多摩多 | {key}色图摩多", description = "要一张涩图且指定tag")
    @QMsg(mastAtBot = true)
    public void setuAtBot(Group group, Message message, GroupConf groupConf, String key){
        getLoliconMsg(group, message, true, groupConf, null, key, 1, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

    @Action("涩图摩多")
    @Synonym({"涩图摩多摩多","色图摩多摩多","涩图摩多","色图摩多"})
    @MenuItem(name = "要一张涩图", usage = "@Bot [色涩]图摩多 | [色涩]图摩多摩多", description = "要一张涩图")
    @QMsg(mastAtBot = true)
    public void setuAtBotMore(Group group, Message message, GroupConf groupConf){
        getLoliconMsg(group, message, true, groupConf, null, null, 1, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

    @Action("我要{strNum}张色图")
    @Synonym({"我要{strNum}张涩图","来{strNum}份涩图","来{strNum}份色图", "来{strNum}张涩图"})
    @MenuItem(name = "涩图且指定数量", usage = "@Bot 我要{strNum}张[色涩]图 | 来{strNum}份[涩色]图", description = "要指定数量的涩图")
    @QMsg(mastAtBot = true)
    public void setuNum(Group group, Message message, GroupConf groupConf, String strNum){
        int num;
        try {
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                group.sendMessage(Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:5D6083D0459F5596CB995088E949B71D.jpg>"));
                return;
            }else {
                group.sendMessage(Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:53AF664AB93AAFF6290632025A1B2787.jpg>"));
                return;
            }
        }

        if (num > 2){
            group.sendMessage(new Message().lineQ().text("正在获取~"));
        }

        ArrayList<Message> messages = new ArrayList<>();

        getLoliconMsg(group, message, true, groupConf, null, null, num, new CallBack<Message[]>() {
            @Override
            public void callback(Message[] p) {
                for (Message msg:p){
                    group.sendMessage(msg);
                }
            }
        });
    }

    @Action("\\img_0E558CBAE41368A42DB812E7C1D9A172\\")
    @QMsg(mastAtBot = true)
    public Message enableR18(Group group, GroupConf groupConf, Member sender){

        //权限判断
        if (Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            groupConf.setSetuR18Enable(true);
            groupConf.setPlainAndR18Enable(false);
            return Message.Companion.toMessageByRainCode("<Rain:Image:241A6BB95CC02A98CCE648BEE17148D0.jpg>");
        }
        throw new DoNone();
    }

    @Action("\\img_982776A6AA3DD49A9DE457F8EABE4EB0\\")
    @Synonym({"\\img_AE8B71A4EB5E5DF105E9EEA61CE6152D\\", "\\img_B112EE7AB6BB3C96A797B0AB5889BFCC\\"})
    @QMsg(mastAtBot = true)
    public Message enableNoR18(Group group, GroupConf groupConf, Member sender){
        //权限判断
        if (Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            groupConf.setSetuR18Enable(false);
            groupConf.setPlainAndR18Enable(false);
            return Message.Companion.toMessageByRainCode("<Rain:Image:2098B7ECBFDC0C092816EE006E24DB05.jpg>");
        }
        throw new DoNone();
    }

    @Action("\\[lL]olicon混合模式$\\ {state}")
    @MenuItem(name = "Lolicon的混合模式开关", usage = "@Bot [lL]olicon混合模式 {state}", description = "开关Lolicon的混合模式", permission = Permissions.GROUP_ADMIN)
    @QMsg(mastAtBot = true,reply = true)
    public Message r18Blend(Group group, boolean state, GroupConf groupConf, Member sender){
        //权限判断
        if (Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            groupConf.setPlainAndR18Enable(state);
            return Message.Companion.toMessageByRainCode("Lolicon 混合模式：" + state);
        }
        throw new DoNone();
    }

    /**
     * 得到Lolicon消息
     * @param isGroupMsg
     * @param conf
     * @param uids
     * @param keyword
     * @param num
     */
    public void getLoliconMsg(Group group, Message message, boolean isGroupMsg, GroupConf conf, int[] uids, String keyword, int num, CallBack<Message[]> callBack){
        ArrayList<Message> messages = new ArrayList<>();
        if (isSending){
            Message msg = new Message().lineQ().text(" 别急，正在发呢").getMessage();
            msg.setReply(message.getSource());
            messages.add(msg);

            callBack.callback(messages.toArray(new Message[]{}));
            return;
        }

        int max = conf.getSetuSendMaxNum();
        if (num > max){
            num = max;
            messages.add(Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>"));
        }

        if (num <= 0){
            callBack.callback(new Message[]{Message.Companion.toMessageByRainCode("<Rain:Image:22C729AA4F85DE574605FA0D19C6A6B7.jpg>")});
            return;
        }

        isSending = true;
        try {
            getLoliconMsgs(group, isGroupMsg, conf, uids, keyword, num, callBack);
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
            callBack.callback(new Message[]{Message.Companion.toMessageByRainCode("出现了亿点点问题\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>")});
        }
        isSending = false;
    }

    /**
     * 解析Lolicon消息
     * @param isGroupMsg
     * @param conf
     * @param uids
     * @param keyword
     * @param num
     * @return
     */
    private void getLoliconMsgs(Group group, boolean isGroupMsg, GroupConf conf, int[] uids, String keyword, int num, CallBack<Message[]> callBack){
        //普通Lolicon
        Lolicon.Request request = LoliconManager.getRequest(isGroupMsg,conf,keyword,uids, num);
        try {
            SfLog.getInstance().d(LoliconManager.class,"Lolicon 获取中");
            Lolicon lolicon = LoliconManager.getLolicon(request);
            SfLog.getInstance().d(LoliconManager.class,"Lolicon 获取成功");
            if (lolicon.getError().equals("")){

                if (lolicon.getData().size() == 0){
                    isSending = false;
                    callBack.callback(new Message[]{MyYuQ.getMif().text("要求过于奇怪，找不到你想要的图呢").toMessage()});
                    return;
                }

                ArrayList<Message> messages = new ArrayList<>();

                final int[] index = {0};

                for (Lolicon.Setu setu:lolicon.getData()){

                    File file = setu.isR18() ? new File(FileHandle.imageLoliconCachePath,"R18/lolicon_" + setu.getPid()) :
                            new File(FileHandle.imageLoliconCachePath,"lolicon_" + setu.getPid());

                    NetworkLoader.INSTANCE.addTask(new NetworkLoader.Task(group, setu.getUrl(), file, new NetworkLoader.NetworkLoaderListener() {
                        @Override
                        public void start(long len) {

                        }

                        @Override
                        public void success(File file) {
                            try{
                                SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
                                //MD5发送方法
                                String md5 = DigestUtils.md5Hex(new FileInputStream(file));

                                if (conf.isLoliconMD5ImageEnable()){
                                    Message reMsg = Message.Companion.toMessageByRainCode("<Rain:Image:" + md5 + ".jpg>");
                                    if (setu.isR18() || conf.isSetuMastReCallEnable()){
                                        reMsg.setRecallDelay((long) conf.getSetuReCallTime() * 1000);
                                    }
                                    messages.add(reMsg);
                                }else {
                                    //普通发送
                                    Message message;
                                    //上传图片
                                    try {
                                        if (MyYuQ.imageEnableTX(md5)){
                                            message = new Message().lineQ().plus(Message.Companion.toMessageByRainCode("<Rain:Image:" + md5 + ".jpg>")).getMessage();

                                        }else {
                                            message = new Message().lineQ().plus(MyYuQ.uploadImage(group, file)).getMessage();
                                        }
                                        if (setu.isR18() || conf.isSetuMastReCallEnable()){
                                            message.setRecallDelay((long) conf.getSetuReCallTime() * 1000);
                                        }

                                    }catch (Exception e){
                                        SfLog.getInstance().e(this.getClass(), e);
                                        message = new Message().lineQ().text("图片上传失败qwq：" + setu.getPid()).getMessage();
                                    }

                                    messages.add(message);
                                }
                            }catch (Exception e){
                                messages.add(new Message().lineQ().text("图片下载失败：" + e.getMessage()).getMessage());
                            }
                            index[0]++;

                            //是否可以发送了
                            if (index[0] == lolicon.getData().size()){
                                callBack.callback(messages.toArray(new Message[]{}));
                            }
                        }

                        @Override
                        public void fail(Exception e) {
                            messages.add(new Message().lineQ().text("图片下载失败：" + e.getMessage()).getMessage());

                            index[0]++;

                            //是否可以发送了
                            if (index[0] == lolicon.getData().size()){
                                callBack.callback(messages.toArray(new Message[]{}));
                            }
                        }

                        @Override
                        public void progress(long pro, long len, long speed) {

                        }
                    }));
                }
            }else {
                Message message = MyYuQ.getMif().text("发生了错误："
                        + lolicon.getError()).plus(Message.Companion.toMessageByRainCode("<Rain:Image:2B15CC31839368DAA35C8F314661FF13.jpg>")).toMessage();
                callBack.callback(new Message[]{message});
            }
        } catch (IOException e) {
            SfLog.getInstance().e(LoliconManager.class,e);
            callBack.callback(new Message[]{MyYuQ.getMif().text("错误:" + e.getMessage()).toMessage()});
        }
    }
}
