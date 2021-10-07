package sereinfish.bot.event.group.repeater;

import lombok.Getter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;
import java.util.Map;

/**
 * 复读管理器
 */
public class RepeaterManager {

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
    public void add(long sender, Group group, MessageChain message){
        String msg = message.toString().replaceAll("\\[mirai:source:\\[-*[0-9]*],\\[-*[0-9]*]]", "");

        if (groupArrayListMap.containsKey(group.getId())){
            ReMsg reMsg = groupArrayListMap.get(group.getId());
            if (msg.equals(reMsg.getMsg())){
                reMsg.num++;
            }else {
                groupArrayListMap.put(group.getId(),new ReMsg(group, message,1));
            }
        }else {
            groupArrayListMap.put(group.getId(),new ReMsg(group, message,1));
        }

        //如果是发送者是bot，则结束本次复读
        if (sender == MyYuQ.getYuQ().getBotId()){
            groupArrayListMap.get(group.getId()).isRepeater = true;
        }

        check();
    }

    /**
     * 检查是否该复读了
     */
    public void check(){
        for (Map.Entry<Long,ReMsg> entry:groupArrayListMap.entrySet()){
            GroupConf conf = ConfManager.getInstance().get(entry.getValue().getGroup().getId());

            if (conf.isEnable() && conf.isReReadEnable()){
                if (!entry.getValue().isRepeater && entry.getValue().num >= conf.getReReadNum()){
                    entry.getValue().isRepeater = true;
                    entry.getValue().getGroup().sendMessage(entry.getValue().getMessage());
                }
            }
        }
    }

    @Getter
    class ReMsg{
        private Group group;
        private MessageChain message;
        private boolean isRepeater = false;
        private String msg;
        private int num;

        public ReMsg(Group group, MessageChain message, int num) {
            this.group = group;
            this.message = message;
            this.msg = message.toString().replaceAll("\\[mirai:source:\\[-*[0-9]*],\\[-*[0-9]*]]", "");
            this.num = num;
        }
    }
}
