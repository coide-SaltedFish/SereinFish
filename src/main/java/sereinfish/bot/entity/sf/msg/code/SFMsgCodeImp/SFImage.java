package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.file.NetHandle;

@SFMsgCodeInfo("Image")
public class SFImage implements SFMsgCode{
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String para = codeContact.getParameter();
        String[] paras = para.split(",");
        String type = paras[0];

        if (paras.length == 1){
            if (para.length() == 32){
                return "<Rain:Image:" + para + ">";
            }
        }else {
            if (type.equalsIgnoreCase("url")){
                Image image = codeContact.getSource().uploadImage(NetHandle.imageDownload(paras[1], System.currentTimeMillis() + ""));
                return "<Rain:Image:" + image.getId() + ">";
            }
        }

        return null;
    }
}
