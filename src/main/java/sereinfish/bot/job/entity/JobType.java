package sereinfish.bot.job.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 任务类型
 */
public class JobType{
    public static final int updateQQHeadImage = 0;//更新qq头像
    public static final int sendMsgJob = 1;//定时消息发送

    public static Map<String, Integer> typeMap;

    public static void init(){
        typeMap = new LinkedHashMap<>();

        typeMap.put("更新qq头像", updateQQHeadImage);
        typeMap.put("定时消息发送", sendMsgJob);
    }

    public static String getName(int type){
        if (typeMap.containsValue(type)){
            for (Map.Entry<String, Integer> entry:typeMap.entrySet()){
                if (entry.getValue() == type){
                    return entry.getKey();
                }
            }
        }

        return "未知";
    }
}
