package sereinfish.bot.event;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.event.SendMessageEvent;

@EventListener
public class MessageStateEvent {
    @Event
    public void messageEvent(MessageEvent event){
        MessageState.getInstance().receive();
    }

    @Event
    public void sendMessageEvent(SendMessageEvent.Post event){
        MessageState.getInstance().send();
    }
}
