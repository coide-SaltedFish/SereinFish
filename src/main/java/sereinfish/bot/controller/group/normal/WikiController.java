package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.entity.xmlEx.XmlMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Wiki相关指令
 */
@GroupController
public class WikiController {

    @Before
    public void before(Group group){
        //检查是否启用
        if (!(Boolean) GroupConfManager.getInstance().get(group.getId()).getControl(GroupControlId.CheckBox_wikiEnable).getValue()){
            throw new DoNone();
        }
    }

    @Action("\\[!！.]wiki\\ {key}")
    public Message mcWiki(Group group, String key){
        //检查是否启用
        if (!(Boolean) GroupConfManager.getInstance().get(group.getId()).getControl(GroupControlId.CheckBox_wikiMcEnable).getValue()){
            throw new DoNone();
        }

        String url = "https://minecraft-zh.gamepedia.com/index.php?search=";
        try {
            url += URLEncoder.encode(key,"utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),"[" + key + "] 查询失败",e);
            return MyYuQ.getMif().text("[" + key + "] 查询失败").toMessage();
        }
        return MyYuQ.getMif().xmlEx(0, XmlMsg.getUrlCard("[Wiki：" + key + "]",url, "https://images.wikia.com/minecraft_zh_gamepedia/images/b/bc/Wiki.png",
                "Wiki：" + key,key + " - Minecraft Wiki，最详细的官方我的世界百科")).toMessage();
    }

    @Action("\\[!！.]百度\\ {key}")
    public Message baidu(Group group, String key){
        //检查是否启用
        if (!(Boolean) GroupConfManager.getInstance().get(group.getId()).getControl(GroupControlId.CheckBox_wikiBaiduEnable).getValue()){
            throw new DoNone();
        }

        String url = "http://www.baidu.com/s?wd=";
        try {
            url += URLEncoder.encode(key,"utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),"[" + key + "] 查询失败",e);
            return MyYuQ.getMif().text("[" + key + "] 查询失败").toMessage();
        }
        return MyYuQ.getMif().xmlEx(0,XmlMsg.getUrlCard("[百度-" + key + "]",url, "https://www.baidu.com/img/bd_logo1.png",
                "百度-" + key,key + "_百度一下，你就知道")).toMessage();
    }

}
