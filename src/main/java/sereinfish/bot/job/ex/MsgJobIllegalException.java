package sereinfish.bot.job.ex;

/**
 * 定时消息异常
 */
public class MsgJobIllegalException extends Throwable{
    public MsgJobIllegalException(String msg){
        super(msg);
    }
}