package sereinfish.bot.job.conf;

import lombok.Getter;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Getter
public class JobConf {
    private long group;
    private ArrayList<MyJob> myJobs = new ArrayList<>();

    public JobConf(long group) {
        this.group = group;
    }

    public void addJob(MyJob myJob){
        myJobs.add(myJob);
        save();
    }

    /**
     * 得到job
     * @param id
     * @return
     */
    public MyJob getMyJob(String id){
        for (MyJob myJob:myJobs){
            if (myJob.getId().equals(id)){
                return myJob;
            }
        }
        return null;
    }

    /**
     * 删除
     * @param id
     */
    public void deleteJob(String id){
        for (int i = 0; i < myJobs.size(); i++){
            if (myJobs.get(i).getId().equals(id)){
                myJobs.remove(i);
                break;
            }
        }
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.groupDataPath, group + "/jobConf.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, JobConf.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "群：" + group + "Job配置保存失败");
        }
    }

    /**
     * 得到配置
     * @param group
     * @return
     * @throws IOException
     */
    public static JobConf getGroupJobs(long group) throws IOException {
        File confFile = new File(FileHandle.groupDataPath, group + "/jobConf.json");
        if (confFile.exists() && confFile.isFile()){
            String json = FileHandle.read(confFile);
            return MyYuQ.toClass(json, JobConf.class);
        }else {
            JobConf jobConf = new JobConf(group);
            jobConf.save();
            return jobConf;
        }
    }
}
