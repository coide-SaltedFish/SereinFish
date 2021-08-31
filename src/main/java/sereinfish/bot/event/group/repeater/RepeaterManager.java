package sereinfish.bot.event.group.repeater;

import com.alibaba.fastjson.JSONArray;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.BotUtils;

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
     * @param contact
     * @param message
     */
    public void add(Contact contact, Message message){
        JSONArray msg = BotUtils.messageToJsonArray(message);

        if (groupArrayListMap.containsKey(contact.getId())){
            ReMsg reMsg = groupArrayListMap.get(contact.getId());
            if (BotUtils.equalsMessageJsonArray(reMsg.getMsg(), msg)){
                reMsg.num++;
            }else {
                groupArrayListMap.put(contact.getId(),new ReMsg(contact, message,1));
            }
        }else {
            groupArrayListMap.put(contact.getId(),new ReMsg(contact, message,1));
        }

        check();
    }

    /**
     * 检查是否该复读了
     */
    public void check(){
        for (Map.Entry<Long,ReMsg> entry:groupArrayListMap.entrySet()){
            GroupConf conf = ConfManager.getInstance().get(entry.getValue().getContact().getId());

            if (conf.isEnable() && conf.isReReadEnable()){
                if (!entry.getValue().isRepeater && entry.getValue().num >= conf.getReReadNum()){
                    entry.getValue().isRepeater = true;
                    entry.getValue().getContact().sendMessage(entry.getValue().getMessage());
                }
            }
        }
    }

    @Getter
    class ReMsg{
        private Contact contact;
        private Message message;
        private boolean isRepeater = false;
        private JSONArray msg;
        private int num;

        public ReMsg(Contact contact, Message message, int num) {
            this.contact = contact;
            this.message = message;
            this.msg = BotUtils.messageToJsonArray(message);
            this.num = num;
        }
    }
}
