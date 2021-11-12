package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.controller.BotActionContext;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;

import java.util.Date;

@SFMsgCodeInfo("time")
public class Time implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String[] paras = codeContact.getParameter().split(",", 2);
        BotActionContext context = codeContact.getBotActionContext();

        if (paras.length == 1){
            String para = paras[0];
            if (para.equals("")){
                para = sereinfish.bot.myYuq.time.Time.LOG_TIME;
            }

            return sereinfish.bot.myYuq.time.Time.dateToString(new Date(), para);
        }else {
            return sereinfish.bot.myYuq.time.Time.dateToString(new Date(Long.decode(paras[0])), paras[1]);
        }

    }
}
