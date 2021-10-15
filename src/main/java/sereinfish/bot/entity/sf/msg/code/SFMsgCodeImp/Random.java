package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.controller.BotActionContext;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.myYuq.MyYuQ;

@SFMsgCodeInfo("random")
public class Random implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String para = codeContact.getParameter();
        BotActionContext context = codeContact.getBotActionContext();

        if (para.equals("")){
            para = "0,100";
        }
        String paras[] = para.split(",");
        int start = Integer.decode(paras[0]);
        int end = Integer.decode(paras[1]);

        return MyYuQ.getRandom(start, end) + "";
    }
}
