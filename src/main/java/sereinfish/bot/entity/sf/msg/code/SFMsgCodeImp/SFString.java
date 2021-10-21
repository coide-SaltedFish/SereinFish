package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;

@SFMsgCodeInfo("string")
public class SFString implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String[] paras = codeContact.getParameter().split(",", 2);
        if (paras.length < 2){
            throw new DoNone();
        }
        String name = paras[0].toLowerCase();
        String val = paras[1];

        if (!name.equals("") && !val.equals("")){
            codeContact.save(name, val);
        }
        return "";
    }
}
