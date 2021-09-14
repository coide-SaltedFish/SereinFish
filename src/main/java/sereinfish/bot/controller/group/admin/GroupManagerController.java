package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import net.mamoe.mirai.Bot;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "群管理", permissions = Permissions.GROUP_ADMIN)
public class GroupManagerController {

    @Action("头衔 {sb} {str}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "设置头衔", usage = "头衔 {指定群员} {头衔内容}", description = "为指定对象设置头衔", permission = Permissions.GROUP_ADMIN)
    public String setPrefix(Group group, Member sender, Member sb, String str){
        if (!group.getBot().isOwner()){
            return "无权执行此操作";
        }

        if (sender.isAdmin()){
            try {
                Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).get(sb.getId()).setSpecialTitle(str);
            }catch (Exception e){
                return "发生错误：" + e.getMessage();
            }
            return "完成";
        }else {
            throw new DoNone();
        }
    }
}
