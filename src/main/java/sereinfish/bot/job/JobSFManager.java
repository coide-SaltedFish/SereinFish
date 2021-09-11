package sereinfish.bot.job;

import com.IceCreamQAQ.Yu.job.JobManager;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.job.conf.JobConf;
import sereinfish.bot.job.entity.JobMsg;
import sereinfish.bot.job.entity.JobType;
import sereinfish.bot.job.ex.MessageJobIllegalException;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.util.ArrayList;
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
        for (Group group:MyYuQ.getGroups()){
            try {
                JobConf jobConf = JobConf.getGroupJobs(group.getId());
                for (MyJob myJob:jobConf.getMyJobs()){
                    add(group.getId(), myJob);
                }
            } catch (JobNotFindException e) {
                SfLog.getInstance().e(this.getClass(), "定时任务添加失败：" + group,e);
            } catch (MessageJobIllegalException e) {
                SfLog.getInstance().e(this.getClass(), "定时任务添加失败：" + group,e);
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), "定时任务添加失败：" + group,e);
            }
        }
    }

    public static JobSFManager init(){
        JobType.init();
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
    public void add(long group, MyJob myJob) throws JobNotFindException, MessageJobIllegalException {
        Runnable run = getRun(myJob);

        String id = jobManager.registerTimer(run, myJob.getAtTime(), true);

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
    public void delete(long group, String id){
        jobManager.deleteTimer(id);
        myJobMap.get(group).remove(id);
    }

    /**
     * 解析运行任务
     * @param myJob
     * @return
     */
    private Runnable getRun(MyJob myJob) throws JobNotFindException, MessageJobIllegalException {
        if (myJob.getType() == JobType.updateQQHeadImage){
            return new JobEntity.updateQQHeadImage();
        }else if(myJob.getType() == JobType.sendMsgJob){//
            JobMsg jobMsg = MyYuQ.toClass(myJob.getValue(), JobMsg.class);
            return new JobEntity.SendMsgJob(jobMsg.getRecipient(), jobMsg.getMessage());
        }

        throw new JobNotFindException("未知Job类型:" + myJob.getType());
    }

    /**
     * 得到群组已注册任务列表
     * @param group
     * @return
     */
    public Map<String, MyJob> getGroupJobMap(long group){
        return myJobMap.get(group);
    }

    /**
     * 得到群组已注册任务列表
     * @param group
     * @return
     */
    public ArrayList<MyJob> getGroupJobList(long group){
        ArrayList<MyJob> myJobs = new ArrayList<>();
        if (myJobMap.containsKey(group)){
            for (Map.Entry<String, MyJob> entry:myJobMap.get(group).entrySet()){
                myJobs.add(entry.getValue());
            }
        }
        return myJobs;
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
        public JobNotFindException(String s){
            super(s);
        }
    }
}
