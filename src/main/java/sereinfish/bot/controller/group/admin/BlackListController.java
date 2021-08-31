package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
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
@Menu(name = "黑名单", permissions = Permissions.GROUP_ADMIN)
public class BlackListController {
    private int maxTime = 25000;
    private int page_num = 5;
    /**
     * 权限检查
     */
    @Before
    public void before(Group group, DataBase dataBase, GroupConf groupConf, Member sender, Message message) throws IllegalModeException, SQLException, ClassNotFoundException {
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
        if (groupConf.isBlackListGroupEnable()){//判断黑名单是否启用
            if (dataBase == null){
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
    @Action("\\^[.!！]黑名单添加$\\ {qq} {remake}")
    @Synonym({"\\^[.!！]加黑$\\ {qq} {remake}"})
    @MenuItem(name = "黑名单添加", usage = "[.!！]黑名单添加 {qq} {remake}", description = "添加到本群黑名单", permission = Permissions.GROUP_ADMIN)
    public Message add(Group group, DataBase dataBase, long qq, String remake){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            //判断是否已存在
            if (blackListDao.exist(group.getId(),qq)){
                return MyYuQ.getMif().text("[" + qq + "]已存在于本群黑名单").toMessage();
            }else {
                blackListDao.insert(new BlackList(new Date(), qq, group.getId(), remake));
                return MyYuQ.getMif().text("[" + qq + "]已添加到本群黑名单").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (MarkIllegalLengthException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
    }

    /**
     * 删除黑名单
     * @param qq        qq
     */
    @Action("\\^[.!！]黑名单删除$\\ {qq}")
    @Synonym({"\\^[.!！]删黑$\\ {qq}"})
    @MenuItem(name = "黑名单删除", usage = "[.!！]黑名单删除 {qq}", description = "移出本群黑名单", permission = Permissions.GROUP_ADMIN)
    public Message delete(DataBase dataBase, Group group, long qq){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            //判断是否已存在
            if (!blackListDao.exist(group.getId(),qq)){
                return MyYuQ.getMif().text("[" + qq + "]不存在于本群黑名单").toMessage();
            }else {
                blackListDao.delete(group.getId(),qq);
                return MyYuQ.getMif().text("[" + qq + "]已从本群黑名单移除").toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (MarkIllegalLengthException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
    }

    /**
     * 查询本群黑名单
     */
    @Action("\\^[.!！]本群黑名单$\\ {page}")
    @Synonym("\\^[.!！]黑名单$\\ {page}")
    public Message query(DataBase dataBase, Group group, int page){
        if (page < 1){
            return MyYuQ.getMif().text("页面错误：" + page).toMessage();
        }
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            ArrayList<BlackList> lists = blackListDao.query(group.getId());
            int page_max = lists.size() / page_num + (lists.size() % page_num == 0 ? 0:1);
            if (page > page_max){
                return MyYuQ.getMif().text("页面错误,最大页数[" + page_max + "]：" + page).toMessage();
            }
            ArrayList<String> msgList = new ArrayList<>();
            for (int i = (page - 1) * page_num; i < lists.size() && i < (page - 1) * page_num + page_num; i++){
                BlackList blackList = lists.get(i);
                msgList.add(Time.dateToString(new Date(blackList.getTime()),Time.DATE_FORMAT) + ":[" + blackList.getQq() + "]" + blackList.getRemarks());
            }
            return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("[黑名单列表(" + page + "/" + page_max + ")]",
                    "本群黑名单列表(" + page + "/" + page_max + ")","http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640",
                    msgList.toArray(new String[0]))).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (MarkIllegalLengthException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
    }

    /**
     * 查询本群黑名单
     */
    @Action("\\^[.!！]本群黑名单$\\")
    @Synonym("\\^[.!！]黑名单$\\")
    @MenuItem(name = "查看本群黑名单", usage = "[.!！]黑名单", description = "查看本群黑名单", permission = Permissions.GROUP_ADMIN)
    public Message query(DataBase dataBase, Group group){
        try {
            BlackListDao blackListDao = new BlackListDao(dataBase);
            ArrayList<BlackList> lists = blackListDao.query(group.getId());
            int page_max = lists.size() / page_num + (lists.size() % page_num == 0 ? 0:1);

            ArrayList<String> msgList = new ArrayList<>();
            for (int i = 0; i < lists.size() && i < page_num; i++){
                BlackList blackList = lists.get(i);
                msgList.add(Time.dateToString(new Date(blackList.getTime()),Time.DATE_FORMAT) + ":[" + blackList.getQq() + "]" + blackList.getRemarks());
            }
            return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("[黑名单列表(1/" + page_max + ")]",
                    "本群黑名单列表(1/" + page_max + ")","http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640",
                    msgList.toArray(new String[0]))).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        } catch (MarkIllegalLengthException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage();
        }
    }

    @Catch(error = IllegalModeException.class)
    public String illegalModeException(IllegalModeException e){
        SfLog.getInstance().e(this.getClass(),e);
        return "出现了一点错误：" + e.getMessage();
    }

    @Catch(error = SQLException.class)
    public String sQLException(SQLException e){
        SfLog.getInstance().e(this.getClass(),e);
        return "出现了一点错误：" + e.getMessage();
    }

    @Catch(error = ClassNotFoundException.class)
    public String classNotFoundException(ClassNotFoundException e){
        SfLog.getInstance().e(this.getClass(),e);
        return "出现了一点错误：" + e.getMessage();
    }



}
