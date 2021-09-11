package sereinfish.bot.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import sereinfish.bot.job.entity.JobMsg;
import sereinfish.bot.myYuq.MyYuQ;

@Data
@AllArgsConstructor
@Getter
public class MyJob{
    private String id;//job id
    private String name;//job 名字

    private int type;//任务类型

    private String value;//任务参数

    private String atTime;


    public JobMsg getJobMsg(){
        return MyYuQ.toClass(value, JobMsg.class);
    }

}
