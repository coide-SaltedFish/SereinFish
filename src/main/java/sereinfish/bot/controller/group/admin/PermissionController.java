package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.permissions.Permissions;

import java.util.Map;

/**
 * 权限命令控制器
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "权限")
public class PermissionController {

    @Before
    public void before(Group group, Member sender) {
//        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.ADMIN)){
//            throw new SkipMe();
//        }
    }

    @Action("权限添加 {permissionName} {qq}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "权限添加", usage = "@Bot 权限添加 {permissionName} {qq}", description = "为指定对象添加指定权限")
    public Message add(Group group, Member sender, String permissionName, Member qq) {
        //查找权限
        try {
            int permission = Permissions.getInstance().getAuthorityValue(permissionName);
            //判断是否可操作
            if (Permissions.getInstance().isOperation(group, sender, permission)) {
                //判断是否已存在
                for (int p : Permissions.getInstance().getMemberPermissions(group, qq)) {
                    if (p == permission) {
                        return new Message().lineQ().text("已存在该权限").getMessage();
                    }
                }
                try{
                    Permissions.getInstance().addPermission(group.getId(), qq.getId(), permission);
                    return new Message().lineQ().text("添加成功").getMessage();
                }catch (IllegalStateException e){
                    return new Message().lineQ().text("错误：" + e.getMessage()).getMessage();
                }
            } else {
                MessageLineQ messageLineQ = new Message().lineQ().text("无法对[" + permissionName + "]权限进行操作，您的权限不够:");
                for (int p : Permissions.getInstance().getMemberPermissions(group, sender)) {
                    messageLineQ.text("[" + Permissions.getInstance().getAuthorityName(p) + "]");
                }
                return messageLineQ.getMessage();
            }

        } catch (IllegalStateException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message().lineQ().text("发生错误了：" + e.getMessage()).getMessage();
        }
    }

    @Action("权限删除 {permissionName} {qq}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "权限删除", usage = "@Bot 权限删除 {permissionName} {qq}", description = "为指定对象删除指定权限")
    public Message delete(Group group, Member sender, String permissionName, Member qq) {
        //查找权限
        try {
            int permission = Permissions.getInstance().getAuthorityValue(permissionName);
            //判断是否可操作
            if (Permissions.getInstance().isOperation(group, sender, permission)) {
                //判断是否已存在
                for (int p : Permissions.getInstance().getMemberPermissions(group, qq)) {
                    if (p == permission) {
                        //删除
                        try {
                            Permissions.getInstance().deletePermission(group.getId(), qq.getId(), Permissions.getInstance().getAuthorityValue(permissionName));
                            return new Message().lineQ().text("已将指定对象移除指定权限组").getMessage();
                        }catch (IllegalStateException e){
                            return new Message().lineQ().text("错误：" + e.getMessage()).getMessage();
                        }
                    }
                }
                return new Message().lineQ().text("目标对象未拥有指定权限").getMessage();
            } else {
                MessageLineQ messageLineQ = new Message().lineQ().text("无法对[" + permissionName + "]权限进行操作，您的权限不够:");
                for (int p : Permissions.getInstance().getMemberPermissions(group, sender)) {
                    messageLineQ.text("[" + Permissions.getInstance().getAuthorityName(p) + "]");
                }
                return messageLineQ.getMessage();
            }

        } catch (IllegalStateException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message().lineQ().text("发生错误了：" + e.getMessage()).getMessage();
        }
    }

    @Action("权限列表")
    @MenuItem(name = "权限列表", usage = "@Bot 权限列表", description = "获取权限命令可用权限列表")
    @QMsg(mastAtBot = true, reply = true)
    public Message permissionList() {
        MessageLineQ messageLineQ = new Message().lineQ().textLine("权限命令可用权限列表如下：");
        for (Map.Entry<String, Integer> entry : Permissions.dynamicPermissionList.entrySet()) {
            messageLineQ.textLine(entry.getKey() + ":" + entry.getValue());
        }
        return messageLineQ.getMessage();
    }

    @Action("权限查询 {user}")
    @Synonym("查询权限 {user}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "查询权限", usage = "@Bot 权限查询 {user}", description = "获取用户的权限")
    public Message queryPermission(Group group, Member user){
        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.textLine("权限为：");
        for (int p : Permissions.getInstance().getMemberPermissions(group, user)) {
            messageLineQ.textLine(Permissions.getInstance().getAuthorityName(p));
        }
        messageLineQ.text("にゃ～");
        return messageLineQ.getMessage();
    }

}
