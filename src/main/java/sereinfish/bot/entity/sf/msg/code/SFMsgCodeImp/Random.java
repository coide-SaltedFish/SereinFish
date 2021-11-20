package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.controller.BotActionContext;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;
import sereinfish.bot.myYuq.MyYuQ;

@SFMsgCodeInfo("random")
public class Random implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        int min = 0;
        int max = 100;

        if (parameter.size() == 1){
            max = parameter.getInt(0);
        }
        if (parameter.size() >= 2){
            min = parameter.getInt(0);
            max = parameter.getInt(1);
        }

        return MyYuQ.getRandom(min, max) + "";
    }
}
