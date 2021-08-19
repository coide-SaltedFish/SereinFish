package sereinfish.bot.data.conf.entity;

import com.IceCreamQAQ.Yu.hook.HookInfo;
import com.IceCreamQAQ.Yu.hook.HookMethod;
import com.IceCreamQAQ.Yu.hook.HookRunnable;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;

public class ConfHook implements HookRunnable {
    @Override
    public void init(HookInfo hookInfo) {
    }

    @Override
    public boolean preRun(HookMethod hookMethod) {
        return false;
    }

    @Override
    public void postRun(HookMethod hookMethod) {
        GroupConf groupConf = (GroupConf) hookMethod.paras[0];
        if (groupConf != null){
            File confFile = new File(FileHandle.groupDataPath, groupConf.getGroup() + "/conf.json");
            try {
                FileHandle.write(confFile, MyYuQ.toJson(groupConf, GroupConf.class));
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), "群：" + groupConf.getGroup() + "配置保存失败");
            }
        }else {
            SfLog.getInstance().e(this.getClass(), "hook失败：类实例为null：" + hookMethod.info);
        }
    }

    @Override
    public boolean onError(HookMethod hookMethod) {
        SfLog.getInstance().e(this.getClass(), hookMethod.error);
        return false;
    }
}
