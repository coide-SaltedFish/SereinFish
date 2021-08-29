package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.bot.menu.MenuManager;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "帮助")
public class HelpController {

    @Action("\\^[.!！]help$\\")
    @MenuItem(name = "帮助命令", usage = "[.!！]help", description = "用来获取帮助菜单")
    public Message getHelpMenu(Member sender, Group group, Message message){
        Message msg = new Message().lineQ().text("获取中~").getMessage();
        msg.setReply(message.getSource());
        group.sendMessage(msg);

        //先保存为图片
        File file = new File(FileHandle.imageCachePath,"help_temp");
        try {
            ImageIO.write(MenuManager.getMenuImage(group, sender, null), "PNG", file);
            MessageLineQ messageLineQ = new Message().lineQ();
            messageLineQ.at(sender).textLine("");
            messageLineQ.text("您的权限为：");

            for (int p: Permissions.getInstance().getMemberPermissions(group, sender)){
                messageLineQ.text("[" + Permissions.getInstance().getAuthorityName(p) + "]");
            }
            messageLineQ.textLine("");
            messageLineQ.textLine("可执行以下命令:");
            messageLineQ.imageByFile(file);

            return messageLineQ.getMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
        }
    }
}
