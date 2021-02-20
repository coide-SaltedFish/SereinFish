package sereinfish.bot.event.group;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 群事件监听
 */
@EventListener
public class OnGroupMessageEvent {
    @Event
    public void groupMessageEvent(GroupMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getGroup(), event.getSender().getId(), event.getMessage())){
            MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }
}
