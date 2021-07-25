package sereinfish.bot.job;

import com.IceCreamQAQ.Yu.job.JobManager;

import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * job管理类
 */
public class JobSFManager {
    public static final int JOB_FRIEND = -1;//好友任务

    //id,job
    private Map<Long, Map<String,MyJob>> myJobMap = new HashMap<>();

    private JobManager jobManager;
    private static JobSFManager jobSFManager;
    private JobSFManager(){
        jobManager = MyYuQ.getJobManager();
        //读取各群配置并初始化
    }

    public static JobSFManager init(){
        jobSFManager = new JobSFManager();
        return jobSFManager;
    }

    public static JobSFManager getInstance(){
        if (jobSFManager == null){
            throw new NullPointerException();
        }
        return jobSFManager;
    }

    /**
     * 添加计划任务
     * @param myJob
     */
    public void add(long group, MyJob myJob) throws JobNotFindException, MyJob.MsgJobIllegalException {
        Runnable run = getRun(myJob);

        String id = jobManager.registerTimer(run, myJob.getStartTime(), myJob.getNextTime());
        myJob.setId(id);

        if (myJobMap.containsKey(group)){
            myJobMap.get(group).put(id, myJob);
        }else {
            Map<String, MyJob> map =  new LinkedHashMap<>();
            map.put(id, myJob);
            myJobMap.put(group, map);
        }
    }

    /**
     * 任务删除
     * @param id
     */
    public void delete(String id){
        jobManager.deleteTimer(id);
    }

    /**
     * 解析运行任务
     * @param myJob
     * @return
     */
    private Runnable getRun(MyJob myJob) throws JobNotFindException, MyJob.MsgJobIllegalException {
        if (myJob.getType() == MyJob.JobType.updateQQHeadImage){
            return new JobEntity.updateQQHeadImage();
        }else if(myJob.getType() == MyJob.JobType.sendMsgJob){
            if (myJob.getObj() instanceof MyJob.JobMsg){
                MyJob.JobMsg jobMsg = (MyJob.JobMsg) myJob.getObj();
                return new JobEntity.SendMsgJob(jobMsg.getRecipient(), jobMsg.getMessage());
            }else {
                return null;
            }
        }

        throw new JobNotFindException();
    }

    /**
     * 得到群组已注册任务列表
     * @param group
     * @return
     */
    public Map<String, MyJob> getGroupJobList(long group){
        return myJobMap.get(group);
    }

    /**
     * 得到所有已注册任务列表
     * @return
     */
    public Map<Long, Map<String,MyJob>> getJobList(){
        return myJobMap;
    }

    /**
     * 未知Job类型
     */
    public class JobNotFindException extends Throwable{
        public JobNotFindException(){
            super("未知Job类型");
        }
    }
}
