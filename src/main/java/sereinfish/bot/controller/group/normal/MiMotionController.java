package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import sereinfish.bot.database.table.Account;
import sereinfish.bot.entity.motion.xiaomi.XiaoMiMotion;
import sereinfish.bot.file.account.AccountManager;
import sereinfish.bot.utils.Result;

import java.io.IOException;
import java.sql.SQLException;

@GroupController
public class MiMotionController {

    @Before
    public Account before(long qq) throws SQLException, IllegalAccessException {
        Account account = AccountManager.getInstance().query(Account.Type.XIAOMI_MOTION, qq);
        if (account == null)
            throw FunKt.getMif().at(qq).plus("您还没有绑定账号，无法操作步数！！").toThrowable();
        else return account;
    }

    @Action("小米运动步数 {step}")
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
            AccountManager.getInstance().changerTaken(loginToken, account.getQq());
            result = XiaoMiMotion.changeStep(loginToken, step);
        }
        return result;
    }
}
