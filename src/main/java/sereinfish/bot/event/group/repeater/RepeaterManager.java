package sereinfish.bot.event.group.repeater;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;
import java.util.Map;

/**
 * 复读管理器
 */
public class RepeaterManager {
    //TODO:配置文件添加复读条数限制
    private int n = 3;//当消息重复3次复读

    private Map<Group, ReMsg> groupArrayListMap = new HashMap<>();//消息列表

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
    public void add(Group group, Message message){
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
        for (Map.Entry<Group,ReMsg> entry:groupArrayListMap.entrySet()){
            if (!entry.getValue().isRepeater && entry.getValue().num >= n){
                entry.getValue().isRepeater = true;
                MyYuQ.sendGroupMessage(entry.getKey(),Message.Companion.toMessageByRainCode(entry.getValue().msg));
            }
        }
    }

    class ReMsg{
        public boolean isRepeater = false;
        public String msg;
        public int num;

        public ReMsg(String msg, int num) {
            this.msg = msg;
            this.num = num;
        }
    }
}
