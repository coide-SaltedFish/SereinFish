package sereinfish.bot.net;

import com.IceCreamQAQ.Yu.util.IO;
import sereinfish.bot.utils.BotUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MyNet {

    public static String ping(String domain) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String os = System.getProperty("os.name");
        String pingStr;
        if (os.contains("Windows")) pingStr = "ping " + domain + " -n 1";
        else pingStr = "ping " + domain + " -c 1";
        Process process = runtime.exec(pingStr);
        if (process != null){
            byte[] bytes = IO.read(process.getInputStream(), true);
            String result;
            if (os.contains("Windows")) result = new String(bytes, Charset.forName("gbk"));
            else result = new String(bytes, StandardCharsets.UTF_8);
            if (result.contains("找不到主机") || result.contains("Name or service not known")) return "域名解析失败！！";
            String ip;
            ip = BotUtils.regex("\\[", "\\]", result);
            if (ip == null) ip = BotUtils.regex("\\(", "\\)", result);
            if (ip == null) return "域名解析失败！！！";
            else ip = ip.trim();
            String time;
            time = BotUtils.regex("时间=", "ms", result);
            if (time == null) time = BotUtils.regex("time=", "ms", result);
            if (time == null) return "请求超时！！"; else time = time.trim();
            return "====查询结果====\n" + "域名/IP：" + domain + "\n" +
                    "IP：" + ip + "\n" +
                    "延迟：" + time + "ms";
        }else return "ping失败，请稍后再试！！";
    }
}
