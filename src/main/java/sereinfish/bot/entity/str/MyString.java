package sereinfish.bot.entity.str;

import com.kennycason.kumo.Word;
import sereinfish.bot.entity.sf.msg.SFMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyString {

    /**
     * 通过占位符提出对应值
     * @param model
     * @param str
     * @return
     */
    public static Map<String,Object> placeholderExtraction(String model, String str){
        Map<String, Object> reMap = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("<SF:[^<>]+?>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(model);

        int startStr = 0;
        int beforeIndex = 0;

        while (matcher.find()){
            String gr = matcher.group();
            //去除头尾
            String c = gr.substring(1, gr.length() - 1);

            String[] p = c.split(":", 3);
            if (p.length >= 2) {
                String valType = "";
                String valName = "";
                if (p.length > 2) {
                    String[] paras = p[2].split(",", 2);
                    if (paras.length > 1){
                        valType = paras[0].toLowerCase();
                        valName = paras[1];
                    }
                }
                if (!valType.equals("") && !valName.equals("")){
                    //TODO:变量名称检查


                    String valStr = "";

                    if (matcher.end() == model.length()){//如果是结尾

                        valStr = str.substring(startStr + (matcher.start() - beforeIndex));
                        startStr += str.length();
                    }else {//如果是中间
                        //获取输入值的宽度
                        char nextStr = model.charAt(matcher.end());
                        int len = 0;
                        while (str.charAt(startStr + len) != nextStr){
                            if (str.charAt(startStr + len) == ' '
                                    || str.charAt(startStr + len) == '\n'
                                    || str.charAt(startStr + len) == '\t'){
                                break;
                            }
                            len++;
                        }
                        valStr = str.substring(startStr, startStr + len);
                        startStr += len;
                        beforeIndex = matcher.end();
                    }

                    if (valType.equals("string")){
                        reMap.put(valName, keyReplace(valStr));
                    }

                    if (valType.equals("int")){
                        reMap.put(valName, Integer.decode(valStr));
                    }
                }

            }
        }

        return reMap;
    }

    /**
     * 关键字处理
     * @param str
     * @return
     */
    public static String keyReplace(String str){
        str = str.replace("\\<", "$&a&$");
        str = str.replace("\\>", "$&b&$");
        str = str.replace("\\,", "$&c&$");

        str = str.replace("<", "$&a&$");
        str = str.replace(">", "$&b&$");
        str = str.replace(",", "$&c&$");
        return str;
    }


    /**
     *
     * @param str   进行获取的字符串
     * @param str1  中间值前面的字符串
     * @param str2  中间值后面的字符串
     * @return
     */
    public static String intermediateValueAcquisition(String str, String str1, String str2){
        String regex = str1 + ".*" + str2;
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()){
            String gr = matcher.group();
            return gr.substring(str1.length(), gr.length() - str2.length());
        }

        return str;
    }
}
