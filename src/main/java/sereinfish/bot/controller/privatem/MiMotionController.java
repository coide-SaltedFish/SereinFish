package sereinfish.bot.controller.privatem;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Member;
import sereinfish.bot.database.table.Account;
import sereinfish.bot.entity.motion.xiaomi.XiaoMiMotion;
import sereinfish.bot.file.account.AccountManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.utils.Result;

import java.io.IOException;
import java.util.Date;

@PrivateController
public class MiMotionController {

    @Action("小米运动登录 {phone} {password}")
    public String login(String phone, String password, Contact qq) throws IOException {
        SfLog.getInstance().d(this.getClass(),"小米运动账号绑定开始");
        Result<String> loginResult = XiaoMiMotion.login(phone, password);
        if (loginResult.getCode() == 200){
            Long group = null;
            if (qq instanceof Member){
                group = ((Member) qq).getGroup().getId();
            }
            String loginToken = loginResult.getData();
            if(AccountManager.getInstance().add(new Account(Account.Type.XIAOMI_MOTION, new Date().getTime(), qq.getId(), phone, password, loginToken))){
                return "绑定或者更新小米运动信息成功！！";
            }else {
                return "绑定或者更新小米运动信息失败，请联系bot管理员";
            }
        }else return "账号或密码错误，请重新绑定！！";
    }
}
