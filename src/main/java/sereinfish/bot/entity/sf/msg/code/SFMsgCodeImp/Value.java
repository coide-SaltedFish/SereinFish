package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.mlog.SfLog;

import java.io.IOException;

@SFMsgCodeInfo("value")
public class Value implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String[] paras = codeContact.getParameter().split(",");
        String name = paras[0];

        if (codeContact.containsKey(name)){
            Object val = codeContact.get(name);

            if (val instanceof String){
                return (String) val;
            }else if(val instanceof Integer){
                return val + "";
            }else if(val instanceof Long){
                return val + "";
            }else if(val instanceof Member){
                String type = "at";
                if (paras.length > 1){
                    type = paras[1];
                }
                return memberHandle(codeContact.getSource(), (Member) val, type);
            }else if(val instanceof Image){
                Image image = (Image) val;
                return "<Rain:Image:" + image.getId() + ">";
            }else {
                return val.toString();
            }
        }
        return null;
    }

    public String memberHandle(Contact contact, Member member, String type) throws IOException {
        switch (type.toLowerCase()){
            case "at":
                return "<Rain:At:" + member.getId() + ">";
            case "name":
                return member.getName();
            case "namecard":
                return member.nameCardOrName();
            case "headimage":
                Image image = contact.uploadImage(CacheManager.getMemberHeadImageFile(member.getId()));
                return "<Rain:Image:" + image.getId() + ">";
            case "id":
                return member.getId() + "";
            case "isadmin":
                return member.isAdmin() + "";
            case "isowner":
                return member.isOwner() + "";
            case "isban":
                return member.isBan() + "";
            case "click":
                SfLog.getInstance().d(this.getClass(), "戳：" + member.getId());
                member.click();
                return "";
            default:
                throw new DoNone();
        }
    }
}
