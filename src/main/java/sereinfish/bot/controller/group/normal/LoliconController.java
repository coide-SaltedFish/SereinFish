package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.lolicon.Lolicon;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.entity.lolicon.sf.Request;
import sereinfish.bot.entity.lolicon.sf.Response;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@GroupController
@PrivateController
@Menu(type = Menu.Type.ALL, name = "Lolicon")
public class LoliconController {
    private static boolean isSending = false;//是否正在发送中

    @Before
    public MySender before(Contact group, Contact qq, Member sender, Message message){
        String apiKey = null;
        if (group != null){
            Group g = (Group) group;

            GroupConf conf = GroupConfManager.getInstance().get(g.getId());
            //权限检查
            int authority = (int) (double) conf.getControl(GroupControlId.AuthorityComboBox_Setu).getValue();
            if (!AuthorityManagement.getInstance().authorityCheck(sender, authority)){
                SfLog.getInstance().w(this.getClass(), "权限不足,所需权限（" + AuthorityManagement.getInstance().getAuthorityName(authority) + "）");
                throw new DoNone();
            }

            apiKey = (String) conf.getControl(GroupControlId.Edit_SetuKey).getValue();
            //检查群功能是否启用
            if (!(Boolean) conf.getControl(GroupControlId.CheckBox_SetuEnable).getValue()){
                throw new DoNone();
            }
            if (apiKey.trim().equals("")){
                apiKey = null;
            }
            int reCallTime = (int) (double) conf.getControl(GroupControlId.Edit_IntNum_SetuReCallTime).getValue();
            if (reCallTime < 0){
                reCallTime = 0;
            }
            if (reCallTime > 110){
                reCallTime = 110;
            }
            reCallTime *= 1000;
            boolean isMustReCall = (Boolean) conf.getControl(GroupControlId.CheckBox_SetuReCall).getValue();
            return new MySender(group, sender, message, apiKey, reCallTime, isMustReCall);
        }else {
            return new MySender(qq, sender, message, apiKey, 25 * 1000, true);
        }
    }

