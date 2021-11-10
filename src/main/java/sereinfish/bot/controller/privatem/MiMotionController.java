package sereinfish.bot.controller.privatem;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Member;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.service.AccountService;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.motion.xiaomi.XiaoMiMotion;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.utils.Result;

import javax.inject.Inject;
import java.io.IOException;

@PrivateController
//@Menu(name = "小米运动绑定", type = Menu.Type.PRIVATE)
public class MiMotionController {
    @Inject
    private AccountService accountService;

    @Action("小米运动登录 {phone} {password}")
    @MenuItem(name = "绑定小米运动", usage = "小米运动登录 {phone} {password}", description = "将小米运动账号与bot绑定")
    public String login(String phone, String password, Contact qq) throws IOException {
        SfLog.getInstance().d(this.getClass(),"小米运动账号绑定开始");
        Result<String> loginResult = XiaoMiMotion.login(phone, password);
        if (loginResult.getCode() == 200){
            Long group = null;
            if (qq instanceof Member){
                group = ((Member) qq).getGroup().getId();
            }
            String loginToken = loginResult.getData();

            accountService.save(new Account(Account.Type.XIAOMI_MOTION, qq.getId(), phone, password, loginToken));
            return "绑定或者更新小米运动信息成功！！";
        }else return "账号或密码错误，请重新绑定！！";
    }
}
