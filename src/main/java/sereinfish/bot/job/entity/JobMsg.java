package sereinfish.bot.job.entity;

import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.job.ex.MessageJobIllegalException;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;

@AllArgsConstructor
public class JobMsg{
    private boolean isGroup;
    private String msg;
    private long recipient;//接收人

    /**
     * 得到消息
     * @return
     */
    public Message[] getMessage(){
        SFMsgCodeContact sfMsgCodeContact = null;
        try {
            sfMsgCodeContact = new SFMsgCodeContact(getRecipient(), getRecipient());
        } catch (MessageJobIllegalException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message[]{new Message().lineQ().text("错误：" + e.getMessage()).getMessage()};
        }
        return SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact, msg).toArray(new Message[]{});
    }

    /**
     * 得到接收对象
     * @return
     * @throws MessageJobIllegalException
     */
    public Contact getRecipient() throws MessageJobIllegalException {
        if (isGroup){
            if(MyYuQ.getYuQ().getGroups().containsKey(recipient)){
                return MyYuQ.getYuQ().getGroups().get(recipient);
            }else {
                throw new MessageJobIllegalException("未知的群组：" + recipient);
            }
        }else {
            if (MyYuQ.getYuQ().getFriends().containsKey(recipient)){
                return MyYuQ.getYuQ().getFriends().get(recipient);
            }else {
                throw new MessageJobIllegalException("未知的好友：" + recipient);
            }
        }
    }
}
