package sereinfish.bot.controller.group.admin;

import com.icecreamqaq.yuq.annotation.GroupController;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.permissions.Permissions;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "群管理", permissions = Permissions.GROUP_ADMIN)
public class GroupManagerController {

}
