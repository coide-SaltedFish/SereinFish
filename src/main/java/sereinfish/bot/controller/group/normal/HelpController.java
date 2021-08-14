package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.entity.bot.menu.MenuManager;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "帮助")
public class HelpController {

    @Action("\\[.!！]help\\")
    @MenuItem(name = "帮助命令", usage = "[.!！]help", description = "用来获取帮助菜单")
    public Message getHelpMenu(){

        //先保存为图片
        File file = new File(FileHandle.imageCachePath,"help_temp");
        try {
            ImageIO.write(MenuManager.getMenuImage(), "PNG", file);
            return MyYuQ.getMif().imageByFile(file).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
        }
    }
}
