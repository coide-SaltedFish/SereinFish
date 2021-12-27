package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import sereinfish.bot.database.entity.GroupHistoryMsg;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.entity.ascii2d.Ascii2d;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.CallBack;

import javax.inject.Inject;
import java.io.IOException;

@GroupController
@Menu(name = "Ascii2d")
public class Ascii2dController {
    @Inject
    GroupHistoryMsgService groupHistoryMsgService;

    @Action("ascii2d图色")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "ascii2d图色", usage = "@Bot ascii2d图色")
    public void ascii2dColor(Message message, Group group, @PathVar(value = 2, type = PathVar.Type.Source) Image image) throws IOException {
        if (image == null && message.getReply() != null){
            Message reMsg = Message.Companion.toMessageByRainCode(groupHistoryMsgService.findByGroupAndMid(group.getId(), message.getReply().getId()).getRainCodeMsg());
            //找回复
            for (MessageItem item: reMsg.getBody()){
                if (item instanceof Image){
                    image = (Image) item;
                    break;
                }
            }
        }

        if (image == null){
            group.sendMessage(new Message().lineQ().text("未发现图片").getMessage());
            return;
        }
        group.sendMessage("正在搜索~");
        new Ascii2d(image.getUrl()).getColorResponse().getInfo(group, new CallBack<Message>() {
            @Override
            public void callback(Message p) {
                group.sendMessage(p);
            }
        });
    }

    @Action("ascii2d特征")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "ascii2d特征", usage = "@Bot ascii2d特征")
    public void ascii2dBovw(Message message, Group group, @PathVar(value = 2, type = PathVar.Type.Source) Image image) throws IOException {
        if (image == null && message.getReply() != null){
            GroupHistoryMsg historyMsg = groupHistoryMsgService.findByGroupAndMid(group.getId(), message.getReply().getId());

            if (historyMsg == null){
                group.sendMessage(new Message().lineQ().text("消息未被记录或消息不存在：" + message.getReply().getId()).getMessage());
                return;
            }

            Message reMsg = Message.Companion.toMessageByRainCode(historyMsg.getRainCodeMsg());
            //找回复
            for (MessageItem item: reMsg.getBody()){
                if (item instanceof Image){
                    image = (Image) item;
                    break;
                }
            }
        }

        if (image == null){
            group.sendMessage(new Message().lineQ().text("未发现图片").getMessage());
            return;
        }
        group.sendMessage("正在搜索~");
        new Ascii2d(MyYuQ.getImageUrlId(image.getId())).getBovwResponse().getInfo(group, new CallBack<Message>() {
            @Override
            public void callback(Message p) {
                group.sendMessage(p);
            }
        });
    }
}
