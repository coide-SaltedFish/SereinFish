package sereinfish.bot;

import com.IceCreamQAQ.Yu.hook.YuHook;
import com.IceCreamQAQ.Yu.loader.AppClassloader;
import com.icecreamqaq.yuq.mirai.YuQMiraiStart;

import java.util.ArrayList;

public class Start {

    /***
     * 请不要往本类里面添加任何项目代码，也不要在本类里面引用任何类，以防止增强失败。
     * 这个问题在之后会解决。
     * @param args 启动参数
     */
    public static void main(String[] args) {
        YuHook.putMatchHookItem("sereinfish.bot.data.conf.entity.GroupConf.set*", "sereinfish.bot.data.conf.entity.ConfHook");
        AppClassloader.registerBackList(new ArrayList<String>() {{add("org.yaml.snakeyaml");}});
        YuQMiraiStart.start(args);
    }

}
