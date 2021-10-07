package org.sereinfish.bot;

import com.IceCreamQAQ.Yu.hook.HookItem;
import com.IceCreamQAQ.Yu.hook.YuHook;
import com.IceCreamQAQ.Yu.loader.AppClassloader;
import com.icecreamqaq.yuq.mirai.YuQMiraiStart;

import java.util.ArrayList;

public class Start {
    public static void main(String[] args){
        YuHook.put(new HookItem("org.hibernate.Version", "initVersion", "com.icecreamqaq.yudb.HibernateVersionHook"));

        AppClassloader.registerBackList(new ArrayList<String>() {{add("jdk.");}});
        YuQMiraiStart.start(args);
    }
}