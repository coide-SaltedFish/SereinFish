package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.Reply;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;
import java.util.ArrayList;

@GroupController
public class ReplyController extends QQController {
    private int maxTime = 25000;
    int page_num = 4;//一页的记录数
    /**
     * 权限检查
     */
    @Before
    public DataBase before(Group group, Member sender, Message message) throws IllegalModeException, SQLException, ClassNotFoundException {
        DataBase dataBase;

        GroupConf conf = GroupConfManager.getInstance().get(group.getId());
        if (!conf.isEnable()){
            throw new DoNone();
        }

        if (!AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }


        if ((Boolean) conf.getControl(GroupControlId.CheckBox_AutoReply).getValue()){
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
        return dataBase;
    }

    @Action("\\[!！.]添加问答\\")
    @Synonym({"\\[!！.]问答添加\\"})
    public Message addReply(Member sender, DataBase dataBase, Group group, ContextSession session){
        try{
            reply(MyYuQ.getMif().at(sender).plus("\n请输入问题"));
            String key = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            reply(MyYuQ.getMif().at(sender).plus("\n请输入回答"));
            String re = Message.Companion.toCodeString(session.waitNextMessage(maxTime));

            ReplyDao replyDao = new ReplyDao(dataBase);
            Reply reply = new Reply(sender.getId(),group.getId(),Reply.BOOLEAN_TRUE,Reply.BOOLEAN_FALSE,key,re);
            if (replyDao.exist(reply.getUuid())){
                return MyYuQ.getMif().at(sender).toMessage().plus("\n问答已存在");
            }else {
                replyDao.insert(reply);
                return MyYuQ.getMif().at(sender).toMessage().plus("\n添加成功");
            }
        }catch (WaitNextMessageTimeoutException e){
            return MyYuQ.getMif().text("已超时取消").toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[!！.]问答添加\\ 问{key}答{re}")
    @Synonym("\\[!！.]添加问答\\ 问{key}答{re}")
    public Message addReply(DataBase dataBase, Group group, Member sender, Message message, String key,String re){
        String msg = Message.Companion.toCodeString(message);
        String msgInfo = msg.substring(msg.indexOf(" 问"));
        key = msgInfo.substring(2,msgInfo.indexOf("答"));
        re = msgInfo.substring(msgInfo.indexOf("答") + 1);

        try{
            ReplyDao replyDao = new ReplyDao(dataBase);
            Reply reply = new Reply(sender.getId(),group.getId(),Reply.BOOLEAN_TRUE,Reply.BOOLEAN_FALSE,key,re);
            if (replyDao.exist(reply.getUuid())){
                return MyYuQ.getMif().at(sender).toMessage().plus("\n问答已存在");
            }else {
                replyDao.insert(reply);
                return MyYuQ.getMif().at(sender).toMessage().plus("\n添加成功");
            }
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[!！.]问答查询\\ {page} \"{key}\"")
    @Synonym("\\[!！.]问答查询\\ {page} {key}")
    public Message queryReply(DataBase dataBase, Group group, int page, String key){
        if (page < 1){
            return MyYuQ.getMif().text("不合法的页数：" + page).toMessage();
        }

        int page_num = 4;//一页的记录数
        try {
            ReplyDao replyDao = new ReplyDao(dataBase);
            ArrayList<Reply> replies = replyDao.query(key, group.getId());
            if (replies.size() == 0){
                return MyYuQ.getMif().text("未查找到相关记录：" + key).toMessage();
            }

            int page_max = replies.size() / page_num + (replies.size() % page_num == 0 ? 0:1);
            if (page > page_max){
                return MyYuQ.getMif().text("页数太大，最大页数：" + page_max).toMessage();
            }
            StringBuilder stringBuilder = new StringBuilder("关键词" + key + "记录查找如下（" + page + "/" + page_max + "）:\n");
            stringBuilder.append("ID\tKEY\tReply");
            for (int i = page_num * (page - 1); i < replies.size() && i < page_num * (page - 1) + page_num; i++){
                Reply reply = replies.get(i);
                stringBuilder.append("\n" + reply.getId() + "\t" + reply.getKey() + "\t" + reply.getReply());
            }
            return MyYuQ.getMif().text(stringBuilder.toString()).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[!！.]问答查询\\ \"{key}\"")
    @Synonym("\\[!！.]问答查询\\ {key}")
    public Message queryReply(DataBase dataBase, Group group, String key){
        try {
            ReplyDao replyDao = new ReplyDao(dataBase);
            ArrayList<Reply> replies = replyDao.query(key, group.getId());
            if (replies.size() == 0){
                return MyYuQ.getMif().text("未查找到相关记录：" + key).toMessage();
            }

            int page_max = replies.size() / page_num + (replies.size() % page_num == 0 ? 0:1);
            StringBuilder stringBuilder = new StringBuilder("关键词" + key + "记录查找如下（1/" + page_max + "）:\n");
            stringBuilder.append("ID\tKEY\tReply");
            for (int i = 0; i < replies.size() && i < page_num; i++){
                Reply reply = replies.get(i);
                stringBuilder.append("\n" + reply.getId() + "\t" + reply.getKey() + "\t" + reply.getReply());
            }
            return MyYuQ.getMif().text(stringBuilder.toString()).toMessage();
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        } catch (IllegalAccessException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
        }
    }

    @Action("\\[!！.]问答删除\\ {id}")
    public Message delete(DataBase dataBase, String id){
        try {
            ReplyDao replyDao = new ReplyDao(dataBase);
            if (replyDao.isExistId(id)){
                replyDao.delete(id);
               return MyYuQ.getMif().text("已删除记录：" + id).toMessage();
            }else {
               return MyYuQ.getMif().text("未找到ID：" + id).toMessage();
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
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
