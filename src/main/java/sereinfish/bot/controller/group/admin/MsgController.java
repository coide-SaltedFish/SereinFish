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
    public Message appState(){
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

        return MyYuQ.getMif().text(str.toString()).toMessage();
    }

    @Action("\\[!！.]json\\ \"{title}\"\"{desc}\"\"{preview}\"\"{jumpUrl}\"")
    public Message jsonTest(String title, String desc, String preview, String jumpUrl){
        return MyYuQ.getMif().jsonEx(JsonMsg.getUrlCard(title, desc, preview, jumpUrl)).toMessage();
    }
}
