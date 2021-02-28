package sereinfish.bot.entity.conf;

import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;

public class GroupConfManager {

    private static GroupConfManager manager;
    private GroupConfManager(){
        //TODO：从文件加载
    }

    public static GroupConfManager init(){
        manager = new GroupConfManager();
        return manager;
    }

    public static GroupConfManager getInstance(){
        if (manager == null){
            throw new NullPointerException("群配置管理器尚未初始化");
        }
        return manager;
    }

    /**
     * 添加新配置
     * @param groupConf
     */
    public boolean put(GroupConf groupConf){
        try {
            write(groupConf);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"群[" + groupConf.getGroup() + "]写入群配置失败",e);
            return false;
        }
        return true;
    }

    /**
     * 得到群配置
     * @param group
     */
    public GroupConf get(long group){
        GroupConf conf = null;
        try {
            conf = read(group);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"群[" + group + "]读取群配置失败",e);
        }
        if (conf == null){
            conf = new GroupConf(group).init();
            put(conf);
        }
        return conf;
    }

    /**
     * 写入群配置
     * @param conf
     */
    public static void write(GroupConf conf) throws IOException {
        FileHandle.write(new File(FileHandle.groupDataPath,
                conf.getGroup() + "/conf.json"),
                MyYuQ.toJson(conf,GroupConf.class));
    }

    /**
     * 读取群配置
     * @param group
     * @return
     */
    public static GroupConf read(long group) throws IOException {
        return MyYuQ.toClass(FileHandle.read(new File(FileHandle.groupDataPath,
                group + "/conf.json")),GroupConf.class);
    }
}
