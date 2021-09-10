package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.bili.live.BiliLiveManager;
import sereinfish.bot.entity.bili.live.entity.info.UserInfo;
import sereinfish.bot.entity.bili.live.entity.live.LiveRoom;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.permissions.Permissions;

import java.io.IOException;
import java.util.Date;

@GroupController
public class TestController {

    /**
     * 权限检查
     */
    @Before
    public void before(Group group, Member sender, Message message){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("这是一个测试命令， 你没有权限使用这个命令").toMessage();

            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    @Action("获取B站用户信息 {uid}")
    @QMsg(mastAtBot = true)
    public Message getBiliUserInfo(Group group, long uid) throws IOException {
        group.sendMessage(MyYuQ.getBotName() + ">" + Time.dateToString(new Date(), Time.RUN_TIME) + ">>命令响应");

        UserInfo userInfo = BiliLiveManager.getUserInfo(uid);
        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.imageByUrl(userInfo.getData().getFace());
        messageLineQ.textLine("名称：" + userInfo.getData().getName());
        messageLineQ.textLine("性别：" + userInfo.getData().getSex());
        messageLineQ.textLine("简介：" + userInfo.getData().getSign());
        messageLineQ.textLine("直播间状态：");
        LiveRoom liveRoom = userInfo.getData().getLive_room();
        if (liveRoom.getRoomStatus() == LiveRoom.ROOM_STATUS_OPEN){
            if (liveRoom.getLiveStatus() == LiveRoom.LIVE_STATUS_OPEN){
                messageLineQ.textLine("正在直播");
            }else if (liveRoom.getRoundStatus() == LiveRoom.ROUND_STATUS_OPEN){
                messageLineQ.textLine("正在轮播");
            }else {
                messageLineQ.textLine("还没有开始直播");
            }
            messageLineQ.imageByUrl(liveRoom.getCover());
            messageLineQ.textLine("直播间标题：" + liveRoom.getTitle());
            messageLineQ.textLine("直播间ID:" + liveRoom.getRoomid());
            messageLineQ.text("直播间地址:" + liveRoom.getUrl());
        }else {
            messageLineQ.text("直播间还未开启");
        }
        return messageLineQ.getMessage();
    }

    @Action("戳 {sb}")
    @QMsg(mastAtBot = true)
    public void clickMe(Member sb){
        SfLog.getInstance().d(this.getClass(), "戳：" + sb.getName());
        sb.click();
    }


    @Catch(error = IOException.class)
    public String iOException(IOException e){
        return "出现错误：" + e.getMessage();
    }
}
