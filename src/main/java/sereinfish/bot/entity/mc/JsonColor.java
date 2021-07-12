package sereinfish.bot.entity.mc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JsonColor {
    private static Map<String, String> colorMap = new HashMap<>();//颜色映射表

    /**
     * 初始化颜色映射表
     */
    public static void initColorMap(){
        colorMap.put("black", "#000000");
        colorMap.put("dark_blue", "#0000AA");
        colorMap.put("dark_green", "#00AA00");
        colorMap.put("dark_aqua", "#00AAAA");
        colorMap.put("dark_red", "#AA0000");
        colorMap.put("dark_purple", "#AA00AA");
        colorMap.put("gold", "#FFAA00");
        colorMap.put("gray", "#AAAAAA");
        colorMap.put("dark_gray", "#555555");
        colorMap.put("blue", "#5555FF");
        colorMap.put("green", "#55FF55");
        colorMap.put("aqua","#55FFFF");
        colorMap.put("red","#FF5555");
        colorMap.put("light_purple","#FF55FF");
        colorMap.put("yellow", "#FFFF55");
        colorMap.put("white", "#FFFFFF");
        colorMap.put("reset", "#ffffff");

        colorMap.put("§0", "#000000");
        colorMap.put("§1", "#0000AA");
        colorMap.put("§2", "#00AA00");
        colorMap.put("§3", "#00AAAA");
        colorMap.put("§4", "#AA0000");
        colorMap.put("§5", "#AA00AA");
        colorMap.put("§6", "#FFAA00");
        colorMap.put("§7", "#AAAAAA");
        colorMap.put("§8", "#555555");
        colorMap.put("§9", "#5555FF");
        colorMap.put("§a", "#55FF55");
        colorMap.put("§b","#55FFFF");
        colorMap.put("§c","#FF5555");
        colorMap.put("§d","#FF55FF");
        colorMap.put("§e", "#FFFF55");
        colorMap.put("§f", "#FFFFFF");
        colorMap.put("§g", "#DDD605");
    }

    public static Color getColor(String name){
        //black、dark_blue、dark_green、dark_aqua、dark_red、dark_purple、gold、gray、dark_gray、blue、green、aqua、red、light_purple、yellow、white
        if (colorMap.containsKey(name)){
            return Color.decode(colorMap.get(name));
        }else {
            if (name.startsWith("#")){
                try {
                    return Color.decode(name);
                }catch (Exception e){
                    return Color.WHITE;
                }
            }
        }
        return Color.WHITE;
    }
}
