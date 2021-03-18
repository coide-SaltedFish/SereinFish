package sereinfish.bot.entity.xmlEx;

import com.icecreamqaq.yuq.message.Message;

public class XmlMsg {

    public static Message getBigImageMsg(String md5){
        return Message.Companion.toMessageByRainCode("<Rain:Xml:0,&&&lt&&&?xml version='1.0' encoding='UTF-8' standalone='yes' ?" +
                "&&&gt&&&&&&lt&&&msg serviceID=\"5\" templateID=\"12345\" action=\"\" brief=\"[图片]\" sourceMsgId=\"0\" url=\"\" flag=\"0\" " +
                "adverSign=\"0\" multiMsgFlag=\"0\"&&&gt&&&&&&lt&&&item layout=\"0\" advertiser_id=\"0\" aid=\"0\"&&&gt&&&&&&lt&&&image " +
                "uuid=\"80de7427e03934b7975f3274549506ef\" " +
                "md5=\"" + md5 + "\" " +
                "GroupFiledid=\"0\" filesize=\"13787\" local_path=\"\" minWidth=\"300\" minHeight=\"300\" maxWidth=\"550\" maxHeight=\"999\" " +
                "/&&&gt&&&&&&lt&&&/item&&&gt&&&&&&lt&&&source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /&&&gt&&&&&&lt&&&/msg&&&gt&&&>");
    }
}
