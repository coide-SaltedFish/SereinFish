package sereinfish.bot.entity.xmlEx;

import com.icecreamqaq.yuq.message.Message;

public class XmlMsg {

    public static Message getBigImageMsg(String md5){
        return Message.Companion.toMessageByRainCode("<Rain:Xml:5,&&&lt&&&?xml version='1.0' encoding='UTF-8' standalone='yes' ?&&&gt&&&&&&lt&&&msg serviceID=\"5\" templateID=\"12345\" a" +
                "ction=\"\" brief=\"[图片]\" sourceMsgId=\"0\" url=\"\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\"&&&gt&&&&&&lt&&&item " +
                "layout=\"0\"&&&gt&&&&&&lt&&&image " +
                "uuid=\"" + md5 + ".png\" " +
                "md5=\"" + md5 + "\" " +
                "GroupFiledid=\"0\" filesize=\"19305\" local_path=\"" +
                "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg/9c4/Cache_-733027f39b6d79c4\"" +
                " minWidth=\"300\" minHeight=\"300\" maxWidth=\"550\" maxHeight=\"999\" /&&&gt&&&&&&lt&&&/item&&&gt&&&&&&lt&&&source name=\"" +
                "\" icon=\"\" action=\"\" appid=\"-1\" /&&&gt&&&&&&lt&&&/msg&&&gt&&&>");
    }
}
