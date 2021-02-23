package sereinfish.bot.event.group;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.*;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.ResultSet;

/**
 * 群事件监听
 */
@EventListener
public class OnGroupMessageEvent {

    /**
     * 群消息记录
     * @param event
     */
    @Event
    public void groupMessageEvent(GroupMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getGroup(), event.getSender().getId(), event.getMessage())){
            MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * 私聊消息记录
     * @param event
     */
    @Event
    public void privateMessageEvent(PrivateMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(-1, event.getSender().getId(), event.getMessage())){
            MyYuQ.sendMessage(event.getSender(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * bot发送的消息记录
     * @param event
     */
    @Event
    public void sendMessageEvent(SendMessageEvent.Post event){
        Message message = event.getMessage();
        message.setSource(event.getMessageSource());
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getSendTo().getId(), MyYuQ.getYuQ().getBotId(), event.getMessage())){
            MyYuQ.sendMessage(event.getSendTo(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * 退群事件
     * @param event
     */
    @Event
    public void groupMemberLeaveEvent(GroupMemberLeaveEvent event){
        MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("刚刚，" + event.getMember().getNameCard() + "(" +
                event.getMember().getName() + ")[" + event.getMember().getId() + "]离开了我们，他说过的最后一句话是：").toMessage());
        try {
            GroupHistoryMsg groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().queryLast(event.getGroup().getId(),event.getMember().getId());

            if (groupHistoryMsg == null){
                MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("他好像还没说过话").toMessage());
                return;
            }else {
                MyYuQ.sendGroupMessage(event.getGroup(),Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg()));
            }
        }catch (Exception e){
            MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("出现了一点错误喵：" + e.getMessage()).toMessage());
        }
    }
}
