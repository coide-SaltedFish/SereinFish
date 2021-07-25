package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.GroupNotice;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.entity.xmlEx.XmlMsg;
import sereinfish.bot.event.GroupReCallMessageManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.performance.MyPerformance;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderableImageOp;
import java.io.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * 消息相关命令处理
 */
@GroupController
public class MsgController extends QQController {
    int maxTime = 25 * 1000;

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

    @Action("\\[!！.]版本$\\")
    @QMsg(reply = true)
    public Message version(){
        return MyYuQ.getMif().text(MyYuQ.getVersionInfo()).toMessage();
    }

    @Action("运行状态")
    @QMsg(reply = true,mastAtBot = true)
    public Message appState(){
        StringBuilder str = new StringBuilder();
        str.append("程序运行时长：" + MyPerformance.getRunTime());
        str.append("\n进程号：" + MyPerformance.getPid());
        str.append("\n处理器核心数：" + MyPerformance.getCoresNum());
        //str.append("\n系统CPU使用率：" + String.format("%.2f",MyPerformance.getSystemCpuLoad() * 100) + "%");
        str.append("\n本程序CPU使用率：" + String.format("%.2f",MyPerformance.getProcessCpuLoad() * 100) + "%");
        str.append("\n总内存：" + MyPerformance.getTotalPhysicalMemorySize());
        str.append("\n已使用内存：" + MyPerformance.getUsedPhysicalMemorySize());
        str.append("\n操作系统：" + MyPerformance.getOSName());
        str.append("\nJVM内存总量：" + MyPerformance.getJvmTotalMemory());
        str.append("\nJVM已使用内存：" + MyPerformance.getJvmUsedMemory());
        str.append("\nJAVA版本：" + MyPerformance.getJavaVersion());
        str.append("\nbot版本：" + MyYuQ.getVersion());

        return MyYuQ.getMif().text(str.toString()).toMessage();
    }

    @Action("\\[.!！]消息转图片$\\")
    public Message testMsgImage(ContextSession session, Group group){
        try {
            File imageFile = new File(FileHandle.imageCachePath,"msg_temp");//文件缓存路径
            reply("输入要转换的消息");
            Message m1 = session.waitNextMessage(maxTime);
            reply("请稍后");
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());
            ImageIO.write(ImageHandle.messageToImage(m1, conf), "png", imageFile);

            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        }catch (WaitNextMessageTimeoutException e){
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("超时：" + maxTime).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("转换失败:" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[.！!]base64转图片$\\")
    public Message base64ToImage(ContextSession session){
        try {
            File imageFile = new File(FileHandle.imageCachePath,"base64ToImage_temp");//文件缓存路径
            reply("输入要转换的消息");
            Message m1 = session.waitNextMessage();
            reply("请稍后");

            BufferedImage image = ImageHandle.base64ToImage(Message.Companion.firstString(m1));
            ImageIO.write(image, "png", imageFile);

            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        }catch (WaitNextMessageTimeoutException e){
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("超时：" + maxTime).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("转换失败:" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[.！!]玩家头像$\\ {uuid}")
    public Message getMcPlayerHeadImage(String uuid){
        File imageFile = new File(FileHandle.imageCachePath,"mcPlayerHeadImage_temp");//文件缓存路径
        try {
            BufferedImage image = NetHandle.getMcPlayerHeadImage(uuid, 90);
            ImageIO.write(image, "png", imageFile);
            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("获取失败:" + e.getMessage()).toMessage();
        }
    }

    @Action("撤回")
    @Synonym({"reCall", "recall"})
    @QMsg(mastAtBot = true)
    public String recall(Group group){
        GroupReCallMessageManager.MsgInfo msgInfo = GroupReCallMessageManager.getInstance().getRecentMsg(group.getId());
        if (msgInfo != null){
            try {
                SfLog.getInstance().d(this.getClass(), "消息撤回，发送时间：" + Time.dateToString(msgInfo.getTime(), Time.LOG_TIME)  );
                msgInfo.getMessage().recall();
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(), e);
                return "异常：" + e.getMessage();
            }
        }
        throw new DoNone();
    }

    @Action("撤回所有")
    @Synonym({"reAllCall", "reallcall"})
    @QMsg(mastAtBot = true)
    public String reAllCall(Group group){
        try {
            for (GroupReCallMessageManager.MsgInfo msgInfo:GroupReCallMessageManager.getInstance().getAllRecentMsg(group.getId())){
                SfLog.getInstance().d(this.getClass(), "消息撤回，发送时间：" + Time.dateToString(msgInfo.getTime(), Time.LOG_TIME));
                msgInfo.getMessage().recall();
            }
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
            return "异常：" + e.getMessage();
        }
        throw new DoNone();
    }
}
