package sereinfish.bot.utils;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Path;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.util.OkHttpWebImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.message.*;
import com.icecreamqaq.yuq.mirai.MiraiBot;
import com.icecreamqaq.yuq.mirai.message.ImageReceive;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedReturnValue")
public class BotUtils {

    private static final Map<String, Pattern> patternMap = new HashMap<>();

    public static String shortUrl(String url){
        // http://www.uc4.cn/
        try {
            if (!url.startsWith("http") && !url.startsWith("https")) url = "http://" + url;
            JSONObject jsonObject = OkHttpUtils.getJson("http://uc4.cn/ajax.php?act=creat1&url=" +
                    URLEncoder.encode(url, "utf-8") + "&pattern=1&type=a6bcn&id=", OkHttpUtils.addUA(UA.PC));
            if (jsonObject.getInteger("code") == 0){
                return jsonObject.getString("dwz");
            }else return url;
        } catch (IOException e) {
            return url;
        }
    }

    public static String regex(String regex, String text){
        Pattern pattern;
        if (patternMap.containsKey(regex))
            pattern = patternMap.get(regex);
        else {
            pattern = Pattern.compile(regex);
            patternMap.put(regex, pattern);
        }
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()){
            return matcher.group();
        }
        return null;
    }

    public static String regex(String first, String last, String text){
        String regex = String.format("(?<=%s).*?(?=%s)", first, last);
        return regex(regex, text);
    }

    private static String random(String str, int length){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++){
            int at = (int) (Math.random() * str.length());
            result.append(str.charAt(at));
        }
        return result.toString();
    }

    public static String randomStr(int length){
        return random("1234567890abcdefghijklmnopqrstuvwxyz", length);
    }

    public static String randomStrLetter(int length){
        return random("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", length);
    }

    public static String randomNum(int length){
        return random("1234567890", length);
    }

    public static Long randomLong(long min, long max){
        return ((long) (Math.random() * max)) % (max - min + 1) + min;
    }

    public static int randomInt(int min, int max){
        return ((int) (Math.random() * max)) % (max - min + 1) + min;
    }

    public static Message jsonArrayToMessage(JSONArray jsonArray){
        Message msg = Message.Companion.toMessage("");
        MessageItemFactory mif = FunKt.getMif();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject aJsonObject = jsonArray.getJSONObject(i);
            switch (aJsonObject.getString("type")){
                case "text":
                    msg.plus(aJsonObject.getString("content"));
                    break;
                case "image":
                    msg.plus(mif.imageByUrl(aJsonObject.getString("content")));
                    break;
                case "face":
                    msg.plus(mif.face(aJsonObject.getInteger("content")));
                    break;
                case "at":
                    msg.plus(mif.at(aJsonObject.getLong("content")));
                    break;
                case "xml":
                    msg.plus(mif.xmlEx(aJsonObject.getInteger("serviceId"), aJsonObject.getString("content")));
                    break;
                case "json":
                    msg.plus(mif.jsonEx(aJsonObject.getString("content")));
                    break;
                case "voice":
                    InputStream is = null;
                    try {
                        is = OkHttpUtils.getByteStream(aJsonObject.getString("content"));
                        msg.plus(mif.voiceByInputStream(is));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.close(is);
                    }
                    break;
            }
        }
        return msg;
    }

    public static boolean equalsMessageJsonArray(JSONArray jsonArray1, JSONArray jsonArray2){
        if (jsonArray1 == null || jsonArray2 == null) return false;
        if (jsonArray1.size() != jsonArray2.size()) return false;
        for (int i = 0; i < jsonArray1.size(); i++){
            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
            String type1 = jsonObject1.getString("type");
            String type2 = jsonObject2.getString("type");
            if (type1 == null || type2 == null) return false;
            if (!type1.equals(type2)) return false;
            if (type1.equals("image")){
                if (!jsonObject1.getString("id").equals(jsonObject2.getString("id"))) return false;
            }else {
                if (!jsonObject1.getString("content").equals(jsonObject2.getString("content"))) return false;
            }
        }
        return true;
    }

    public static JSONArray delManager(JSONArray jsonArray, String content){
        for (int i = 0; i < jsonArray.size(); i++){
            String str = jsonArray.getString(i);
            if (content.equals(str)){
                jsonArray.remove(str);
                break;
            }
        }
        return jsonArray;
    }

    public static JSONArray delMonitorList(JSONArray jsonArray, String username){
        List<JSONObject> list = new ArrayList<>();
        jsonArray.forEach(obj -> {
            JSONObject jsonObject = (JSONObject) obj;
            if (username.equals(jsonObject.getString("name"))) list.add(jsonObject);
        });
        list.forEach(jsonArray::remove);
        return jsonArray;
    }

    public static List<JSONObject> match(JSONArray jsonArray, String userId){
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (userId.equals(jsonObject.getString("id"))) list.add(jsonObject);
        }
        return list;
    }

    public static List<String> menu(Class<?>...clazzArr){
        List<String> list = new ArrayList<>();
        for (Class<?> clazz : clazzArr) {
            String first = "";
            Path path = clazz.getAnnotation(Path.class);
            if (path != null){
                first = path.value() + " ";
            }
            Method[] methods = clazz.getMethods();
            for (Method method: methods){
                Action action = method.getAnnotation(Action.class);
                if (action != null){
                    list.add(first + action.value());
                }
                Synonym synonym = method.getAnnotation(Synonym.class);
                if (synonym != null){
                    String[] arr = synonym.value();
                    for (String str: arr){
                        list.add(first + str);
                    }
                }
            }
        }
        return list;
    }

    public static String removeLastLine(StringBuilder sb){
        if ("\n".equals(sb.substring(sb.length() - 1, sb.length()))) return sb.deleteCharAt(sb.length() - 1).toString();
        else return sb.toString();
    }

    public static Message toMessage(String str){
        return Message.Companion.toMessage(str);
    }

    public static String firstString(Message message){
        return Message.Companion.firstString(message);
    }

}
