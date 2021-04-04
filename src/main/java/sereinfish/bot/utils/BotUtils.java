package sereinfish.bot.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedReturnValue")
public class BotUtils {
    private static final Map<String, Pattern> patternMap = new HashMap<>();

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
}
