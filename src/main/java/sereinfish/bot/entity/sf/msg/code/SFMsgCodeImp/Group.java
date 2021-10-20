package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;

@SFMsgCodeInfo("group")
public class Group implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String para = codeContact.getParameter();
        com.icecreamqaq.yuq.entity.Group group = null;
        if (codeContact.getSource() instanceof com.icecreamqaq.yuq.entity.Group){
            group = (com.icecreamqaq.yuq.entity.Group) codeContact.getSource();
        }else {
            throw new DoNone();
        }

        if (para.toLowerCase().equals("name")){
            return group.getName();
        }

        if (para.toLowerCase().equals("id")){
            return group.getId() + "";
        }

        if (para.toLowerCase().equals("headimage")){
            Image image = codeContact.getSource().uploadImage(CacheManager.getGroupHeadImageFile(codeContact.getSource().getId()));
            return "<Rain:Image:" + image.getId() + ">";
        }
        return null;
    }
}
