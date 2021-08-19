package sereinfish.bot.data.conf;

import com.IceCreamQAQ.Yu.hook.YuHook;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;

public class ConfManager {
    private static ConfManager manager;
    private ConfManager(){
    }

    public static ConfManager init(){
        manager = new ConfManager();
        return manager;
    }

    public static ConfManager getInstance(){
        if (manager == null){
            throw new NullPointerException("群配置管理器尚未初始化");
        }
        return manager;
    }

    /**
     * 得到群配置
     * @param group
     * @return
     */
    public GroupConf get(long group){
        File confFile = new File(FileHandle.groupDataPath, group + "/conf.json");
        if (confFile.exists() && confFile.isFile()){
            try {
                String fileStr = FileHandle.read(confFile);
                GroupConf groupConf = MyYuQ.toClass(fileStr, GroupConf.class);
                return groupConf;
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), e);
                return null;
            }
        }
        GroupConf groupConf = new GroupConf(group);
        groupConf.save();
        return groupConf;
    }
}
