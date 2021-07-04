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

    public static String getUrlCard(String brief, String url, String cover, String title, String summary){
        String msg = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"33\" templateID=\"123\" action=\"web\" " +
                "brief=\"" + brief + "\" sourceMsgId=\"0\" url=\"" + url + "\" flag=\"8\" adverSign=\"0\" multiMsgFlag=\"0\">" +
                "<item layout=\"2\" advertiser_id=\"0\" aid=\"0\"><picture cover=\"" + cover + "\" w=\"0\" h=\"0\" />" +
                "<title>" + title + "</title><summary>" + summary + "</summary></item><source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /></msg>";
        return msg;
    }
}
