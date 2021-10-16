package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.google.zxing.WriterException;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.event.GroupReCallMessageManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.performance.MyPerformance;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 * 消息相关命令处理
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "Bot相关指令", permissions = Permissions.GROUP_ADMIN)
public class MsgController extends QQController {
    int maxTime = 25 * 1000;

    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("\\^[!！.]版本$\\")
    @QMsg(reply = true)
    @MenuItem(name = "获取Bot版本", usage = "[!！.]版本", description = "返回当前Bot版本", permission = Permissions.GROUP_ADMIN)
    public Message version(){
        return MyYuQ.getMif().text(MyYuQ.getVersionInfo()).toMessage();
    }

    @Action("运行状态")
    @QMsg(reply = true,mastAtBot = true)
    @MenuItem(name = "获取Bot运行状态", usage = "@Bot 运行状态", description = "返回当前Bot运行状态", permission = Permissions.GROUP_ADMIN)
    public Message appState(){

        String str = "程序运行时长：" + MyPerformance.getRunTime() +
                "\n进程号：" + MyPerformance.getPid() +
                "\n处理器核心数：" + MyPerformance.getCoresNum() +
                //str.append("\n系统CPU使用率：" + String.format("%.2f",MyPerformance.getSystemCpuLoad() * 100) + "%");
                "\n本程序CPU使用率：" + String.format("%.2f", MyPerformance.getProcessCpuLoad() * 100) + "%" +
                "\n总内存：" + MyPerformance.getTotalPhysicalMemorySize() +
                "\n已使用内存：" + MyPerformance.getUsedPhysicalMemorySize() +
                "\n操作系统：" + MyPerformance.getOSName() +
                "\nJVM内存总量：" + MyPerformance.getJvmTotalMemory() +
                "\nJVM已使用内存：" + MyPerformance.getJvmUsedMemory() +
                "\nJAVA版本：" + MyPerformance.getJavaVersion() +
                "\nYuQ版本：" + MyPerformance.getYuQVersion() +
                "\nbot版本：" + MyYuQ.getVersion();
        return MyYuQ.getMif().text(str).toMessage();
    }

    @Action("\\^[.!！]消息转图片$\\")
    @MenuItem(name = "消息转图片", usage = "[.!！]消息转图片", description = "输入消息并转为图片", permission = Permissions.GROUP_ADMIN)
    public Message testMsgImage(GroupConf groupConf, ContextSession session){
        try {
            File imageFile = new File(FileHandle.imageCachePath,"msg_temp");//文件缓存路径
            reply("输入要转换的消息");
            Message m1 = session.waitNextMessage(maxTime);
            reply("请稍后");
            ImageIO.write(ImageHandle.messageToImage(m1, groupConf), "png", imageFile);

            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        }catch (WaitNextMessageTimeoutException e){
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("超时：" + maxTime).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("转换失败:" + e.getMessage()).toMessage();
        }
    }

    @Action("\\^[.！!]base64转图片$\\")
    @MenuItem(name = "base64转图片", usage = "[.!！]base64转图片", description = "输入base64并转为图片", permission = Permissions.GROUP_ADMIN)
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

    @Action("\\^[.！!]玩家头像$\\ {uuid}")
    @MenuItem(name = "Mc玩家头像获取", usage = "[.!！]玩家头像", description = "获取对应uuid的Mc玩家头像", permission = Permissions.GROUP_ADMIN)
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

    @Action("戳我")
    @QMsg(mastAtBot = true)
    public void clickMe(Member sender){
        SfLog.getInstance().d(this.getClass(), "戳：" + sender.getName());
        sender.click();
    }

    @Action("撤回")
    @Synonym({"reCall", "recall"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "撤回", usage = "@Bot 撤回", description = "撤回最近的Bot消息", permission = Permissions.GROUP_ADMIN)
    public String recall(Group group){
        try {
            GroupReCallMessageManager.getInstance().reCallRecentMsg(group.getId());
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
            return "异常：" + e.getMessage();
        }
        throw new DoNone();
    }

    @Action("撤回所有")
    @Synonym({"reAllCall", "reallcall"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "撤回所有", usage = "@Bot 撤回所有", description = "撤回最近的所有Bot消息", permission = Permissions.GROUP_ADMIN)
    public String reAllCall(Group group){
        try {
            GroupReCallMessageManager.getInstance().reCallAllRecentMsg(group.getId());
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
            return "异常：" + e.getMessage();
        }
        throw new DoNone();
    }
}
