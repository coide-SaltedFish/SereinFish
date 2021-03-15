package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.lolicon.Lolicon;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@GroupController
@PrivateController
public class LoliconController {
    private boolean isGroupMsg = false;//是否是群聊消息
    private Group group;
    private Contact qq;
    private GroupConf conf;
    private Message message;

    private String apiKey;

    @Before
    public void before(Group group, Contact qq, Message message){
        this.group = group;
        this.qq = qq;
        this.message = message;
        if (group == null){
            isGroupMsg = false;
        }else {
            isGroupMsg = true;
        }
        if (qq == null){

        }
        if (isGroupMsg){
            conf = GroupConfManager.getInstance().get(group.getId());
            apiKey = (String) conf.getControl(GroupControlId.Edit_SetuKey).getValue();
            //检查群功能是否启用
            if (!(Boolean) conf.getControl(GroupControlId.CheckBox_SetuEnable).getValue()){
                throw new DoNone();
            }
            if (apiKey.trim().equals("")){
                this.apiKey = null;
            }
        }else {
            apiKey = null;
        }
    }

    @Action("\\img_\\{B407F708-A2C6-A506-3420-98DF7CAC4A57\\}\\")
    @Synonym({"\\[!！.]setu\\", "\\[!！.]色图\\"})
    public void getST(){
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,null,1)){
            if (message != null){
                group.sendMessage(message);
            }
        }
    }

    @Action("\\img_\\{7CF98559-280F-D216-C5C4-8AA3D22A8815\\}\\")
    @QMsg(mastAtBot = true)
    public void getST_2(){
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,null,1)){
            if (message != null){
                group.sendMessage(message);
            }
        }
    }

    @Action(".来点{key}色图")
    @Synonym(".来点{key}涩图")
    public void setu(String key){
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,key,1)){
            if (message != null){
                group.sendMessage(message);
            }
        }
    }

    @Action("来点{key}色图")
    @Synonym({"来点{key}涩图","{key}涩图摩多摩多","{key}色图摩多摩多","{key}涩图摩多","{key}色图摩多"})
    @QMsg(mastAtBot = true)
    public void setuAtBot(String key){
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,key,1)){
            if (message != null){
                group.sendMessage(message);
            }
        }
    }

    @Action("涩图摩多")
    @Synonym({"涩图摩多摩多","色图摩多摩多","涩图摩多","色图摩多"})
    @QMsg(mastAtBot = true)
    public void setuAtBotMore(){
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,null,1)){
            if (message != null){
                group.sendMessage(message);
            }
        }
    }

    @Action("我要{strNum}张色图")
    @Synonym("我要{strNum}张涩图")
    @QMsg(mastAtBot = true)
    public Message setuNum(String strNum){
        int num = 0;
        try {
            num = Integer.valueOf(strNum);
        }catch (Exception e){
            if (MyYuQ.getRandom(0,100) % 2 == 0){
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:{5D6083D0-459F-5596-CB99-5088E949B71D}.jpg>");
            }else {
                return Message.Companion.toMessageByRainCode(strNum + "?不认识\n<Rain:Image:{53AF664A-B93A-AFF6-2906-32025A1B2787}.jpg>");
            }
        }

        if (num > 6){
            num = 6;
            return Message.Companion.toMessageByRainCode("我只有这些了\n<Rain:Image:{62E2788A-2579-6250-0ECF-2401DD69A76B}.jpg>");
        }

        if (num <= 0){
            return Message.Companion.toMessageByRainCode("<Rain:Image:{22C729AA-4F85-DE57-4605-FA0D19C6A6B7}.jpg>");
        }
        for (Message message:LoliconManager.getLoliconMsg(isGroupMsg,conf,apiKey,null,num)){
            if (message != null){
                group.sendMessage(message);
            }
        }
        throw new DoNone();
    }

    @Action("\\[!！.]LoliconKey\\ {key}")
    public String apiKey(String key){
        conf.getControl(GroupControlId.Edit_SetuKey).setValue(key);
        if(GroupConfManager.getInstance().put(conf)){
            return "设置成功";
        }else {
            return "设置成功";
        }
    }

    @Action("\\img_\\{8F2283DA-2199-823A-C507-B452F386D654\\}\\")
    @QMsg(mastAtBot = true)
    public Message enableR18(Member sender){
        if (isGroupMsg){
            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(true);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                return Message.Companion.toMessageByRainCode("<Rain:Image:{241A6BB9-5CC0-2A98-CCE6-48BEE17148D0}.jpg>");
            }
        }
        throw new DoNone();
    }

    @Action("\\img_\\{982776A6-AA3D-D49A-9DE4-57F8EABE4EB0\\}\\")
    @Synonym("\\img_\\{AE8B71A4-EB5E-5DF1-05E9-EEA61CE6152D\\}\\")
    @QMsg(mastAtBot = true)
    public Message enableNoR18(Member sender){
        if (isGroupMsg){
            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(false);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                return Message.Companion.toMessageByRainCode("<Rain:Image:{2098B7EC-BFDC-0C09-2816-EE006E24DB05}.jpg>");
            }
        }
        throw new DoNone();
    }
}
