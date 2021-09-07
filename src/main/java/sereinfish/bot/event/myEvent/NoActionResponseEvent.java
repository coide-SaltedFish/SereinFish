package sereinfish.bot.event.myEvent;

import com.IceCreamQAQ.Yu.event.events.Event;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.event.ActionContextInvokeEvent;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;

@AllArgsConstructor
public class NoActionResponseEvent extends Event{
    private ActionContextInvokeEvent.Post event;

    public Message getMessage(){
        return event.getContext().getMessage();
    }

    public Contact getContact(){
        return event.getContext().getSource();
    }

    public Contact getSender(){
        return event.getContext().getSender();
    }

    public GroupConf getGroupConf(){
        if (getContact() instanceof Group){
            Group group = (Group) getContact();

            return ConfManager.getInstance().get(group.getId());
        }
        return null;
    }
}
