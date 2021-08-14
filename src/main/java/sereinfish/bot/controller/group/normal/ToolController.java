package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

@GroupController
@Menu(name = "骰子")
public class ToolController {
    @Action("\\[.。!！][Rr][Dd]$\\")
    @MenuItem(name = "随机一个数", usage = "[.。!！][Rr][Dd] {text/num} {num} (参数可选)", description = "指定或不指定（默认100）随机一个范围内数")
    public Message roll(Message message){
        int num = 100;

        String reText = "";

        String msgText = message.getCodeStr();
        String msgs[] = msgText.split(" ");

        if (msgs.length > 1){
            try {
                num = Integer.valueOf(msgs[1]);
                if (num <= 0){
                    num = 100;
                }
                if (msgs.length > 2){
                    String flag = msgs[2];
                    reText = "随机[" + flag + "]得到：" + MyYuQ.getRandom(0, num);
                }else {
                    reText = MyYuQ.getRandom(0, num) + "";
                }
            }catch (Exception e){
                String flag = msgs[1];
                reText = "随机[" + flag + "]得到：" + MyYuQ.getRandom(0, num);
            }
        }else {
            reText = MyYuQ.getRandom(0, num) + "";
        }

        Message reMsg = MyYuQ.getMif().text(reText).toMessage();
        reMsg.setReply(message.getSource());
        return reMsg;
    }
}
