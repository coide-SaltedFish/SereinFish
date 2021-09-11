package sereinfish.bot.job.ex;

/**
 * 定时消息异常
 */
public class MessageJobIllegalException extends Throwable{
    public MessageJobIllegalException(String msg){
        super(msg);
    }
}