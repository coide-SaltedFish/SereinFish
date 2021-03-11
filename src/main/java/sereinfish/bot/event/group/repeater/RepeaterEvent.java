package sereinfish.bot.event.group.repeater;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.event.SendMessageEvent;
import com.icecreamqaq.yuq.message.Message;

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
    public void sendMessageEvent(SendMessageEvent.Post event){
        Message message = event.getMessage();
        message.setSource(event.getMessageSource());
        RepeaterManager.getInstance().add(event.getSendTo().getId(),message);
    }
}
