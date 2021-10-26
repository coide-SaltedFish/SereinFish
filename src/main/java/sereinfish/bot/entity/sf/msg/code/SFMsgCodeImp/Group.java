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
        String[] paras = codeContact.getParameter().toLowerCase().split(",", 2);

        String type = paras[0];
        int num = 0;
        if (paras.length > 1){
            num = Integer.decode(paras[1]);
        }


        com.icecreamqaq.yuq.entity.Group group = null;
        if (codeContact.getSource() instanceof com.icecreamqaq.yuq.entity.Group){
            group = (com.icecreamqaq.yuq.entity.Group) codeContact.getSource();
        }else {
            throw new DoNone();
        }

        if (type.equals("name")){
            return group.getName();
        }

        if (type.equals("id")){
            return group.getId() + "";
        }

        if (type.equals("headimage")){
            Image image = codeContact.getSource().uploadImage(CacheManager.getGroupHeadImageFile(codeContact.getSource().getId()));
            return "<Rain:Image:" + image.getId() + ">";
        }

        if (type.equals("owner")){
            return group.getOwner().getId() + "";
        }

        if (type.equals("admin")){
            return group.getAdmins().get(num).getId() + "";
        }

        if (type.equals("member")){
            return group.getMembers().get(num).getId() + "";
        }

        return null;
    }
}
