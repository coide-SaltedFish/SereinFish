package sereinfish.bot.myYuq;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.controller.Router;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.IceCreamQAQ.Yu.util.DateUtil;
import com.IceCreamQAQ.Yu.util.Web;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icecreamqaq.yuq.RainBot;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import lombok.Getter;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
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
    public static final int version = 100378;
    public static final String versionName = "v_0.0.49_ART_LAST";

    private static MyYuQ myYuQ = null;//单例模式
    //
    private static YuQ yuQ;
    private static MessageItemFactory mif;
    private static JobManager jobManager;
    private static DateUtil dateUtil;
    private static RainBot rainBot;
    private static OkHttpClient okHttpClient;
    private static Web web;

    //一些正则标签
    public static final String FLAG_IMAGE = "img_[A-Za-z0-9]{8}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{12}\\..{3}";

    @Config("yu.scanPackages")
    Object scanPackages;

    private MyYuQ(YuQ yuQ, MessageItemFactory mif, JobManager jobManager,DateUtil dateUtil,RainBot rainBot, Web web) {
        this.yuQ = yuQ;
        this.mif = mif;
        this.jobManager = jobManager;
        this.dateUtil = dateUtil;
        this.rainBot = rainBot;
        this.web = web;

        okHttpClient = new OkHttpClient();
    }

    public static MyYuQ init(YuQ yuQ, MessageItemFactory mif, JobManager jobManager, DateUtil dateUtil, RainBot rainBot, Web web){
        myYuQ = new MyYuQ(yuQ,mif,jobManager,dateUtil,rainBot, web);

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

    public static JobManager getJobManager() {
        return jobManager;
    }

    public static DateUtil getDateUtil() {
        return dateUtil;
    }

    public static RainBot getRainBot() {
        return rainBot;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static Web getWeb() {
        return web;
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
     * 得到版本
     * @return
     */
    public static String getVersion(){
        return versionName + "-"+ version;
    }

    /**
     * 得到版本信息
     * @return
     */
    public static String getVersionInfo(){
        return appName + "\n" +
                "测试版本：" + versionName + "-"+ version;
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
