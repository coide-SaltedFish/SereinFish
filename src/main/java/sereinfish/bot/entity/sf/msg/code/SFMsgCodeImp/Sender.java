package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;

@SFMsgCodeInfo("sender")
public class Sender implements SFMsgCode {
    @Override
    public String error(Exception e) {
        e.printStackTrace();
        throw new DoNone();
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        String type = parameter.getString(0).toLowerCase();

        if (type.equals("name")){
            return codeContact.getSender().getName();
        }

        if (type.equals("id")){
            return codeContact.getSender().getId() + "";
        }

        if (type.equals("namecard")){
            if (codeContact.getSource() instanceof Group){
                Group group = (Group) codeContact.getSource();
                return group.get(codeContact.getSender().getId()).nameCardOrName();
            }
            return codeContact.getSender().getName();
        }
        if (type.equals("headimage")){
            Image image = codeContact.getSource().uploadImage(CacheManager.getMemberHeadImageFile(codeContact.getSender().getId()));
            return "<Rain:Image:" + image.getId() + ">";
        }

        if (type.equals("at")){
            return "<Rain:At:" + codeContact.getSender().getId() + ">";
        }

        if (type.equals("message")){
            return codeContact.getBotActionContext().getMessage().getCodeStr();
        }

        return null;
    }
}
