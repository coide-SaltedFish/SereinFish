package sereinfish.bot.event.group.repeater;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;

/**
 * 复读机
 */
@EventListener
public class RepeaterEvent {
    @Event
    public void repeaterEvent(GroupMessageEvent event){
        RepeaterManager.getInstance().add(event.getGroup(),event.getMessage());
    }
}
