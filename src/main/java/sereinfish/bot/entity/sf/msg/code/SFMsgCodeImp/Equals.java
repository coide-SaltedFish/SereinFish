package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;

@SFMsgCodeInfo("equals")
public class Equals implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        if (parameter.getString(0).equals(parameter.getString(1))){
            return parameter.getString(2);
        }
        return parameter.getString(3);
    }
}
