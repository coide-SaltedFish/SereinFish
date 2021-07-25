package sereinfish.bot.job;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.Map;

@Data
@AllArgsConstructor
@Getter
public class MyJob {
    private String id;//job id
    private String name;//job 名字
    private String owner;//拥有者

    private JobType type;//任务类型

    private Object obj;//任务参数

    private long startTime;//开始时间
    private long nextTime;//间隔时间

    /**
     * 任务类型
     */
    public enum JobType{
        updateQQHeadImage,//更新qq头像
        sendMsgJob,//定时消息发送
    }

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

    /**
     * 定时消息异常
     */
    public class MsgJobIllegalException extends Throwable{
        public MsgJobIllegalException(String msg){
            super(msg);
        }
    }
}
