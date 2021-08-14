package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
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
@Menu(type = Menu.Type.GROUP, name = "搜索快捷入口")
public class WikiController {

    @Before
    public void before(Group group){
        //检查是否启用
        if (!(Boolean) GroupConfManager.getInstance().get(group.getId()).getControl(GroupControlId.CheckBox_wikiEnable).getValue()){
            throw new DoNone();
        }
    }

    @Action("\\[!！.]wiki$\\ {key}")
    @MenuItem(name = "Mc Wiki", usage = "[!！.]wiki {key}", description = "得到Mc Wiki中指向关键词的链接")
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
        //return MyYuQ.getMif().xmlEx(0, XmlMsg.getUrlCard("[Wiki：" + key + "]",url, "https://images.wikia.com/minecraft_zh_gamepedia/images/b/bc/Wiki.png",
        //        "Wiki：" + key,key + " - Minecraft Wiki，最详细的官方我的世界百科")).toMessage();
        return MyYuQ.getMif().text("点击链接查看MC Wiki中有关[" + key + "]的内容:\n" + url).toMessage();
    }

    @Action("\\[!！.]百度$\\ {key}")
    @MenuItem(name = "百度", usage = "[!！.]百度 {key}", description = "得到百度中指向关键词的链接")
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
//        return MyYuQ.getMif().xmlEx(0,XmlMsg.getUrlCard("[百度-" + key + "]",url, "https://www.baidu.com/img/bd_logo1.png",
//                "百度-" + key,key + "_百度一下，你就知道")).toMessage();
        return MyYuQ.getMif().text("点击链接查看百度中有关[" + key + "]的内容:\n" + url).toMessage();
    }

    @Action("\\[!！.][Pp][Rr][Tt][Ss]$\\ {key}")
    @MenuItem(name = "PRTS", usage = "[!！.][Pp][Rr][Tt][Ss] {key}", description = "得到PRTS中指向关键词的链接")
    public Message prts(Group group, String key){
        //检查是否启用
        if (!(Boolean) GroupConfManager.getInstance().get(group.getId()).getControl(GroupControlId.CheckBox_wikiPRTSEnable).getValue()){
            throw new DoNone();
        }

        String url = "http://prts.wiki/index.php?search=";
        try {
            url += URLEncoder.encode(key,"utf-8");
        } catch (UnsupportedEncodingException e) {
            SfLog.getInstance().e(this.getClass(),"[" + key + "] 查询失败",e);
            return MyYuQ.getMif().text("[" + key + "] 查询失败").toMessage();
        }
//        return MyYuQ.getMif().xmlEx(0,XmlMsg.getUrlCard("[百度-" + key + "]",url, "https://www.baidu.com/img/bd_logo1.png",
//                "百度-" + key,key + "_百度一下，你就知道")).toMessage();
        return MyYuQ.getMif().text("点击链接查看PRTS中有关[" + key + "]的搜索内容:\n" + url).toMessage();
    }



}
