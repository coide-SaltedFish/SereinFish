package sereinfish.bot.entity.random;

import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 抽签管理器
 */
public class DrawManager {
    private static Map<String, Integer> drawsMap = new LinkedHashMap<>();
    private static DrawConf drawConf;

    static {
        //下下签、下签、中签、上签、上上签、无事签
        drawsMap.put("下下签", 3);
        drawsMap.put("上上签", 20);
        drawsMap.put("下签", 20);
        drawsMap.put("中签", 60);
        drawsMap.put("上签", 80);
        drawsMap.put("无事签", 10);

        try {
            drawConf = DrawConf.read();
        } catch (IOException e) {
            SfLog.getInstance().e(DrawManager.class, e);
        }
    }

    /**
     * 为指定id抽签
     * @return
     */
    public static String draw(GroupConf conf, long member){
        //判断是否还能抽
        if (!drawConf.isDoDraw(conf.getGroup(), member, conf.getDrawNum())){
            return "今日签：" + drawConf.getDraw(conf.getGroup(), member) + "\n今日抽签数已达上限，抽太多就不准了哦";
        }

        String draw = "无事签";
        for (Map.Entry<String, Integer> entry:drawsMap.entrySet()){
            if (MyYuQ.getRandom(0, 100) < entry.getValue()){
                draw = entry.getKey();
                break;
            }
        }
        //保存状态
        drawConf.add(conf.getGroup(), member, draw);

        return draw;
    }
}
