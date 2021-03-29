package sereinfish.bot.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class MyJob {
    private boolean isGroup;
    private long receive;
    private String time;
    private MyJobType type;
    private String value;

    @Override
    public String toString(){
        return "[isGroup:" + isGroup + ",receive:" + receive + ",Time:" + time + ",type:" + type + ",Value:" + value + "]";
    }

    public enum MyJobType{
        //定时消息
        MSG,
        //action触发
        ACTION,
    }
}
