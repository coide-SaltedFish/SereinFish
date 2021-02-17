package sereinfish.bot.event;

import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.log.SfLog;
import sereinfish.bot.ui.tray.AppTray;

import java.util.*;

public class MessageState {
    private Queue<Long> messageMap = new LinkedList<>();
    private boolean isRun = true;

    private static MessageState messageState;
    private MessageState(){

    }

    public static MessageState init(){
        messageState = new MessageState();
        messageState.updateMsgState();

        return messageState;
    }

    public static MessageState getInstance(){
        if (messageState == null){
            throw new NullPointerException("消息状态队列未初始化");
        }
        return messageState;
    }

    public void add(){
        messageMap.offer(new Date().getTime());
    }

    /**
     * 更新信息状态
     */
    public void updateMsgState(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun){
                    AppTray.getInstance().setTip("消息数：" + getMsgNum(1000 * 60) + "条/分钟");
                    //最大保存1000条消息
                    delete(1000);
                    sleep(1500);
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

    /**
     * 返回范围时间内的消息数
     * @param t
     * @return
     */
    public int getMsgNum(long t){
        int n = 0;
        for (long time:messageMap){
            if (new Date().getTime() - t < time){
                n++;
            }
        }
        return n;
    }

    /**
     * 删除多余消息
     * @param n
     */
    public void delete(int n){
        if (n < messageMap.size()){
            for (int i = 0; i < messageMap.size() - n; i++){
                messageMap.poll();
            }
        }
    }
}
