package sereinfish.bot.entity.qingyunke;

import com.icecreamqaq.yuq.message.At;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 青云客聊天api
 */
public class QingYunKeApi {

    /**
     * 聊天
     * @param msg
     * @return
     */
    public static Result chat(String msg) throws IOException {
        Request request = new Request(msg);
        String json = OkHttpUtils.getStr(request.getUrl());
        return MyYuQ.toClass(json, Result.class);
    }

    /**
     * 处理用于聊天api的消息文本
     * @return
     */
    public static String getMsgText(Message message){
        String msg = "";
        int i = 0;
        for (MessageItem item:message.getBody()){
            if (item instanceof Text){
                Text text = (Text) item;
                msg += text.getText();
            }

            if (item instanceof At && i != 0){
                At at = (At) item;
                if (at.getUser() == MyYuQ.getYuQ().getBotId()){
                    msg += MyYuQ.getBotName();
                }
            }
            i++;
        }
        return msg;
    }
}
