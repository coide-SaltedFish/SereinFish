package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;

@SFMsgCodeInfo("member")
public class SFMember implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        Group group = codeContact.getGroup();
        String[] paras = codeContact.getParameter().split(",", 2);
        if (paras.length < 2){
            throw new DoNone();
        }
        String name = paras[0].toLowerCase();
        String val = paras[1];

        if (group == null){
            return null;
        }else {
            long qq = Long.decode(val);
            if (group.getMembers().containsKey(qq)){
                codeContact.save(name, group.get(qq));
            }
        }
        return "";
    }
}
