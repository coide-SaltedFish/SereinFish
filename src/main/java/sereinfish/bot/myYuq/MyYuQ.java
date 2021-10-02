package sereinfish.bot.myYuq;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.IceCreamQAQ.Yu.util.DateUtil;
import com.IceCreamQAQ.Yu.util.Web;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icecreamqaq.yuq.RainBot;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.Text;
import it.sauronsoftware.jave.*;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyYuQ {
    //TODO:钓鱼功能

    public static boolean isEnable = false;//功能启用标志
    public static final String appName = "SereinFish Bot";
    public static final int version = 100544;
    public static final String versionName = "v_0.0.54";

    private static MyYuQ myYuQ = null;//单例模式
    //
    private static YuQ yuQ;
    private static MessageItemFactory mif;
    private static JobManager jobManager;
    private static DateUtil dateUtil;
    private static RainBot rainBot;
    private static OkHttpClient okHttpClient;
    private static Web web;
    private static String botName;

    public static final String BASE_PACK = "sereinfish.bot";//包名

    //一些正则标签
    public static final String FLAG_IMAGE = "img_[A-Za-z0-9]{8}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{12}\\..{3}";

    @Config("yu.scanPackages")
    Object scanPackages;

    private MyYuQ(YuQ yuQ, MessageItemFactory mif, JobManager jobManager,DateUtil dateUtil,RainBot rainBot, Web web, String botName) {
        this.yuQ = yuQ;
        this.mif = mif;
        this.jobManager = jobManager;
        this.dateUtil = dateUtil;
        this.rainBot = rainBot;
        this.web = web;
        this.botName = botName;

        okHttpClient = new OkHttpClient();
    }

    public static MyYuQ init(YuQ yuQ, MessageItemFactory mif, JobManager jobManager, DateUtil dateUtil, RainBot rainBot, Web web, String botName){
        myYuQ = new MyYuQ(yuQ,mif,jobManager,dateUtil,rainBot, web, botName);

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
     * 去除html标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
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
        if (!str.contains(s)){
            return str;
        }
        for (int i = 0; i <= str.length() - s.length(); i++) {
            String s_1 = str.substring(i, i + s.length());
            System.out.println(s_1);
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

    /**
     * 上传图片
     * @param contact
     * @param file
     * @return
     */
    public static String uploadImage(Contact contact, File file) throws IOException {
        contact.uploadImage(file);
        return DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
    }

    /**
     * 得到消息文本部分
     * @param message
     * @return
     */
    public static String getMsgText(Message message){
        String msg = "";
        for (MessageItem item:message.getBody()){
            if (item instanceof Text){
                Text text = (Text) item;
                msg += text.getText();
            }
        }
        return msg;
    }

    /**
     * 消息处理
     * @param str
     * @return
     */
    public static Message[] sfCodeToMessage(BotActionContext actionContext, String str){
        //语音
        Pattern pattern=Pattern.compile("<sf:voice:File:.*>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()){
            String path = matcher.group(0).replace("<sf:voice:File:", "");
            path = path.substring(0, path.length() - 1);
            File file = new File(path);
            if (file.exists() && file.isFile()){
                try {
                    File voiceFile = voiceTo(file, "libamr_wb","amr");
                    return new Message[]{new Message().lineQ().voiceByInputStream(new FileInputStream(voiceFile)).getMessage()};
                } catch (FileNotFoundException e) {
                    SfLog.getInstance().e(MyYuQ.class, e);
                } catch (EncoderException e) {
                    SfLog.getInstance().e(MyYuQ.class, e);
                    try {
                        return new Message[]{new Message().lineQ().voiceByInputStream(new FileInputStream(file)).getMessage()};
                    } catch (FileNotFoundException fileNotFoundException) {
                        SfLog.getInstance().e(MyYuQ.class, e);
                    }
                }
            }
        }
        //消息引用

        double i = 5e10;

        //多条消息
        if (str.split("<sf:split>").length > 1){
            String[] strs = str.split("<sf:split>");
            ArrayList<Message> msgs = new ArrayList<>();
            for (String msgStr:strs){
                for (Message message:sfCodeToMessage(actionContext, msgStr)){
                    msgs.add(message);
                }
            }

            return msgs.toArray(new Message[]{});
        }

        //触发者At
        str = str.replace("<sf:triggerAt>","<Rain:At:" + actionContext.getSender().getId() + ">");
        //触发者qq
        str = str.replace("<sf:triggerQq>", actionContext.getSender().getId() + "");
        //触发者名字
        str = str.replace("<sf:triggerName>",actionContext.getSender().getName());
        //触发者头像
        if (str.contains("<sf:triggerHead>")){
            try {
                String md5 = uploadImage(actionContext.getSource(), NetHandle.imageDownload(actionContext.getSender().getAvatar(), actionContext.getSender().getId() + ""));
                str = str.replace("<sf:triggerHead>", "<Rain:Image:" + md5 + ".jpg>");
            } catch (IOException e) {
                SfLog.getInstance().e(MyYuQ.class, e);
            }
        }

        //回复触发消息
        Message message = Message.Companion.toMessageByRainCode(str);
        if (str.startsWith("<sf:reply>")){
            str = str.replace("<sf:reply>", "");
            message = Message.Companion.toMessageByRainCode(str);
            message.setReply(actionContext.getMessage().getSource());
        }

        return new Message[]{message};
    }

    /**
     * 语音文件转换
     * @return
     */
    public static File voiceTo(File file, String codec,String format) throws EncoderException {
        File target = new File(FileHandle.voiceCachePath, file.getName().substring(0, file.getName().lastIndexOf(".")) + "." + format);
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(codec);//编码器

        audioAttributes.setBitRate(12200);//比特率
        audioAttributes.setChannels(1);//声道；1单声道，2立体声
        audioAttributes.setSamplingRate(8000);//采样率（重要！！！）

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setFormat(format);//格式
        encodingAttributes.setAudioAttributes(audioAttributes);//音频设置

        Encoder encoder = new Encoder();

        encoder.encode(file, target, encodingAttributes);

        return target;
    }

    public static String getBotName() {
        return botName;
    }
}
