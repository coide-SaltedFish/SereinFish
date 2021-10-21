package sereinfish.bot.entity.sf.msg.code;

import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class SFMsgCodeContact {
    Map<String,Object> map = new HashMap<>();

    Contact sender;
    Contact source;

    public SFMsgCodeContact(BotActionContext botActionContext) {
        this.botActionContext = botActionContext;

        sender = botActionContext.getSender();
        source = botActionContext.getSource();
    }

    public SFMsgCodeContact(Contact sender, Contact source) {
        this.sender = sender;
        this.source = source;
    }

    BotActionContext botActionContext;
    Message reMessage = new Message();
    String parameter;

    public Message getReMessage(){
        Message message = new Message();
        message.setRecallDelay(reMessage.getRecallDelay());
        return message;
    }

    public void save(String key, Object o){
        map.put(key, o);
    }

    public Object get(String key){
        return map.get(key);
    }

    public boolean containsKey(Object key){
        return map.containsKey(key);
    }

    public Object getOrDefault(String key, Object defaultValue){
        return map.getOrDefault(key, defaultValue);
    }

    public Group getGroup(){
        if (source instanceof Group){
            return (Group) source;
        }
        return null;
    }
}
