package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.entity.User;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.bili.live.BiliManager;
import sereinfish.bot.entity.bili.live.entity.dynamic.DynamicCard;
import sereinfish.bot.entity.bili.live.entity.info.UserInfo;
import sereinfish.bot.entity.bili.live.entity.live.LiveRoom;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.image.gif.GifDecoder;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.permissions.Permissions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@GroupController
public class TestController extends QQController {
    private int maxTime = 25 * 1000;

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

        UserInfo userInfo = BiliManager.getUserInfo(uid);
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

    @Action("获取UP最新动态 {mid}")
    @QMsg(mastAtBot = true)
    public String getDynamic(long mid){
        try {
            DynamicCard dynamicCard = BiliManager.getUserDynamic(mid).getCard(0).getDynamicCard();
            if (dynamicCard != null){
                if (dynamicCard.getItem().getContent() != null && !dynamicCard.getItem().getContent().equals("")){
                    return dynamicCard.getUser().getUname() + ":转发动态";
                }else {
                    return dynamicCard.getUser().getName() + ":" + dynamicCard.getItem().getDescription();
                }
            }
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
        }
        throw new DoNone();
    }

    @Action("戳 {sb}")
    @QMsg(mastAtBot = true)
    public void clickMe(Member sb){
        SfLog.getInstance().d(this.getClass(), "戳：" + sb.getName());
        sb.click();
    }

    @Action("设置提醒消息 {ms}")
    @QMsg(mastAtBot = true)
    public String addJobMs(Group group, Member sender, int ms, ContextSession session){
        reply(new Message().lineQ().at(sender).textLine("").text("请输入要提醒的消息(" + (maxTime / 1000) + ")").getMessage());

        try{
            Message m1 = session.waitNextMessage(maxTime);
            MyYuQ.getJobManager().registerTimer(new Runnable() {
                @Override
                public void run() {
                    group.sendMessage(new Message().lineQ().at(sender).textLine("").text("是定时提醒哦:"));
                    group.sendMessage(m1);
                }
            }, ms);
            return "已设置提醒，执行时间：\n"
                    + Time.dateToString(new Date().getTime() + ms, Time.DAY_TIME);
        }catch (WaitNextMessageTimeoutException e){
            SfLog.getInstance().e(this.getClass(),e);
            return "超时：" + maxTime;
        }
    }

    @Action("设置定时消息 {atTime}")
    @QMsg(mastAtBot = true)
    public String addJobAtTime(Group group, Member sender, String atTime, ContextSession session){
        reply(new Message().lineQ().at(sender).textLine("").text("请输入提醒消息(" + (maxTime / 1000) + ")").getMessage());

        try{
            Message m1 = session.waitNextMessage(maxTime);
            MyYuQ.getJobManager().registerTimer(new Runnable() {
                @Override
                public void run() {
                    group.sendMessage(new Message().lineQ().text("是定时提醒哦:"));
                    group.sendMessage(m1);
                }
            }, atTime);
            return "已设置提醒";
        }catch (WaitNextMessageTimeoutException e){
            SfLog.getInstance().e(this.getClass(),e);
            return "超时：" + maxTime;
        }
    }

    @Action("假期三连抽")
    @Synonym("假期3连抽")
    @QMsg(mastAtBot = true, reply = true)
    public Message holidaySmoking() throws IOException {
        File imageFile = new File(FileHandle.imageCachePath, "/random_holidaySmoking_" + new Date().getTime());
        GifDecoder decoder = new GifDecoder();
        InputStream inputStream = getClass().getResourceAsStream("/image/holiday_smoking.gif");
        if (inputStream == null){
            SfLog.getInstance().e(this.getClass(), "资源文件丢失：/image/holiday_smoking.gif" );
            throw new DoNone();
        }
        int state = decoder.read(inputStream);
        if (state != GifDecoder.STATUS_OK) {
            return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误:" + state);
        }

        BufferedImage bufferedImage = decoder.getFrame(MyYuQ.getRandom(0, decoder.getFrames().size() - 1));

        ImageIO.write(bufferedImage, "png", imageFile);
        return new Message().lineQ().imageByFile(imageFile).getMessage();
    }

    @Action("qr {ver} {str}")
    @QMsg(mastAtBot = true)
    public Message qrcode(int ver, String str){
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.QR_VERSION, ver);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 500, 500, hints);

            BufferedImage bufferedImage =  MatrixToImageWriter.toBufferedImage(bitMatrix);
            File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
            ImageIO.write(bufferedImage, "PNG", imageFile);
            return new Message().lineQ().imageByFile(imageFile).getMessage();

        } catch (WriterException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message().lineQ().text(e.getMessage()).getMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message().lineQ().text(e.getMessage()).getMessage();
        }
    }

    @Action("晚安")
    @QMsg(mastAtBot = true, reply = true)
    public String goodNight(Group group, Member sender){
        try {
            //判断时间
            Date startTime = new SimpleDateFormat(Time.DAY_TIME).parse("23:00:00");
            Date endTime = new Date(startTime.getTime() + 60 * 60 * 8 * 1000);

            if (Time.isEffectiveDate(new Date(), startTime, endTime)){
                long banTime = endTime.getTime() - new Date().getTime();

                if (group.getBot().isAdmin() || group.getBot().isOwner()){
                    group.sendMessage("晚安哦~");
                    if (!sender.isOwner() && !sender.isAdmin()){
                        sender.ban((int) banTime);
                    }
                    return "要保持充足的睡眠哦";
                }else {
                    return "晚安";
                }
            }else {
                return "这个功能在晚上11点到第二天早上7点才启用哦";
            }
        } catch (ParseException e) {
            return "出现了一点错误：" + e.getMessage();
        }
    }

    @Action("判断消息相等")
    @QMsg(mastAtBot = true, reply = true)
    public String messageEqu(ContextSession session ){
        try {
            reply("输入第一条消息(" + (maxTime / 1000) + "s)");
            Message msg1 = session.waitNextMessage(maxTime);
            reply("输入第二条消息(" + (maxTime / 1000) + "s)");
            Message msg2 = session.waitNextMessage(maxTime);

            return "结果：" + msg1.bodyEquals(msg2);
        }catch (WaitNextMessageTimeoutException e){
            return "已超时取消";
        }

    }

    @Action("喜报 {text}")
    @QMsg(mastAtBot = true)
    public Message xibao(Group group, String text){
        group.sendMessage("正在加载");
        try {
            File file = new File(FileHandle.imageCachePath, "xibao_" + System.currentTimeMillis());
            BufferedImage bufferedImage = ImageHandle.getXiBao(text, new Font("黑体", Font.BOLD, 168));
            ImageIO.write(bufferedImage, "jpg", file);
            Image image = group.uploadImage(file);
            return new Message().lineQ().plus(image).getMessage();
        } catch (IOException e) {
            return new Message().lineQ().text("错误：" + e.getMessage()).getMessage();
        }
    }

    @Action("抛个异常 {text}")
    @QMsg(mastAtBot = true)
    public void thrEx(String text){
        throw new NullPointerException(text);
    }

    @Catch(error = IOException.class)
    public String iOException(IOException e){
        return "出现错误：" + e.getMessage();
    }
}
