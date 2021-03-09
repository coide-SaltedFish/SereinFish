package sereinfish.bot.event.group.repeater;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.event.SendMessageEvent;

/**
 * 复读机
 */
@EventListener
public class RepeaterEvent {
    @Event
    public void repeaterEvent(GroupMessageEvent event){
        RepeaterManager.getInstance().add(event.getGroup().getId(),event.getMessage());
    }

    @Event
    public void sendMessageEvent(SendMessageEvent event){
        RepeaterManager.getInstance().add(event.getSendTo().getId(),event.getMessage());
    }
}
