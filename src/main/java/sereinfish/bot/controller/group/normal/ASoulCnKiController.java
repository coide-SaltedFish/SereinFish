package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import okhttp3.*;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.aSoul.ASoulCnKi;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.sql.SQLException;

import static sereinfish.bot.utils.OkHttpUtil.JSON;

/**
 * 枝网查重
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "枝网")
public class ASoulCnKiController {

    @Action("\\[.!！]枝网查重$\\")
    @MenuItem(name = "枝网查重", usage = "[ReplyMsg][.!！]枝网查重", description = "查询小作文重复率捏")
    public String check(Group group, Message message){
        if(message.getReply() == null){
            return "请以回复小作文的形式调用此命令捏";
        }

        GroupHistoryMsg groupHistoryMsg = null;
        try {
            groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(), message.getReply().getId());
            if (groupHistoryMsg == null){
                return "找不到该消息捏，请以回复小作文的形式调用此命令捏";
            }
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return "发生错误了捏：" + e.getMessage();
        }
        String text = groupHistoryMsg.getMsg();

        if (text.length() < 11){
            return "这篇小作文太短了捏";
        }else if (text.length() > 1000){
            return "太长了捏，不看了捏";
        }

        group.sendMessage(MyYuQ.getMif().text("正在检查这篇小作文捏，请稍等捏").toMessage());

        String api = "https://asoulcnki.asia/v1/api/check";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON , MyYuQ.toJson(new ASoulCnKi.ASoulRequest(text), ASoulCnKi.ASoulRequest.class));

        Request request = new Request.Builder()
                .url(api)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SfLog.getInstance().e(this.getClass(), e);
                group.sendMessage(MyYuQ.getMif().text("发生错误了捏：" + e.getMessage()).toMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                ASoulCnKi.ASoulCnKiResult aSoulCnKiResult = MyYuQ.toClass(string, ASoulCnKi.ASoulCnKiResult.class);
                group.sendMessage(MyYuQ.getMif().text(ASoulCnKi.getReport(aSoulCnKiResult)).toMessage());
            }
        });

        throw new DoNone();
    }
}
