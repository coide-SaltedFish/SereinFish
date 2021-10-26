package sereinfish.bot.entity.msg;

import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.Reply;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.str.MyString;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.ArrayList;
import java.util.Map;

public class ReplyManager {

    public static ArrayList<SFMessage.SFMessageEntity> reply(GroupConf groupConf, BotActionContext botActionContext){
        try {
            ReplyDao replyDao = new ReplyDao(DataBaseManager.getInstance().getDataBase(groupConf.getDataBaseConfig().getID()));
            ArrayList<Reply> replies = replyDao.query(botActionContext.getSource().getId());
            ArrayList<Reply> replyList = new ArrayList<>();

            String msgStr = botActionContext.getMessage().getCodeStr();
            //开始匹配
            for (Reply reply:replies){

                String eqStr = reply.getKey().replaceAll("<SF:Value:.+?>", ".+?");//匹配用字段
                if(msgStr.matches(eqStr)){
                    try {
                        MyString.placeholderExtraction(reply.getKey(), msgStr);
                        replyList.add(reply);
                    }catch (Exception e){
                        SfLog.getInstance().w(ReplyManager.class, "匹配失败：" + reply.getKey());
                        continue;
                    }
                }
            }

            if (replyList.size() > 0){
                Reply reply = replyList.get(MyYuQ.getRandom(0, replyList.size() - 1));//回复的消息文本
                SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(botActionContext);
                //参数注入
                for (Map.Entry<String, Object> entry:MyString.placeholderExtraction(reply.getKey(), msgStr).entrySet()){
                    sfMsgCodeContact.save(entry.getKey(), entry.getValue());
                }

                return SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact, reply.getReply());
            }
        }catch (Exception e){
            SfLog.getInstance().e(ReplyManager.class,"自动回复失败：",e);
        } catch (Throwable e) {
            SfLog.getInstance().e(ReplyManager.class,"自动回复失败：",e);
        }

        return null;
    }
}
