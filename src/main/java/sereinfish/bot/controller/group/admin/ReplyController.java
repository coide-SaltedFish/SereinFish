package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.Reply;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;
import java.util.ArrayList;

@GroupController
public class ReplyController extends QQController {
    private Group group;
    private Member sender;
    private Message message;
    private DataBase dataBase;
    private GroupConf conf;

    private int maxTime = 25000;
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
    }

    @Action("\\[!！.]添加问答\\")
    @Synonym({"\\[!！.]问答添加\\"})
    public void addReply(ContextSession session){
        try{
            reply(MyYuQ.getMif().at(sender).plus("\n请输入问题"));
            String key = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            reply(MyYuQ.getMif().at(sender).plus("\n请输入回答"));
            String re = Message.Companion.toCodeString(session.waitNextMessage(maxTime));

            ReplyDao replyDao = new ReplyDao(dataBase);
            Reply reply = new Reply(sender.getId(),group.getId(),Reply.BOOLEAN_TRUE,Reply.BOOLEAN_FALSE,key,re);
            if (replyDao.exist(reply.getUuid())){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().at(sender).toMessage().plus("\n问答已存在"));
            }else {
                replyDao.insert(reply);
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().at(sender).toMessage().plus("\n添加成功"));
            }
        }catch (WaitNextMessageTimeoutException e){
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("已超时取消").toMessage());
        } catch (IllegalAccessException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    @Action("\\[!！.]问答查询\\ {page} \"{key}\"")
    public void queryReply(int page, String key){
        if (page < 1){
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("不合法的页数：" + page).toMessage());
            return;
        }

        int page_num = 4;//一页的记录数
        try {
            ReplyDao replyDao = new ReplyDao(dataBase);
            ArrayList<Reply> replies = replyDao.query(key, group.getId());
            if (replies.size() == 0){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("未查找到相关记录：" + key).toMessage());
                return;
            }

            int page_max = replies.size() / page_num + (replies.size() % page_num == 0 ? 0:1);
            if (page > page_max){
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("页数太大，最大页数：" + page_max).toMessage());
                return;
            }
            StringBuilder stringBuilder = new StringBuilder("关键词" + key + "记录查找如下（" + page + "/" + page_max + "）:\n");
            stringBuilder.append("ID\tKEY\tReply");
            for (int i = page_num * (page - 1); i < replies.size() && i < page_num * (page - 1) + page_num; i++){
                Reply reply = replies.get(i);
                stringBuilder.append("\n" + reply.getId() + "\t" + reply.getKey() + "\t" + reply.getReply());
            }
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text(stringBuilder.toString()).toMessage());
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalAccessException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    @Action("\\[!！.]问答删除\\ {id}")
    public void delete(String id){
        try {
            ReplyDao replyDao = new ReplyDao(dataBase);
            if (replyDao.isExistId(id)){
                replyDao.delete(id);
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("已删除记录：" + id).toMessage());
            }else {
                MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("未找到ID：" + id).toMessage());
            }
        } catch (SQLException e) {
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }
}
