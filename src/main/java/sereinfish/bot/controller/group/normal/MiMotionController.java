package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.service.AccountService;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.motion.xiaomi.XiaoMiMotion;
import sereinfish.bot.utils.Result;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

@GroupController
//@Menu(type = Menu.Type.GROUP, name = "小米运动")
public class MiMotionController {

    @Inject
    AccountService accountService;

    @Before
    public Account before(long qq) throws SQLException, IllegalAccessException {
        Account account = accountService.findByTypeAndQq(Account.Type.XIAOMI_MOTION, qq);
        if (account == null)
            throw FunKt.getMif().at(qq).plus("您还没有绑定账号，无法操作步数！！").toThrowable();
        else return account;
    }

    @Action("小米运动步数 {step}")
    @MenuItem(name = "小米运动步数", usage = "小米运动步数 {step}", description = "修改小米运动步数为指定值")
    @QMsg(at = true)
    public String xiaomiMotion(Account account, int step) throws IOException, SQLException {
        if (account.getToken() == null)
            return "您还没绑定小米账号，如需绑定请私聊机器人发送<小米运动登录 账号 密码>";
        String loginToken = account.getToken();
        String result = XiaoMiMotion.changeStep(loginToken, step);
        if (result.contains("登录已失效")){
            Result<String> loginResult = XiaoMiMotion.login(account.getAccount(), account.getPassword());
            loginToken = loginResult.getData();
            if (loginToken == null) {
                return loginResult.getMessage();
            }
            account.setToken(loginToken);
            accountService.saveOrUpdate(account);
            result = XiaoMiMotion.changeStep(loginToken, step);
        }
        return result;
    }
}
