package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 黑名单相关控制器
 */
@GroupController
public class BlackListController {
    private Group group;
    private Member sender;
    private Message message;
    private DataBase dataBase;
    private GroupConf conf;

    private int maxTime = 25000;
    private int page_num = 5;
    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        this.group = group;
        this.sender = sender;
        this.message = message;

        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }

        conf = GroupConfManager.getInstance().get(group.getId());
        if ((Boolean) conf.getControl(GroupControlId.CheckBox_BlackList).getValue()){
            if (conf.isDataBaseEnable()){
                dataBase = DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID());
            }else {
                Message msg = MyYuQ.getMif().text("数据库未启用").toMessage();
                msg.setReply(message.getSource());
                throw msg.toThrowable();
            }
        }else {
            Message msg = MyYuQ.getMif().text("功能未启用").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    /**
     * 添加黑名单
     * @param qq        qq
     * @param remake    备注
     */
    @Action("\\[.!！]黑名单添加\\ {qq} \"{remake}\"")
    @Synonym({"\\[.!！]加黑\\ {qq} \"{remake}\""})
    public void add(long qq, String remake){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            //判断是否已存在
            if (blackListDao.exist(group.getId(),qq)){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("[" + qq + "]已存在于本群黑名单").toMessage());
            }else {
                blackListDao.insert(new BlackList(new Date(), qq, group.getId(), remake));
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("[" + qq + "]已添加到本群黑名单").toMessage());
            }
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 删除黑名单
     * @param qq        qq
     */
    @Action("\\[.!！]黑名单删除\\ {qq}")
    @Synonym({"\\[.!！]删黑\\ {qq}"})
    public void delete(long qq){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            //判断是否已存在
            if (!blackListDao.exist(group.getId(),qq)){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("[" + qq + "]不存在于本群黑名单").toMessage());
            }else {
                blackListDao.delete(group.getId(),qq);
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("[" + qq + "]已从本群黑名单移除").toMessage());
            }
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 查询本群黑名单
     */
    @Action("\\[.!！]本群黑名单\\ {page}")
    @Synonym("\\[.!！]黑名单\\ {page}")
    public void query(int page){
        if (page < 1){
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("页面错误：" + page).toMessage());
            return;
        }
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            ArrayList<BlackList> lists = blackListDao.query(group.getId());
            int page_max = lists.size() / page_num + (lists.size() % page_num == 0 ? 0:1);
            if (page > page_max){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("页面错误,最大页数[" + page_max + "]：" + page).toMessage());
                return;
            }
            ArrayList<String> msgList = new ArrayList<>();
            for (int i = (page - 1) * page_num; i < lists.size() && i < (page - 1) * page_num + page_num; i++){
                BlackList blackList = lists.get(i);
                msgList.add(Time.dateToString(new Date(blackList.getTime()),Time.DATE_FORMAT) + ":[" + blackList.getQq() + "]" + blackList.getRemarks());
            }
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("[黑名单列表(" + page + "/" + page_max + ")]",
                    "本群黑名单列表(" + page + "/" + page_max + ")","http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640",
                    msgList.toArray(new String[0]))).toMessage());
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 查询本群黑名单
     */
    @Action("\\[.!！]本群黑名单\\")
    @Synonym("\\[.!！]黑名单\\")
    public void query(){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            ArrayList<BlackList> lists = blackListDao.query(group.getId());
            int page_max = lists.size() / page_num + (lists.size() % page_num == 0 ? 0:1);

            ArrayList<String> msgList = new ArrayList<>();
            for (int i = 0; i < lists.size() && i < page_num; i++){
                BlackList blackList = lists.get(i);
                msgList.add(Time.dateToString(new Date(blackList.getTime()),Time.DATE_FORMAT) + ":[" + blackList.getQq() + "]" + blackList.getRemarks());
            }
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("[黑名单列表(1/" + page_max + ")]",
                    "本群黑名单列表(1/" + page_max + ")","http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640",
                    msgList.toArray(new String[0]))).toMessage());
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }
}
