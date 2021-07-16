package sereinfish.bot.event;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.mlog.SfLog;

import java.util.*;

/**
 * 群消息撤回管理器
 */
public class GroupReCallMessageManager {
    private Map<Long, Queue<MsgInfo>> map = new LinkedHashMap<>();

    private static GroupReCallMessageManager reCallMessageManager;

    private GroupReCallMessageManager(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    delete(1000 * 60 * 2);
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
        });
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
            map.put(group, new LinkedList<>());
        }
        for (Map.Entry<Long, Queue<MsgInfo>> entry:map.entrySet()){
            entry.getValue().offer(new MsgInfo(new Date().getTime(), message));
        }
    }

    /**
     * 得到群最近消息
     * @param group
     * @return
     */
    public Message getRecentMsg(long group){
        return map.get(group).poll().getMessage();
    }

    public Queue<MsgInfo> getAllRecentMsg(long group){
        Queue<MsgInfo> reQueue = map.get(group);
        map.put(group, new LinkedList<>());
        return reQueue;
    }

    /**
     * 去除已超时消息
     * @param t
     */
    public void delete(long t){
        long time = new Date().getTime();
        for (Map.Entry<Long, Queue<MsgInfo>> entry:map.entrySet()){
            Queue<MsgInfo> messageMap = entry.getValue();
            for (int i  = 0; i < messageMap.size(); i++){
                if (messageMap.peek().getTime() <= time - t){
                    messageMap.poll();
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
