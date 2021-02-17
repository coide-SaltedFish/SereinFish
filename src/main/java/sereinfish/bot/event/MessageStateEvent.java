package sereinfish.bot.event;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.MessageEvent;

@EventListener
public class MessageStateEvent {
    @Event
    public void messageEvent(MessageEvent event){
        MessageState.getInstance().add();
    }
}
