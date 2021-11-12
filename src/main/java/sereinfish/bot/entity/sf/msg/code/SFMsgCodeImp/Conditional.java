package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.mlog.SfLog;

/**
 * 三目运算实现
 */

@SFMsgCodeInfo({"Conditional", "con"})
public class Conditional implements SFMsgCode {
    @Override
    public String error(Exception e) {
        e.printStackTrace();
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        //<SF:Con:1,>,2,3,4>
        //1>2?3:4
        String[] paras = codeContact.getParameter().split(",", 5);
        int val1 = 0;
        int val2 = 0;
        try {
            val1 = Integer.decode(paras[0]);
        }catch (Exception e){
            if (codeContact.containsKey(paras[0])){
                val1 = (Integer) codeContact.get(paras[0]);
            }else {
                return null;
            }
        }

        String c1 = paras[1];

        try {
            val2 = Integer.decode(paras[2]);
        }catch (Exception e){
            if (codeContact.containsKey(paras[2])){
                val2 = (Integer) codeContact.get(paras[2]);
            }else {
                return null;
            }
        }

        String re1 = paras[3];
        String re2 = paras[4];

        if (c1.equals("==")){
            return val1 == val2 ? re1 : re2;
        }else if (c1.equals("\\>")){
            return val1 > val2 ? re1 : re2;
        }else if (c1.equals("\\<")){
            return val1 < val2 ? re1 : re2;
        }else if (c1.equals("\\>=")){
            return val1 >= val2 ? re1 : re2;
        }else if(c1.equals("\\<=")){
            return val1 <= val2 ? re1 : re2;
        }

        return null;
    }
}
