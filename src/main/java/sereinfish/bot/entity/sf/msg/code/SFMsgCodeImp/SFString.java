package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;

@SFMsgCodeInfo("string")
public class SFString implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        if (parameter.size() < 2){
            throw new DoNone();
        }
        String name = parameter.getString(0).toLowerCase();
        String val = parameter.getString(1);

        if (!name.equals("") && !val.equals("")){
            codeContact.save(name, val);
        }
        return "";
    }
}
