package sereinfish.bot.event.myEvent;

import com.IceCreamQAQ.Yu.event.events.Event;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.Message;

public class BotNameEvent extends Event {
    private GroupMessageEvent groupMessageEvent;

    public BotNameEvent(GroupMessageEvent groupMessageEvent) {
        this.groupMessageEvent = groupMessageEvent;
    }

    public Member getSender(){
        return groupMessageEvent.getSender();
    }

    public Message getMessage(){
        return groupMessageEvent.getMessage();
    }

    public Contact getContact(){
        return groupMessageEvent.getGroup();
    }
}
