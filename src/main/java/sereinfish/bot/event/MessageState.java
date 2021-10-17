package sereinfish.bot.event;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.tray.AppTray;

import java.util.*;

public class MessageState {
    private ArrayList<Long> receiveMsgList = new ArrayList<>();
    private ArrayList<Long> sendMsgList = new ArrayList<>();

    private static MessageState messageState;
    private MessageState(){

    }

    public static MessageState getInstance(){
        if (messageState == null){
            messageState = new MessageState();
        }
        return messageState;
    }

    public void receive(){
        receiveMsgList.add(System.currentTimeMillis());
    }

    public void send(){
        sendMsgList.add(System.currentTimeMillis());
    }

    /**
     * 返回范围时间内接收的消息数
     * @param t
     * @return
     */
    public int getReceiveMsgNum(long t){
        long time = System.currentTimeMillis() - t;
        int num = 0;

        for (int i = receiveMsgList.size() - 1; i >= 0 && receiveMsgList.get(i) > time; i--, num ++);

        return num;
    }

    /**
     * 返回范围时间内发送的消息数
     * @param t
     * @return
     */
    public int getSendMsgNum(long t){
        long time = System.currentTimeMillis() - t;
        int num = 0;

        for (int i = sendMsgList.size() - 1;  i >= 0 && sendMsgList.get(i) > time; i--, num ++);

        return num;
    }

    /**
     * 返回接收到的消息数量
     * @return
     */
    public int getReceiveMsgNum(){
        return receiveMsgList.size();
    }

    /**
     * 返回发送的消息数量
     * @return
     */
    public int getSendMsgNum(){
        return sendMsgList.size();
    }
}
