package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.service.ReplyService;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.database.entity.Reply;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.inject.Inject;
import java.util.List;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "问答", permissions = Permissions.GROUP_ADMIN)
public class ReplyController extends QQController {
    @Inject
    private ReplyService replyService;

    private int maxTime = 25000;
    int page_num = 4;//一页的记录数
    /**
     * 权限检查
     */
    @Before
    public void before(Group group, GroupConf groupConf, Member sender, Message message){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }


        if (!groupConf.isAutoReplyEnable()){
            Message msg = MyYuQ.getMif().text("功能未启用").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("添加问答")
    @Synonym({"问答添加"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "添加问答", usage = "@Bot 添加问答", description = "为Bot添加精确问答", permission = Permissions.GROUP_ADMIN)
    public Message addReply(Member sender, Group group, ContextSession session){
        try{
            reply(MyYuQ.getMif().at(sender).plus("\n请输入问题"));
            String key = Message.Companion.toCodeString(session.waitNextMessage(maxTime));
            reply(MyYuQ.getMif().at(sender).plus("\n请输入回答"));
            String re = Message.Companion.toCodeString(session.waitNextMessage(maxTime));

            Reply reply = new Reply(sender.getId(), group.getId(), key, re);
            if (replyService.exist(reply.getUuid())){
                return MyYuQ.getMif().at(sender).toMessage().plus("\n问答已存在");
            }else {
                replyService.save(reply);
                return MyYuQ.getMif().at(sender).toMessage().plus("\n添加成功");
            }
        }catch (WaitNextMessageTimeoutException e){
            return MyYuQ.getMif().text("已超时取消").toMessage();
        }
    }

//    @Action("\\^[!！.]问答添加$\\ 问{key}答{re}")
//    @Synonym("\\^[!！.]添加问答$\\ 问{key}答{re}")
//    public Message addReply(DataBase dataBase, Group group, Member sender, Message message, String key,String re){
//        String msg = Message.Companion.toCodeString(message);
//        String msgInfo = msg.substring(msg.indexOf(" 问"));
//        key = msgInfo.substring(2,msgInfo.indexOf("答"));
//        re = msgInfo.substring(msgInfo.indexOf("答") + 1);
//
//        try{
//            ReplyDao replyDao = new ReplyDao(dataBase);
//            Reply reply = new Reply(sender.getId(),group.getId(),Reply.BOOLEAN_TRUE,Reply.BOOLEAN_FALSE,key,re);
//            if (replyDao.exist(reply.getUuid())){
//                return MyYuQ.getMif().at(sender).toMessage().plus("\n问答已存在");
//            }else {
//                replyDao.insert(reply);
//                return MyYuQ.getMif().at(sender).toMessage().plus("\n添加成功");
//            }
//        } catch (IllegalAccessException e) {
//            SfLog.getInstance().e(this.getClass(),e);
//            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
//        } catch (SQLException e) {
//            SfLog.getInstance().e(this.getClass(),e);
//            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
//        } catch (MarkIllegalLengthException e) {
//            SfLog.getInstance().e(this.getClass(),e);
//            return MyYuQ.getMif().text("失败：" + e.getMessage()).toMessage();
//        }
//    }

    @Action("\\^[!！.]问答查询$\\ {key}")
    @Synonym("\\^[!！.]问答查询$\\ {key}")
    public Message queryReply(Group group, @PathVar(2) String pageStr, String key){
        int page = 1;

        try{
            page = Integer.decode(pageStr);
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), "页数转换失败：" + pageStr);
        }

        if (page < 1){
            return MyYuQ.getMif().text("不合法的页数：" + page).toMessage();
        }

        int page_num = 4;//一页的记录数
        List<Reply> replies = replyService.findByKeyAndSource(key, group.getId());
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
            stringBuilder.append("\n" + reply.getId() + "\t" + reply.getReKey() + "\t" + reply.getReply());
        }
        return MyYuQ.getMif().text(stringBuilder.toString()).toMessage();
    }

    @Action("问答删除 {id}")
    @MenuItem(name = "问答删除", usage = "@Bot 问答删除 {id}", description = "删除指定问题", permission = Permissions.GROUP_ADMIN)
    @QMsg(mastAtBot = true)
    public Message delete(int id){
        if (replyService.get(id) != null){
            replyService.delete(id);
            return MyYuQ.getMif().text("已删除记录：" + id).toMessage();
        }else {
            return MyYuQ.getMif().text("未找到ID：" + id).toMessage();
        }
    }
}
