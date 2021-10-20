package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;

@SFMsgCodeInfo("sender")
public class Sender implements SFMsgCode {
    @Override
    public String error(Exception e) {
        e.printStackTrace();
        throw new DoNone();
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String para = codeContact.getParameter();

        if (para.toLowerCase().equals("name")){
            return codeContact.getSender().getName();
        }

        if (para.toLowerCase().equals("id")){
            return codeContact.getSender().getId() + "";
        }

        if (para.toLowerCase().equals("namecard")){
            if (codeContact.getSource() instanceof Group){
                Group group = (Group) codeContact.getSource();
                return group.get(codeContact.getSender().getId()).nameCardOrName();
            }
            return codeContact.getSender().getName();
        }
        if (para.toLowerCase().equals("headimage")){
            Image image = codeContact.getSource().uploadImage(CacheManager.getMemberHeadImageFile(codeContact.getSender().getId()));
            return "<Rain:Image:" + image.getId() + ">";
        }

        if (para.toLowerCase().equals("at")){
            return "<Rain:At:" + codeContact.getSender().getId() + ">";
        }

        return "<参数错误>";
    }
}
