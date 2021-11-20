package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;
import sereinfish.bot.file.NetHandle;

@SFMsgCodeInfo("Image")
public class SFImage implements SFMsgCode{
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        String type = parameter.getString(0);

        if (parameter.size() == 1){
            if (parameter.getString(0).length() == 32){
                return "<Rain:Image:" + parameter.getString(0) + ">";
            }
        }else {
            if (type.equalsIgnoreCase("url")){
                Image image = codeContact.getSource().uploadImage(NetHandle.imageDownload(parameter.getString(1), "SFCode_" + System.currentTimeMillis()));
                return "<Rain:Image:" + image.getId() + ">";
            }
        }

        return null;
    }
}