    @Action("\\img_B407F708A2C6A506342098DF7CAC4A57\\")
    @Synonym({"\\[!！.]setu$\\", "\\[!！.]色图$\\","\\img_049231702ACB5E94ECCD953F46E6CAB9\\"})
    //@MenuItem(name = "涩图", usage = "[!！.]setu | [!！.]色图", description = "要一张涩图")
    public void getST(MySender mySender){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),null,1);
            }else {
                getLoliconMsg(mySender, true, conf, null, null, 1);
            }
        }
    }

    @Action("\\img_7CF98559280FD216C5C48AA3D22A8815\\")
    @QMsg(mastAtBot = true)
    public void getST_2(MySender mySender){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf, mySender.getApikey(), null,1);
            }else {
                getLoliconMsg(mySender, true, conf, null, null, 1);
            }
        }
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
    public Message setuAtBotKeyWord(MySender mySender, String strNum, String key){
        int max = 20;

        if (mySender.isGroup()){
            GroupConf groupConf = mySender.getGroupConf();
            if (groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue() instanceof Integer){
                max = (int) groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue();
            }else {
                max = Double.valueOf(groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue().toString()).intValue();
            }
        }
        int num;
        try {
            if (strNum.equals("")){
                strNum = "1";
            }
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:5D6083D0459F5596CB995088E949B71D.jpg>");
            }else {
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:53AF664AB93AAFF6290632025A1B2787.jpg>");
            }
        }

        if (num > max){
            num = max;
            sendMessage(mySender, Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>"),false);
        }

        if (num <= 0){
            return Message.Companion.toMessageByRainCode("<Rain:Image:22C729AA4F85DE574605FA0D19C6A6B7.jpg>");
        }
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),key,num);
            }else {
                getLoliconMsg(mySender, true, conf, null, key.split("#"), num);
            }
        }

        throw new DoNone();
    }

    @Action("来点{key}色图")
    @Synonym({"来点{key}涩图","来张{key}涩图","来张{key}色图","{key}涩图摩多摩多","{key}色图摩多摩多","{key}涩图摩多","{key}色图摩多"})
    @MenuItem(name = "来点涩图且指定tag", usage = "@Bot 来[点张]{key}[涩色]图 | {key}[色涩]图摩多摩多 | {key}色图摩多", description = "要一张涩图且指定tag")
    @QMsg(mastAtBot = true)
    public void setuAtBot(MySender mySender, String key){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),key,1);
            }else {
                getLoliconMsg(mySender, true, conf, null, key.split("#"), 1);
            }
        }
    }

    @Action("涩图摩多")
    @Synonym({"涩图摩多摩多","色图摩多摩多","涩图摩多","色图摩多"})
    @MenuItem(name = "要一张涩图", usage = "@Bot [色涩]图摩多 | [色涩]图摩多摩多", description = "要一张涩图")
    @QMsg(mastAtBot = true)
    public void setuAtBotMore(MySender mySender){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),null,1);
            }else {
                getLoliconMsg(mySender, true, conf, null, null, 1);
            }
        }
    }

    @Action("我要{strNum}张色图")
    @Synonym({"我要{strNum}张涩图","来{strNum}份涩图","来{strNum}份色图"})
    @MenuItem(name = "涩图且指定数量", usage = "@Bot 我要{strNum}张[色涩]图 | 来{strNum}份[涩色]图", description = "要指定数量的涩图")
    @QMsg(mastAtBot = true)
    public Message setuNum(MySender mySender, String strNum){
        int max = 20;

        if (mySender.isGroup()){
            GroupConf groupConf = mySender.getGroupConf();
            if (groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue() instanceof Integer){
                max = (int) groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue();
            }else {
                max = Double.valueOf(groupConf.getControl(GroupControlId.Edit_IntNum_SetuSendMaxNum).getValue().toString()).intValue();
            }
        }

        int num;
        try {
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:5D6083D0459F5596CB995088E949B71D.jpg>");
            }else {
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:53AF664AB93AAFF6290632025A1B2787.jpg>");
            }
        }

        if (num > max){
            num = max;
            sendMessage(mySender, Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>"),false);
        }

        if (num <= 0){
            return Message.Companion.toMessageByRainCode("<Rain:Image:22C729AA4F85DE574605FA0D19C6A6B7.jpg>");
        }
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender, true,conf,mySender.getApikey(),null,num);
            }else {
                getLoliconMsg(mySender, true, conf, null, null, num);
            }
        }
        throw new DoNone();
    }

    @Action("\\img_0E558CBAE41368A42DB812E7C1D9A172\\")
    @QMsg(mastAtBot = true)
    public Message enableR18(MySender mySender, Member sender){
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(true);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                return Message.Companion.toMessageByRainCode("<Rain:Image:241A6BB95CC02A98CCE648BEE17148D0.jpg>");
            }
        }
        throw new DoNone();
    }

    @Action("\\img_982776A6AA3DD49A9DE457F8EABE4EB0\\")
    @Synonym({"\\img_AE8B71A4EB5E5DF105E9EEA61CE6152D\\", "\\img_B112EE7AB6BB3C96A797B0AB5889BFCC\\"})
    @QMsg(mastAtBot = true)
    public Message enableNoR18(MySender mySender, Member sender){
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());
            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(false);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                return Message.Companion.toMessageByRainCode("<Rain:Image:2098B7ECBFDC0C092816EE006E24DB05.jpg>");
            }
        }
        throw new DoNone();
    }

    @Action("\\[lL]olicon混合模式$\\ {state}")
    @MenuItem(name = "Lolicon的混合模式开关", usage = "[lL]olicon混合模式 {state}", description = "开关Lolicon的混合模式")
    @QMsg(mastAtBot = true,reply = true)
    public Message r18Blend(boolean state, MySender mySender, Member sender){
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(state);
                GroupConfManager.getInstance().put(conf);
                return Message.Companion.toMessageByRainCode("Lolicon 混合模式：" + state);
            }
        }
        throw new DoNone();
    }

    /**
     * 得到Lolicon消息
     * @param mySender
     * @param isGroupMsg
     * @param conf
     * @param uids
     * @param tags
     * @param num
     */
    public void getLoliconMsg(MySender mySender, boolean isGroupMsg, GroupConf conf, int[] uids, String[] tags, int num){
        if (isSending){
            Message msg = MyYuQ.getMif().at(mySender.getSender()).plus(" 别急，正在发呢");
            msg.setReply(mySender.getMessage().getSource());
            sendMessage(mySender, msg, false);
        }
        isSending = true;
        getLoliconMsgs(mySender, isGroupMsg, conf, uids, tags, num);
        isSending = false;
    }

    /**
     * 解析Lolicon消息
     * @param isGroupMsg
     * @param conf
     * @param uids
     * @param tags
     * @param num
     * @return
     */
    private void getLoliconMsgs(MySender mySender, boolean isGroupMsg, GroupConf conf, int[] uids, String[] tags, int num){
        //普通Lolicon
        Lolicon.Request request = LoliconManager.getRequest(isGroupMsg,conf,tags,uids, num);
        try {
            SfLog.getInstance().d(LoliconManager.class,"Lolicon 获取中");
            Lolicon lolicon = LoliconManager.getLolicon(request);
            SfLog.getInstance().d(LoliconManager.class,"Lolicon 获取成功");
            if (lolicon.getError().equals("")){
                boolean isR18 = false;
                Message reMsg = new Message();

                if (lolicon.getData().size() == 0){
                    sendMessage(mySender, MyYuQ.getMif().text("要求过于奇怪，找不到你想要的图呢").toMessage(),false);
                    isSending = false;
                    return;
                }

                for (Lolicon.Setu setu:lolicon.getData()){
                    if (setu.isR18()){
                        isR18 = true;
                    }
                    File file = CacheManager.getLoliconImage(setu.getPid(),setu);
                    SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
                    //MD5发送方法
                    Message message = MyYuQ.getMif().imageByFile(file).toMessage();
                    if (isGroupMsg && (Boolean) conf.getControl(GroupControlId.CheckBox_LoliconMD5Image).getValue()){
                        try {
                            StringBuilder stringBuilderMd5 = new StringBuilder(DigestUtils.md5Hex(new FileInputStream(file)));
                            stringBuilderMd5.insert(20,"-");
                            stringBuilderMd5.insert(16,"-");
                            stringBuilderMd5.insert(12,"-");
                            stringBuilderMd5.insert(8,"-");
                            sendMessage(mySender, Message.Companion.toMessageByRainCode("<Rain:Image:{" + stringBuilderMd5 + "}.jpg>"),setu.isR18());
                        } catch (IOException e) {
                            SfLog.getInstance().e(LoliconManager.class,e);
                        }
                    }else {
                        sendMessage(mySender, message,setu.isR18());
                    }
                }
            }else {
                sendMessage(mySender, MyYuQ.getMif().text("发生了错误：" + lolicon.getError()).plus(Message.Companion.toMessageByRainCode("<Rain:Image:2B15CC31839368DAA35C8F314661FF13.jpg>")),false);
            }
        } catch (IOException e) {
            SfLog.getInstance().e(LoliconManager.class,e);
            sendMessage(mySender, MyYuQ.getMif().text("错误:" + e.getMessage()).toMessage(),false);
        }
    }

    /**
     * 得到SFLolicon消息
     */
    public void getSFLoliconMsg(MySender mySender, boolean isGroupMsg, GroupConf conf, String apiKey, String keyWord, int num){
        if (isSending){
            Message msg = MyYuQ.getMif().at(mySender.getSender()).plus(" 别急，正在发呢");
            msg.setReply(mySender.getMessage().getSource());
            sendMessage(mySender, msg, false);
        }
        isSending = true;
        getSFLoliconMsgs(mySender, isGroupMsg, conf, apiKey, keyWord, num);
        isSending = false;
    }

    /**
     * 解析SFLolicon消息
     * @param isGroupMsg
     * @param conf
     * @param apiKey
     * @param keyWord
     * @param num
     * @return
     */
    private void getSFLoliconMsgs(MySender mySender, boolean isGroupMsg, GroupConf conf, String apiKey, String keyWord, int num){
        //代理SF_Lolicon
        int r18 = Lolicon.NO_R18;
        if (isGroupMsg){
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SetuR18).getValue()){
                r18 = Lolicon.R18;
            }
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_PlainAndR18).getValue()){
                r18 = Lolicon.PLAIN_AND_R18;
            }
        }
        String api = (String) conf.getControl(GroupControlId.Edit_SFLoliconApi).getValue();
        Request request = new Request(api,num,r18,keyWord,null);

        try {
            SfLog.getInstance().d(LoliconManager.class,"SF_Lolicon 获取中");
            Response response = LoliconManager.getSFLolicon(request, true);
            if (response == null){
                sendMessage(mySender, MyYuQ.getMif().text("SF加速线路获取失败, 转到Lolicon线路").toMessage(),false);
                getLoliconMsg(mySender, isGroupMsg, conf, null, keyWord.split("#"), num);
                return;
            }

            if (response.getCode() == 0){
                //消息发送
                for (Response.Setu setu:response.getList()){
                    sendMessage(mySender, Message.Companion.toMessageByRainCode("<Rain:Image:" + setu.getMd5() + ".jpg>"),setu.isR18());
                }
            }else {
                sendMessage(mySender, MyYuQ.getMif().text("错误:" + response.getMsg()).toMessage(),false);
            }
        } catch (IOException e) {
            SfLog.getInstance().e(LoliconManager.class,e);
            sendMessage(mySender, MyYuQ.getMif().text("错误:" + e.getMessage()).toMessage(),false);
        }
    }

    /**
     * 消息发送
     * @param mySender
     */
    private void sendMessage(MySender mySender,Message message, boolean isRecall){
        if (mySender.isGroup()){
            if ((!(Boolean) mySender.getGroupConf().getControl(GroupControlId.CheckBox_PlainAndR18).getValue()
                    && !(Boolean) mySender.getGroupConf().getControl(GroupControlId.CheckBox_SetuR18).getValue()) && isRecall){
                mySender.getContact().sendMessage(Message.Companion.toMessageByRainCode("欸嘿"));
                return;
            }


        }

        try {
            mySender.getContact().sendMessage(message);
        }catch (IllegalStateException e){
            SfLog.getInstance().e(this.getClass(), e);
            mySender.getContact().sendMessage(MyYuQ.getMif().text("消息发送失败:" + e.getMessage()).toMessage());
        }

        if (isRecall || mySender.isMastReCall()){
            ReCallMsgManager.getInstance().add(new ReCallMsg(mySender, new Date().getTime(), message, mySender.getReCallTime()));
        }
    }

    @AllArgsConstructor
    @Getter
    static class MySender{
        private Contact contact;
        private Member sender;
        private Message message;
        private String apikey;
        private int reCallTime = 0;
        private boolean isMastReCall = false;

        public boolean isGroup(){
            return contact instanceof Group;
        }

        public GroupConf getGroupConf(){
            if (isGroup()){
                Group group = (Group) contact;
                return GroupConfManager.getInstance().get(group.getId());
            }
            return null;
        }
    }

    @AllArgsConstructor
    @Getter
    static class ReCallMsg{
        private MySender mySender;
        private long time;
        private final Message message;
        private long reCallTime;
    }

    /**
     * 撤回
     */
    static class ReCallMsgManager{
        private static Thread reCallThread;//撤回线程
        private static ArrayList<ReCallMsg> reCallMsgList = new ArrayList<>();//需要撤回的消息列表
        private static ReCallMsgManager msgManager;

        private ReCallMsgManager(){
            if (reCallThread == null || !reCallThread.isAlive()){
                reCallThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            if (reCallMsgList.size() != 0){
                                for (int i = 0; i < reCallMsgList.size(); i++){
                                    ReCallMsg reCallMsg = reCallMsgList.get(i);
                                    long timeDifference = new Date().getTime() - reCallMsg.getTime();
                                    if (timeDifference > reCallMsg.getReCallTime()){
                                        try {
                                            reCallMsg.getMessage().recall();
                                        }catch (Exception e){
                                            SfLog.getInstance().e(ReCallMsgManager.class, e);
                                            reCallMsg.getMySender().contact.sendMessage(MyYuQ.getMif().text("由于一些不可预知的错误导致了原本应该撤回的消息并未撤回，请求管理员进行撤回").toMessage());
                                        }
                                        reCallMsgList.remove(reCallMsg);
                                    }
                                }
                            }
                            try {
                                Thread.sleep(1000);//2s检测一次
                            } catch (InterruptedException e) {
                                SfLog.getInstance().e(this.getClass(), e);
                            }
                        }
                    }
                });
                reCallThread.start();
                SfLog.getInstance().w(LoliconController.class, "撤回线程初始化");
            }
        }

        public static ReCallMsgManager getInstance(){
            if (msgManager == null){
                //
                SfLog.getInstance().d(LoliconController.class, "撤回控制器初始化");
                msgManager = new ReCallMsgManager();
            }
            return msgManager;
        }

        public void add(ReCallMsg msg){
            reCallMsgList.add(msg);
        }
    }
}
