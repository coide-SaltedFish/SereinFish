package sereinfish.bot.myYuq;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MyYuQ {
    public static boolean isEnable = false;//功能启用标志
    public static final String appName = "SereinFish Bot";
    public static final int version = 100254;
    public static final String versionName = "v_0.0.25";

    private static MyYuQ myYuQ = null;//单例模式
    //
    private static YuQ yuQ;
    private static MessageItemFactory mif;

    private MyYuQ(YuQ yuQ, MessageItemFactory mif) {
        this.yuQ = yuQ;
        this.mif = mif;
    }

    public static MyYuQ init(YuQ yuQ, MessageItemFactory mif){
        myYuQ = new MyYuQ(yuQ,mif);

        return myYuQ;
    }

    public static MyYuQ getInstance(){
        if (myYuQ == null){
            throw new NullPointerException("MyYuQ尚未初始化");
        }
        return myYuQ;
    }

    public static YuQ getYuQ() {
        return yuQ;
    }

    public static MessageItemFactory getMif() {
        return mif;
    }

    /**
     * 对象转json文本
     * @param o
     * @param type
     * @return
     */
    public static String toJson(Object o, Type type){
        return new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create().toJson(o,type);
    }

    /**
     * 文本转json对象
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T toClass(String json,Type type){
        if (json == null){
            return null;
        }
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        jsonReader.setLenient(true);
        return new GsonBuilder().setPrettyPrinting().create().fromJson(jsonReader,type);
    }

    /**
     * 得到一个可控范围随机数
     * @param start
     * @param end
     * @return
     */
    public static int getRandom(int start,int end) {
        Random random = new Random();
        return random.nextInt(end - start + 1) + start;
    }

    /**
     * 计算MD5码
     * @param plainText
     * @return
     */
    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 处理消息变量
     * qq:被操作qq
     * fromQQ：操作者qq
     * group：来自的群
     * name：被操作qq名称
     * fromName：操作qq名称
     * @return
     */
    public static String messageVariable(String msg, Member sender, Member operator, Group group){
        //进行变量替换
        if (sender != null){
            msg = replace(msg,"<qq>",sender.getId()+"");
            msg = replace(msg,"<name>",sender.getName());
        }
        //
        if (operator != null){
            msg = replace(msg,"<operatorQQ>",operator.getId()+"");
            msg = replace(msg,"<operatorName>",operator.getName());
        }
        //group
        if (group != null){
            msg = replace(msg,"<groupID>",group.getId()+"");
            msg = replace(msg,"<groupName>",group.getName());
            msg = replace(msg,"<group>",group.toString());
        }
        return msg;
    }

    /**
     * 发送群消息
     * @param group
     * @param message
     */
    public static boolean sendGroupMessage(Group group, Message message){
        if(group.sendMessage(message).getId() < 0){
            group.sendMessage(myYuQ.mif.text("消息发送失败，转图片发送中，请稍候").toMessage());
            //TODO:转图片发送
        }
        return true;
    }

    /**
     * 发送群消息
     * @param group
     * @param message
     */
    public static boolean sendGroupMessage(Group group, String message){
        if(group.sendMessage(mif.text(message).toMessage()).getId() < 0){
            group.sendMessage(myYuQ.mif.text("消息发送失败，转图片发送中，请稍候").toMessage());
            //TODO:转图片发送
        }
        return true;
    }

    /**
     * 发送消息
     * @param contact
     * @param message
     */
    public static boolean sendMessage(Contact contact, Message message){
        if(contact.sendMessage(message).getId() < 0){
            contact.sendMessage(myYuQ.mif.text("消息发送失败，转图片发送中，请稍候").toMessage());
            //TODO:转图片发送
        }
        return true;
    }

    /**
     * 发送消息
     */
    public static boolean sendMessage(Member sender, Message message){
        if(sender.sendMessage(message).getId() < 0){
            sender.sendMessage(myYuQ.mif.text("消息发送失败，转图片发送中，请稍候").toMessage());
            //TODO:转图片发送
        }
        return true;
    }

    /**
     * 字符串替换
     * @param str 进行替换的文本
     * @param s 要替换的字符串
     * @param n 要替换成的字符串
     * @return
     */
    public static String replace(String str,String s,String n) {
        for (int i = 0; i <= str.length() - s.length(); i++) {
            String s_1 = str.substring(i, i + s.length());
            if (s_1.equals(s)) {
                str = str.substring(0, i) + n + str.substring(i + s.length());
            }
        }
        return str;
    }

    /**
     * 得到版本信息
     * @return
     */
    public static String getVersion(){
        return appName + "\n" +
                "测试版本：" + versionName + "\n" +
                "V:" + version;
    }

    /**
     * 得到群组列表
     * @return
     */
    public static ArrayList<Group> getGroups(){
        ArrayList<Group> groups = new ArrayList<>();
        for (Map.Entry<Long,Group> entry:yuQ.getGroups().entrySet()){
            groups.add(entry.getValue());
        }
        return groups;
    }


}
