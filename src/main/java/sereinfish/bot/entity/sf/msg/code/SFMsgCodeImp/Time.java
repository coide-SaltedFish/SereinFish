package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.controller.BotActionContext;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;

import java.util.Date;

@SFMsgCodeInfo("time")
public class Time implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        String defFormat = sereinfish.bot.myYuq.time.Time.LOG_TIME;

        if (parameter.size() == 0){
            return sereinfish.bot.myYuq.time.Time.dateToString(new Date(), defFormat);
        }

        if (parameter.size() == 1){
            return sereinfish.bot.myYuq.time.Time.dateToString(new Date(), parameter.getString(0));
        }else {
            return sereinfish.bot.myYuq.time.Time.dateToString(new Date(parameter.getLong(0)), parameter.getString(1));
        }

    }
}
