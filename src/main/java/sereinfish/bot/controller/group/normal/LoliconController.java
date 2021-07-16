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

@GroupController
@PrivateController
public class LoliconController {
    private int reCallTime = 25 * 1000;//撤回延时

    @Before
    public MySender before(Contact group, Contact qq){
        String apiKey = null;
        if (group != null){
            Group g = (Group) group;

            GroupConf conf = GroupConfManager.getInstance().get(g.getId());
            apiKey = (String) conf.getControl(GroupControlId.Edit_SetuKey).getValue();
            //检查群功能是否启用
            if (!(Boolean) conf.getControl(GroupControlId.CheckBox_SetuEnable).getValue()){
                throw new DoNone();
            }
            if (apiKey.trim().equals("")){
                apiKey = null;
            }
            return new MySender(group, apiKey);
        }else {
            return new MySender(qq, apiKey);
        }
    }

    @Action("\\img_B407F708A2C6A506342098DF7CAC4A57\\")
    @Synonym({"\\[!！.]setu$\\", "\\[!！.]色图$\\","\\img_049231702ACB5E94ECCD953F46E6CAB9\\"})
    public void getST(MySender mySender){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),null,1);
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
                getSFLoliconMsg(mySender.getContact(), true,conf, mySender.getApikey(), null,1);
            }
        }
    }

    @Action(".来点{key}色图")
    @Synonym(".来点{key}涩图")
    public void setu(MySender mySender, String key){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),key,1);
            }
        }
    }

    @Action("来{strNum}张{key}色图")
    @Synonym({"来{strNum}张{key}涩图","来{strNum}份{key}涩图"})
    @QMsg(mastAtBot = true)
    public Message setuAtBotKeyWord(MySender mySender, String strNum, String key){
        int max = 20;
        int num;
        try {
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:{5D6083D0-459F-5596-CB99-5088E949B71D}.jpg>");
            }else {
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:{53AF664A-B93A-AFF6-2906-32025A1B2787}.jpg>");
            }
        }

        if (num > max){
            num = max;
            sendMessage(mySender.getContact(), Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:{62E2788A-2579-6250-0ECF-2401DD69A76B}.jpg>"),false);
        }

        if (num <= 0){
            return Message.Companion.toMessageByRainCode("<Rain:Image:{22C729AA-4F85-DE57-4605-FA0D19C6A6B7}.jpg>");
        }
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),key,num);
            }
        }

        throw new DoNone();
    }

    @Action("来点{key}色图")
    @Synonym({"来点{key}涩图","来张{key}涩图","来张{key}色图","{key}涩图摩多摩多","{key}色图摩多摩多","{key}涩图摩多","{key}色图摩多"})
    @QMsg(mastAtBot = true)
    public void setuAtBot(MySender mySender, String key){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),key,1);
            }
        }
    }

    @Action("涩图摩多")
    @Synonym({"涩图摩多摩多","色图摩多摩多","涩图摩多","色图摩多"})
    @QMsg(mastAtBot = true)
    public void setuAtBotMore(MySender mySender){
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),null,1);
            }
        }
    }

    @Action("我要{strNum}张色图")
    @Synonym({"我要{strNum}张涩图","来{strNum}份涩图","来{strNum}份色图"})
    @QMsg(mastAtBot = true)
    public Message setuNum(MySender mySender, String strNum){
        int max = 20;
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
            sendMessage(mySender.getContact(), Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:62E2788A257962500ECF2401DD69A76B.jpg>"),false);
        }

        if (num <= 0){
            return Message.Companion.toMessageByRainCode("<Rain:Image:22C729AA4F85DE574605FA0D19C6A6B7.jpg>");
        }
        //判断是否启用Sf
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SFLoliconEnable).getValue()){
                getSFLoliconMsg(mySender.getContact(), true,conf,mySender.getApikey(),null,num);
            }
        }
        throw new DoNone();
    }

    @Action("\\[!！.]LoliconKey\\ {key}")
    public String apiKey(MySender mySender, String key){
        if (mySender.getContact() instanceof Group){
            Group group = (Group) mySender.getContact();
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            conf.getControl(GroupControlId.Edit_SetuKey).setValue(key);
            if(GroupConfManager.getInstance().put(conf)){
                return "设置成功";
            }else {
                return "设置失败";
            }
        }
        return "失败，未实现功能";
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

    @Action("lolicon混合模式 {state}")
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
     * 解析Lolicon消息
     * @param isGroupMsg
     * @param conf
     * @param apiKey
     * @param keyWord
     * @param num
     * @return
     */
    public void getLoliconMsg(Contact contact, boolean isGroupMsg, GroupConf conf, String apiKey, String keyWord, int num){
        //普通Lolicon
        Lolicon.Request request = LoliconManager.getRequest(isGroupMsg,conf,apiKey,keyWord, num);
        try {
            SfLog.getInstance().d(LoliconManager.class,"Lolicon 获取中");
            Lolicon lolicon = LoliconManager.getLolicon(request);
            if (lolicon.getCode() == Lolicon.SUCCESS){
                if (lolicon.getQuota() == 0){
                    sendMessage(contact, Message.Companion.toMessageByRainCode("<Rain:Image:2B15CC31839368DAA35C8F314661FF13.jpg>"),false);
                }else {
                    boolean isR18 = false;
                    Message reMsg = new Message();
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
                                sendMessage(contact, Message.Companion.toMessageByRainCode("<Rain:Image:" + stringBuilderMd5 + ".jpg>"),setu.isR18());
                            } catch (IOException e) {
                                SfLog.getInstance().e(LoliconManager.class,e);
                            }
                        }else {
                            sendMessage(contact, message,setu.isR18());
                        }
                    }
                }
            }else {
                sendMessage(contact, LoliconManager.loliconErr(isGroupMsg,conf,lolicon,request),false);
            }
        } catch (IOException e) {
            SfLog.getInstance().e(LoliconManager.class,e);
            sendMessage(contact, MyYuQ.getMif().text("错误:" + e.getMessage()).toMessage(),false);
        }
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
    public void getSFLoliconMsg(Contact contact, boolean isGroupMsg, GroupConf conf, String apiKey, String keyWord, int num){
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
                sendMessage(contact, MyYuQ.getMif().text("SF加速线路获取失败, 转到Lolicon线路").toMessage(),false);
                getLoliconMsg(contact, isGroupMsg, conf, apiKey, keyWord, num);
                return;
            }

            if (response.getCode() == 0){
                //消息发送
                for (Response.Setu setu:response.getList()){
                    sendMessage(contact, Message.Companion.toMessageByRainCode("<Rain:Image:" + setu.getMd5() + ".jpg>"),setu.isR18());
                }
            }else {
                sendMessage(contact, MyYuQ.getMif().text("错误:" + response.getMsg()).toMessage(),false);
            }
        } catch (IOException e) {
            SfLog.getInstance().e(LoliconManager.class,e);
            sendMessage(contact, MyYuQ.getMif().text("错误:" + e.getMessage()).toMessage(),false);
        }
    }

    /**
     * 消息发送
     * @param message
     */
    private void sendMessage(Contact contact, Message message, boolean isRecall){
        if (contact instanceof Group){
            Group group = (Group) contact;
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());

            if ((!(Boolean) conf.getControl(GroupControlId.CheckBox_PlainAndR18).getValue()
                    && !(Boolean) conf.getControl(GroupControlId.CheckBox_SetuR18).getValue()) && isRecall){
                contact.sendMessage(Message.Companion.toMessageByRainCode("欸嘿"));
                return;
            }


        }

        contact.sendMessage(message);

        if (isRecall){
            new Thread(new Runnable() {
                final Message fm = message;
                @Override
                public void run() {
                    try {
                        Thread.sleep(reCallTime);
                    } catch (InterruptedException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                    }
                    fm.recall();
                }
            }).start();
        }
    }

    @AllArgsConstructor
    @Getter
    static class MySender{
        private Contact contact;
        private String apikey;
    }
}
