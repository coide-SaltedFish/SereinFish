package sereinfish.bot.job.entity;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import sereinfish.bot.job.ex.MsgJobIllegalException;
import sereinfish.bot.myYuq.MyYuQ;

@AllArgsConstructor
public class JobMsg{
    private boolean isGroup;
    private String msg;
    private long recipient;//接收人

    /**
     * 得到消息
     * @return
     */
    public Message getMessage(){
        return Message.Companion.toMessageByRainCode(msg);
    }

    /**
     * 得到接收对象
     * @return
     * @throws MsgJobIllegalException
     */
    public Contact getRecipient() throws MsgJobIllegalException {
        if (isGroup){
            if(MyYuQ.getYuQ().getGroups().containsKey(recipient)){
                return MyYuQ.getYuQ().getGroups().get(recipient);
            }else {
                throw new MsgJobIllegalException("未知的群组：" + recipient);
            }
        }else {
            if (MyYuQ.getYuQ().getFriends().containsKey(recipient)){
                return MyYuQ.getYuQ().getFriends().get(recipient);
            }else {
                throw new MsgJobIllegalException("未知的好友：" + recipient);
            }
        }
    }
}
