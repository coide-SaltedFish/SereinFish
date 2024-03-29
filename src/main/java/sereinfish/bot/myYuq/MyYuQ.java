package sereinfish.bot.myYuq;

import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.IceCreamQAQ.Yu.util.DateUtil;
import com.IceCreamQAQ.Yu.util.Web;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.YuQInternalBotImpl;
import com.icecreamqaq.yuq.YuQVersion;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.*;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.database.service.BlackListService;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.database.service.ReplyService;
import sereinfish.bot.database.service.WhiteListService;
import sereinfish.bot.entity.calendar.holiday.HolidayManager;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.mlog.SfLog;

import javax.inject.Inject;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
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
    public static final int version = 100644;
    public static final String versionName = "v_0.0.65";

    private static MyYuQ myYuQ = null;//单例模式
    //
    private static YuQ yuQ;
    private static MessageItemFactory mif;
    private static JobManager jobManager;
    private static DateUtil dateUtil;
    private static YuQInternalBotImpl rainBot;
    private static OkHttpClient okHttpClient;
    private static Web web;
    private static String botName;
    private static YuQVersion rainVersion;

    private static GroupHistoryMsgService groupHistoryMsgService;
    private static ReplyService replyService;
    private static BlackListService blackListService;
    private static WhiteListService whiteListService;

    private static HolidayManager holidayManager;

    public static final String BASE_PACK = "sereinfish.bot";//包名

    //一些正则标签
    public static final String FLAG_IMAGE = "img_[A-Za-z0-9]{8}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{4}[A-Za-z0-9]{12}\\..{3}";

    @Config("yu.scanPackages")
    Object scanPackages;

    private MyYuQ(YuQ yuQ, MessageItemFactory mif, JobManager jobManager,DateUtil dateUtil,YuQInternalBotImpl rainBot, Web web, String botName) {
        this.yuQ = yuQ;
        this.mif = mif;
        this.jobManager = jobManager;
        this.dateUtil = dateUtil;
        this.rainBot = rainBot;
        this.web = web;
        this.botName = botName;

        okHttpClient = new OkHttpClient();
    }

    public static MyYuQ init(YuQ yuQ, MessageItemFactory mif, JobManager jobManager, DateUtil dateUtil, YuQInternalBotImpl rainBot, Web web, String botName){
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

    public static YuQInternalBotImpl getRainBot() {
        return rainBot;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static Web getWeb() {
        return web;
    }

    public static HolidayManager getHolidayManager() {
        return holidayManager;
    }

    public static void setHolidayManager(HolidayManager holidayManager) {
        MyYuQ.holidayManager = holidayManager;
    }

    public static GroupHistoryMsgService getGroupHistoryMsgService() {
        return groupHistoryMsgService;
    }

    public static void setGroupHistoryMsgService(GroupHistoryMsgService groupHistoryMsgService) {
        MyYuQ.groupHistoryMsgService = groupHistoryMsgService;
    }

    public static ReplyService getReplyService() {
        return replyService;
    }

    public static void setReplyService(ReplyService replyService) {
        MyYuQ.replyService = replyService;
    }

    public static BlackListService getBlackListService() {
        return blackListService;
    }

    public static void setBlackListService(BlackListService blackListService) {
        MyYuQ.blackListService = blackListService;
    }

    public static WhiteListService getWhiteListService() {
        return whiteListService;
    }

    public static void setWhiteListService(WhiteListService whiteListService) {
        MyYuQ.whiteListService = whiteListService;
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

    public static YuQVersion getRainVersion() {
        return rainVersion;
    }

    public static void setRainVersion(YuQVersion rainVersion) {
        MyYuQ.rainVersion = rainVersion;
    }

    /**
     * 得到图片链接
     * @param md5
     * @return
     */
    public static URL getImageUrl(String md5) throws MalformedURLException {
        return new URL("http://gchat.qpic.cn/gchatpic_new/0/-0-" + md5 + "/0");
    }

    /**
     * 得到图片链接
     * @param md5
     * @return
     */
    public static String getImageUrlStr(String md5) throws MalformedURLException {
        return "http://gchat.qpic.cn/gchatpic_new/0/-0-" + md5 + "/0";
    }

    /**
     * 得到图片链接
     * @return
     */
    public static String getImageUrlId(String id) throws MalformedURLException {
        return "http://gchat.qpic.cn/gchatpic_new/0/-0-" + id.substring(0, id.indexOf(".")) + "/0";
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
    public static Image uploadImage(Contact contact, File file){
        NetworkLoader.INSTANCE.setWait(true);
        Image image = contact.uploadImage(file);
        NetworkLoader.INSTANCE.setWait(false);

        return image;
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
     * 缩减字符串
     * @param text
     * @param len
     * @return
     */
    public static String textLengthLimit(String text, int len){
        if (len < 1){
            len = 1;
        }
        text = text.replace("\n", " ");
        if (text.length() > len){
            text = text.substring(0, len) + "...";
        }

        return text;
    }

    /**
     * 腾讯图片可用性检测
     * @return
     */
    public static boolean imageEnableTX(String md5) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getImageUrl(md5))
                .get()//默认就是GET请求，可以不写
                .build();
        Response response = okHttpClient.newCall(request).execute();
        response.close();
        int code = response.code();
        return code != 404;
    }

    /**
     * 多消息发送
     * @param contact
     * @param msgList
     */
    public static void sendSFMessage(Contact contact, ArrayList<SFMessage.SFMessageEntity> msgList){
        if (msgList == null){
            return;
        }

        int time = 0;
        for (SFMessage.SFMessageEntity messageEntity:msgList){
            jobManager.registerTimer(new Runnable() {
                @Override
                public void run() {
                    contact.sendMessage(messageEntity.getMessage());
                }
            }, time);
            time += messageEntity.getWaitTime();
        }
    }

    /**
     * yuq图片id转mirai
     * @param id
     * @return
     */
    public static String getMiraiImageId(String id){
        StringBuilder md5 = new StringBuilder(id.substring(0, id.lastIndexOf(".")));

        md5.insert(20,"-");
        md5.insert(16,"-");
        md5.insert(12,"-");
        md5.insert(8,"-");

        id = "{" + md5 + "}" + id.substring(id.indexOf("."));

        return id;
    }

    /**
     * md5转mirai
     * @return
     */
    public static String getMiraiImageId(String md5, String ex){
        StringBuilder stringBuilder = new StringBuilder(md5);

        stringBuilder.insert(20,"-");
        stringBuilder.insert(16,"-");
        stringBuilder.insert(12,"-");
        stringBuilder.insert(8,"-");

        String id = "{" + stringBuilder + "}" + "." + ex;

        return id;
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{}
     * \\需要第一个替换，否则replace方法替换时会有逻辑bug
     */
    public static String makeQueryStringAllRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }

    public static String getBotName() {
        return botName;
    }
}
