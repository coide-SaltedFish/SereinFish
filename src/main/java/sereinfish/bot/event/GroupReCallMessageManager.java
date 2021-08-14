package sereinfish.bot.event;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.time.Time;

import java.util.*;

/**
 * 群消息撤回管理器
 */
public class GroupReCallMessageManager {
    private Map<Long, Stack<MsgInfo>> map = new LinkedHashMap<>();

    private static GroupReCallMessageManager reCallMessageManager;

    private GroupReCallMessageManager(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    delete(1000 * 50 * 2);
                    sleep(1000);
                }
            }

            public void sleep(int ms){
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                }
            }
        }).start();
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
        for (Map.Entry<Long, Stack<MsgInfo>> entry:map.entrySet()){
            entry.getValue().push(new MsgInfo(new Date().getTime(), message));
        }
    }

    /**
     * 得到群最近消息
     * @param group
     * @return
     */
    public MsgInfo getRecentMsg(long group){
        return map.get(group).pop();
    }

    public Stack<MsgInfo> getAllRecentMsg(long group){
        Stack<MsgInfo> reStack = map.get(group);
        map.put(group, new Stack<>());
        return reStack;
    }

    /**
     * 去除已超时消息
     * @param t
     */
    public void delete(long t){
        long time = new Date().getTime();
        for (Map.Entry<Long, Stack<MsgInfo>> entry:map.entrySet()){
            Stack<MsgInfo> messageMap = entry.getValue();
            for (int i  = 0; i < messageMap.size(); i++){
                if (messageMap.peek().getTime() <= time - t){
                    MsgInfo msgInfo = messageMap.pop();
                }
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