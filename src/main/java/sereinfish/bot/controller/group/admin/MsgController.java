package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.performance.MyPerformance;

/**
 * 消息相关命令处理
 */
@GroupController
public class MsgController {
    private Group group;
    private Member sender;
    private Message message;

    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        this.group = group;
        this.sender = sender;
        this.message = message;

        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("\\[!！.]版本\\")
    @QMsg(reply = true)
    public Message version(){
        return MyYuQ.getMif().text(MyYuQ.getVersion()).toMessage();
    }

    @Action("运行状态")
    @QMsg(reply = true,mastAtBot = true)
    public void appState(){
        StringBuilder str = new StringBuilder();
        str.append("程序运行时长：" + MyPerformance.getRunTime());
        str.append("\n进程号：" + MyPerformance.getPid());
        str.append("\n处理器核心数：" + MyPerformance.getCoresNum());
        str.append("\n系统CPU使用率：" + String.format("%.2f",MyPerformance.getSystemCpuLoad() * 100) + "%");
        str.append("\n本程序CPU使用率：" + String.format("%.2f",MyPerformance.getProcessCpuLoad() * 100) + "%");
        str.append("\n总内存：" + MyPerformance.getTotalPhysicalMemorySize());
        str.append("\n已使用内存：" + MyPerformance.getUsedPhysicalMemorySize());
        str.append("\n操作系统：" + MyPerformance.getOSName());
        str.append("\nJVM内存总量：" + MyPerformance.getJvmTotalMemory());
        str.append("\nJVM已使用内存：" + MyPerformance.getJvmUsedMemory());
        str.append("\nJAVA版本：" + MyPerformance.getJavaVersion());

        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text(str.toString()).toMessage());
    }

    @Action("\\[!！.]服务器规则\\")
    public Message gameRules(){
        return MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard("Harmoland-服务器规则","桦木原-Harmoland 是一个正版 Minecraft Java Edition 服务器。我们以玩家们和谐友爱为宗旨，以纯净生存为核心玩法。",
                "https://harmo.redlnn.top/logo.png","https://harmo.redlnn.top/%E6%A1%A6%E6%9C%A8%E5%8E%9FHarmoland_%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%A7%84%E5%88%99_20200925.pdf")).toMessage();
    }

    @Action("\\[!！.]官网\\")
    public Message harmoland(){
        return MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard("桦木原","桦木原Harmoland 是一个正版 Minecraft Java Edition 服务器。我们以玩家们和谐友爱为宗旨，以纯净生存为核心玩法。",
                "https://harmo.redlnn.top/logo.png","https://harmo.redlnn.top")).toMessage();
    }

    @Action("\\[!！.]json\\ \"{title}\"\"{desc}\"\"{preview}\"\"{jumpUrl}\"")
    public void jsonTest(String title, String desc, String preview, String jumpUrl){
        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard(title, desc, preview, jumpUrl)).toMessage());
    }
}
