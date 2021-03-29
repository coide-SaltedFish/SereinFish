package sereinfish.bot.job;

import com.IceCreamQAQ.Yu.job.JobManager;

import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;

import kotlin.coroutines.Continuation;

import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.ContinuationKt;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.HashMap;
import java.util.Map;

/**
 * job管理类
 */
public class JobSFManager {
    private Map<String,MyJob> myJobMap = new HashMap<>();

    private JobManager jobManager;
    private static JobSFManager jobSFManager;
    private JobSFManager(){
        jobManager = MyYuQ.getJobManager();
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

    public void add(MyJob job){
        SfLog.getInstance().d(this.getClass(),"注册定时任务：" + job.toString());
        String t = jobManager.registerTimer(getJobRun(job),job.getTime());
        myJobMap.put(t,job);
        SfLog.getInstance().d(this.getClass(),"注册定时任务完成：" + t);
    }

    public void delete(String id){
        jobManager.deleteTimer(id);
    }

    public void stop(){
        jobManager.stop();
    }

    public void start(){
        jobManager.start();
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    /**
     * 得到定时任务的run
     * @param job
     * @return
     */
    public Runnable getJobRun(MyJob job){
        Runnable runnable;

        switch (job.getType()){
            case MSG:
                runnable = new Runnable() {
                    final MyJob fJob = job;

                    @Override
                    public void run() {
                        jobSendMessage(fJob,Message.Companion.toMessage(fJob.getValue()));
                    }
                };
                break;
            case ACTION:
                runnable = new Runnable() {
                    final MyJob fJob = job;

                    @Override
                    public void run() {
                        runAction(fJob);
                    }
                };
                break;
            default:
                runnable = new Runnable() {
                    final MyJob fJob = job;

                    @Override
                    public void run() {
                        jobSendMessage(fJob,Message.Companion.toMessage("未知定时任务类型：" +
                                fJob.getType()));
                    }
                };
        }

        return runnable;
    }

    private void jobSendMessage(MyJob job,Message m){
        if (job.isGroup()){
            if (MyYuQ.getYuQ().getGroups().containsKey(job.getReceive())){
                MyYuQ.getYuQ().getGroups().get(job.getReceive()).sendMessage(m);
            }else {
                SfLog.getInstance().w(this.getClass(),"定时任务已失效,未找到群组：" + job.toString());
            }
        }else {
            if (MyYuQ.getYuQ().getFriends().containsKey(job.getReceive())){
                MyYuQ.getYuQ().getFriends().get(job.getReceive()).sendMessage(m);
            }else {
                SfLog.getInstance().w(this.getClass(),"定时任务已失效,未找到好友：" + job.toString());
            }
        }
    }

    /**
     * 执行action
     */
    private void runAction(MyJob job){
//        ContinuationKt.createCoroutine()
//        MyYuQ.getRainBot().receiveGroupMessage(Member,Message,Continuation)
    }
}
