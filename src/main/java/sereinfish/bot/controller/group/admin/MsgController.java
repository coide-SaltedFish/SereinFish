package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.entity.xmlEx.XmlMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.performance.MyPerformance;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * 消息相关命令处理
 */
@GroupController
public class MsgController {
    /**
     * 权限检查
     */
    @Before
    public Group before(Group group, Member sender, Message message){
        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }

        return group;
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

    //消息放大术
//
//    @Action("\\[.!！]大\\")
//    public Message bigImage(Group group, Message message){
//        GroupHistoryMsg groupHistoryMsg = null;
//
//        if(message.getReply() == null){
//            throw new SkipMe();
//        }
//
//        try {
//            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(),message.getReply().getId());
//            if (groupHistoryMsg == null){
//                return MyYuQ.getMif().text("找不到该消息").toMessage();
//            }
//        } catch (SQLException e) {
//            SfLog.getInstance().e(this.getClass(),e);
//            return MyYuQ.getMif().text("操作失败：" + e.getMessage()).toMessage();
//        }
//        Message hMsg = Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg());
//
//        for (MessageItem messageItem : hMsg.getBody()){
//            String msg_str = messageItem.toPath();
//            if (Pattern.matches("img_\\{.*}.jpg",msg_str)){
//                String uuid = msg_str.split("img_\\{|\\}.jpg")[1].replace("-","");
//                return XmlMsg.getBigImageMsg(uuid);
//            }
//        }
//
//        return MyYuQ.getMif().text("未发现图片").toMessage();
//    }
}
