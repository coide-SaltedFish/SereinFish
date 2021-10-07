package sereinfish.bot.controller;

import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Global;
import com.IceCreamQAQ.Yu.controller.ActionContext;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.mlog.SfLog;

@GroupController
@PrivateController
public class GlobalController {

    @Before(weight = -1000)
    @Global
    public void before(ActionContext actionContext, Group group){
        if (group != null){
            actionContext.set("group", group);
            GroupConf groupConf = ConfManager.getInstance().get(group.getId());
            actionContext.set("groupConf", groupConf);

            if (groupConf.getDataBaseConfig() != null){
                DataBase dataBase = null;
                try {
                    dataBase = DataBaseManager.getInstance().getDataBase(groupConf.getDataBaseConfig().getID());
                    actionContext.set("dataBase", dataBase);
                } catch (Exception e) {
                    SfLog.getInstance().e(this.getClass(), e);
                } catch (IllegalModeException e) {
                    SfLog.getInstance().e(this.getClass(), e);
                }
            }
        }
    }
}
