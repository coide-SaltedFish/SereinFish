package sereinfish.bot.event;

import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.util.*;

/**
 * 群消息撤回管理器
 */
public class GroupReCallMessageManager {
    private Map<Long, Stack<MsgInfo>> map = new LinkedHashMap<>();

    private static GroupReCallMessageManager reCallMessageManager;

    private GroupReCallMessageManager(){
    }

    public static GroupReCallMessageManager init(){
        reCallMessageManager = new GroupReCallMessageManager();
        return reCallMessageManager;
    }

    public static GroupReCallMessageManager getInstance(){
        if (reCallMessageManager == null){
            throw new NullPointerException();
        }
        return reCallMessageManager;
    }

    /**
     * 添加新项
     * @param group
     * @param message
     */
    public void add(long group, Message message){
        if (!map.containsKey(group)){
            map.put(group, new Stack<>());
        }

        MsgInfo msgInfo = new MsgInfo(new Date().getTime(), message);
        map.get(group).push(msgInfo);
        MyYuQ.getJobManager().registerTimer(new Runnable() {
            @Override
            public void run() {
                map.get(group).remove(msgInfo);
            }
        }, 1000 * 55 * 2);
    }

    /**
     * 得到群最近消息
     * @param group
     * @return
     */
    public void reCallRecentMsg(long group) throws IllegalStateException{
        MsgInfo msgInfo = map.get(group).pop();
        if (msgInfo == null){
            throw new IllegalStateException("未能找到最近消息：" + group);
        }
        int i = msgInfo.getMessage().recall();
        SfLog.getInstance().d(this.getClass(), "消息撤回: " + i + " >>发送时间：" + Time.dateToString(msgInfo.getTime(), Time.LOG_TIME));
    }

    public void reCallAllRecentMsg(long group){
        Stack<MsgInfo> reStack = map.get(group);
        map.put(group, new Stack<>());
        for (MsgInfo msgInfo:reStack){
            if (msgInfo != null){
                int i = msgInfo.getMessage().recall();
                SfLog.getInstance().d(this.getClass(), "消息撤回: " + i + " >>发送时间：" + Time.dateToString(msgInfo.getTime(), Time.LOG_TIME));
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public class MsgInfo{
        private long time;//时间戳
        private Message message;
    }
}
