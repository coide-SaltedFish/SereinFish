package sereinfish.bot.event.group.repeater;

import com.icecreamqaq.yuq.message.Message;
import lombok.Getter;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;
import java.util.Map;

/**
 * 复读管理器
 */
public class RepeaterManager {
    //TODO:添加配置文件
    private int n = 3;//当消息重复3次复读

    private Map<Long, ReMsg> groupArrayListMap = new HashMap<>();//消息列表

    private static RepeaterManager repeaterManager;
    private RepeaterManager(){

    }

    public static RepeaterManager init(){
        repeaterManager = new RepeaterManager();
        return repeaterManager;
    }

    public static RepeaterManager getInstance(){
        if (repeaterManager == null){
            throw new NullPointerException("复读管理器尚未初始化");
        }
        return repeaterManager;
    }

    /**
     * 添加消息记录
     * @param group
     * @param message
     */
    public void add(long group, Message message){
        String msg = Message.Companion.toCodeString(message);

        if (groupArrayListMap.containsKey(group)){
            ReMsg reMsg = groupArrayListMap.get(group);
            if (reMsg.msg.equals(msg)){
                reMsg.num++;
            }else {
                groupArrayListMap.put(group,new ReMsg(msg,1));
            }
        }else {
            groupArrayListMap.put(group,new ReMsg(msg,1));
        }

        check();
    }

    /**
     * 检查是否该复读了
     */
    public void check(){
        for (Map.Entry<Long,ReMsg> entry:groupArrayListMap.entrySet()){
            GroupConf conf = ConfManager.getInstance().get(entry.getKey());
            if (conf.isEnable() && conf.isReReadEnable()){
                if (!entry.getValue().isRepeater && entry.getValue().num >= n){
                    entry.getValue().isRepeater = true;
                    if (MyYuQ.getYuQ().getGroups().containsKey(entry.getKey())){
                        if(!entry.getValue().msg.contains("<Rain:NoImpl:NoImpl>")){
                            MyYuQ.getYuQ().getGroups().get(entry.getKey()).sendMessage(Message.Companion.toMessageByRainCode(entry.getValue().msg));
                        }else {
                            MyYuQ.getYuQ().getGroups().get(entry.getKey()).sendMessage(Message.Companion.toMessageByRainCode("啪唧，打断（混乱"));
                        }
                    }
                }
            }
        }
    }

    @Getter
    class ReMsg{
        private boolean isRepeater = false;
        private String msg;
        private int num;

        public ReMsg(String msg, int num) {
            this.msg = msg;
            this.num = num;
        }
    }
}
