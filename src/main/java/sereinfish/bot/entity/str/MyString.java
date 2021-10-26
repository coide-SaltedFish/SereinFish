package sereinfish.bot.entity.str;

import java.util.LinkedHashMap;
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

        while (matcher.find()){
            String gr = matcher.group();
            //去除头尾
            String c = gr.substring(1, gr.length() - 1);

            String[] p = c.split(":", 3);
            if (p.length >= 2) {
                String type = p[1].toLowerCase();
                String valType = "";
                String valName = "";
                if (p.length > 2) {
                    String[] paras = p[2].split(",", 2);
                    if (paras.length > 1){
                        valType = paras[0];
                        valName = paras[1];
                    }
                }
                if (!valType.equals("") && !valName.equals("")){
                    //获取值
                    String str1 = model.substring(startStr, matcher.start());
                    String str2 = "";

                    if (matcher.end() + 1 < model.length()){
                        str2 = model.substring(matcher.end(), matcher.end() + 1);
                    }else {
                        str2 = model.substring(matcher.end());
                    }
                    String valStr = intermediateValueAcquisition(str, str1, str2);

                    startStr = matcher.end();

                    if (valType.toLowerCase().equals("string")){
                        reMap.put(valName, valStr);
                    }

                    if (valType.toLowerCase().equals("int")){
                        reMap.put(valName, Integer.decode(valStr));
                    }
                }

            }
        }

        return reMap;
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
