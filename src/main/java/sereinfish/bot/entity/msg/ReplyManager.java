package sereinfish.bot.entity.msg;

import com.icecreamqaq.yuq.controller.BotActionContext;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.entity.Reply;
import sereinfish.bot.database.service.ReplyService;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.str.MyString;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplyManager {

    public static ArrayList<SFMessage.SFMessageEntity> reply(BotActionContext botActionContext){
        ReplyService replyService = MyYuQ.getReplyService();
        List<Reply> replies = replyService.findBySource(botActionContext.getSource().getId());
        ArrayList<Reply> replyList = new ArrayList<>();

        String msgStr = botActionContext.getMessage().getCodeStr();
        //开始匹配
        for (Reply reply:replies){
            //普通正则
            if(reply.getReKey().startsWith("\\\\")){
                if(msgStr.matches(reply.getReKey().substring(2))){
                    replyList.add(reply);
                }
            }else {
                if(msgStr.equals(reply.getReKey())){
                    replyList.add(reply);
                }

                //变量输入
                String eqStr = MyYuQ.makeQueryStringAllRegExp(reply.getReKey()).replaceAll("<SF:Value:.+?>", ".+?");//匹配用字段
                if(msgStr.matches(eqStr)){
                    try {
                        MyString.placeholderExtraction(reply.getReKey(), msgStr);
                        replyList.add(reply);
                    }catch (Exception e){
                        SfLog.getInstance().w(ReplyManager.class, "匹配失败：" + reply.getReKey());
                        continue;
                    }
                }
            }
        }

        if (replyList.size() > 0){
            Reply reply = replyList.get(MyYuQ.getRandom(0, replyList.size() - 1));//回复的消息文本

            SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(botActionContext);
            //参数注入
            for (Map.Entry<String, Object> entry:MyString.placeholderExtraction(reply.getReKey(), msgStr).entrySet()){
                sfMsgCodeContact.save(entry.getKey(), entry.getValue());
            }

            return SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact, reply.getReply());
        }
        return null;
    }
}
