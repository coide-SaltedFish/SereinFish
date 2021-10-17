package sereinfish.bot.event.group;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.EventBus;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.entity.MessageAt;
import com.icecreamqaq.yuq.error.SendMessageFailedByCancel;
import com.icecreamqaq.yuq.event.*;
import com.icecreamqaq.yuq.message.*;
import com.icecreamqaq.yuq.message.Image;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import kotlin.UninitializedPropertyAccessException;
import net.mamoe.mirai.event.EventHandler;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.entity.msg.LeavingMessage;
import sereinfish.bot.entity.msg.MyMessage;
import sereinfish.bot.entity.qingyunke.QingYunKeApi;
import sereinfish.bot.entity.qingyunke.Result;
import sereinfish.bot.entity.sereinfish.api.SereinFishSetu;
import sereinfish.bot.entity.sereinfish.api.msg.ImageItem;
import sereinfish.bot.entity.sereinfish.api.msg.re.Msg;
import sereinfish.bot.entity.sf.msg.SFMessage;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.event.myEvent.BotNameEvent;
import sereinfish.bot.event.myEvent.NoActionResponseEvent;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.event.GroupReCallMessageManager;
import sereinfish.bot.event.group.repeater.RepeaterManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 群事件监听
 */
@EventListener
public class OnGroupMessageEvent {

    @Inject
    private EventBus eventBus;

    @Event
    public void messageEvent(MessageEvent event){
        if (!MyYuQ.isEnable){
            event.setCancel(true);
        }
    }

    /**
     * 群消息记录
     * @param event
     */
    @Event
    public void groupMessageEvent(GroupMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getGroup(), event.getSender().getId(), event.getMessage())){
            SfLog.getInstance().e(this.getClass(),"消息记录失败");
            //event.getGroup().sendMessage(MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
        //群功能启用判断
        GroupConf conf = ConfManager.getInstance().get(event.getGroup().getId());
        //启用群命令
        if (!conf.isEnable()){
            if (Message.Companion.toCodeString(event.getMessage()).equals("SereinFish Bot 开")){
                //权限判断
                if (Permissions.getInstance().authorityCheck(event.getGroup(), event.getSender(), Permissions.GROUP_ADMIN)){
                    //开启群
                    conf.setEnable(true);
                    event.getGroup().sendMessage(MyYuQ.getMif().text("本群[启用]开关状态已设置为[true]").plus(MyYuQ.getMif().at(event.getSender())));
                }
            }
        }

        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }
        //RepeaterManager.getInstance().add(event.getGroup(),event.getMessage());//复读

        //留言
        LeavingMessage.checkRunnable(event.getGroup().getId(), event.getSender().getId());
        //TODO:刷屏检测
        Message message = event.getMessage();
        //图片过期检测
        try {
            for (MessageItem item:message.getBody()){
                if (item instanceof Image){
                    Image image = (Image) item;
                    String md5 = image.getId().substring(0, image.getId().lastIndexOf(".")).toUpperCase();
                    if(!MyYuQ.imageEnableTX(md5)){
                        event.getGroup().sendMessage("检测到图片已过期：" + md5);
                        //向api查询是否有图片信息
                        Msg msg = SereinFishSetu.getMd5(md5);
                        if (msg.getCode() == Msg.SUCCESS){
                            ImageItem imageItem = (ImageItem) msg.getMessage();
                            //如果有，进行发送
                            if (imageItem.isR18()){
                                //生成二维码
                                File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                                BufferedImage bufferedImage = QRCodeImage.backgroundMatrix(
                                        QRCodeImage.generateQRCodeBitMatrix(imageItem.getUrl(), 800, 800),
                                        ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                        0.0f,
                                        Color.BLACK);
                                ImageIO.write(bufferedImage, "png", imageFile);
                                Image image1 = event.getGroup().uploadImage(imageFile);
                                event.getGroup().sendMessage("原图可能是：");

                                MessageLineQ messageLineQ = new Message().lineQ();
                                messageLineQ.text("检测到R18标签，这里" + MyYuQ.getBotName() + "就不进行展示了哦");
                                //撤回设置
                                Message message1 = messageLineQ.getMessage();
                                message1.setRecallDelay((long) ConfManager.getInstance().get(event.getGroup().getId()).getSetuReCallTime() * 1000);
                                event.getGroup().sendMessage(message1);
                            }else {
                                Image image1 = event.getGroup().uploadImage(NetHandle.imageDownload(imageItem.getUrl(), md5));
                                event.getGroup().sendMessage("原图可能是：");
                                event.getGroup().sendMessage(image1);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
        }


        //单独@bot或者bot名字
        if (message.getCodeStr().trim().equals("<Rain:At:" + MyYuQ.getYuQ().getBotId() + ">")
                || message.getCodeStr().trim().equals(MyYuQ.getBotName())){
            //到bot名字事件
            BotNameEvent botNameEvent = new BotNameEvent(event);
            eventBus.post(botNameEvent);
        }


        //bot名称
        if (message.getBody().size() > 0){
            if(message.getBody().get(0) instanceof Text){
                Text text = (Text) message.getBody().get(0);
                String botName = MyYuQ.getBotName();
                if (text.getText().startsWith(MyYuQ.getBotName() + ",")){
                    botName = MyYuQ.getBotName() + ",";
                }else if (text.getText().startsWith(MyYuQ.getBotName() + "，")){
                    botName = MyYuQ.getBotName() + "，";
                }

                if (text.getText().startsWith(botName)){
                    String noNameMsg = text.getText().substring(botName.length());
                    while (noNameMsg.startsWith(" ")){
                        noNameMsg = noNameMsg.substring(1);
                    }

                    Message msgStart = new Message().lineQ()
                            .at(MyYuQ.getYuQ().getBotId())
                            .text(noNameMsg)
                            .getMessage();
                    ArrayList<MessageItem> messageItems = new ArrayList<>(msgStart.getBody());
                    for (int i = 1; i < message.getBody().size(); i++){
                        messageItems.add(message.getBody().get(i));
                    }
                    message.setBody(messageItems);

                    //path修改
                    if (message.getPath().size() > 0){
                        MessageItem pathItem = message.getPath().get(0);
                        if (pathItem instanceof Text){
                            if (text.getText().startsWith(botName)){
                                noNameMsg = text.getText().substring(botName.length());
                                while (noNameMsg.startsWith(" ")){
                                    noNameMsg = noNameMsg.substring(1);
                                }
                                noNameMsg = noNameMsg.replaceAll("[ \n\r]+", " ");

                                String[] paths = noNameMsg.split(" ");
                                message.getPath().remove(0);
                                message.getPath().add(0, new Message().lineQ().text(paths[0]).getMessage().getBody().get(0));
                            }
                        }
                    }


//                    for (MessageItem messageItem:message.getPath()){
//                        System.out.println(messageItem.toPath());
//                    }
                }
            }
        }
    }

    /**
     * bot名字事件
     * @param event
     */
    @Event
    public void botNameEvent(BotNameEvent event){
        if (MyYuQ.getRandom(1, 100) > 80){
            //戳回去
            event.getSender().click();
        }else if (MyYuQ.getRandom(1, 100) > 90){
            //不理
            return;
        }else {
            String[] tips = {"嗯？",
                    "输入[" + MyYuQ.getBotName() + " 指令]可以获取指令帮助哦",
                    "在哦",
                    "现在是：" + Time.dateToString(new Date(), "HH时mm分ss秒"),
                    "怎么了?",
                    "正在待命！",
                    "...",
                    "可以用" + MyYuQ.getBotName() + "的名字来代替指令开头的@哦",
                    "ko~ko~da~yo~"};
            event.getContact().sendMessage(tips[MyYuQ.getRandom(0, tips.length - 1)]);
        }
    }

    /**
     * 私聊消息记录
     * @param event
     */
    @Event
    public void privateMessageEvent(PrivateMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(-1, event.getSender().getId(), event.getMessage())){
            event.getSender().sendMessage(MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * bot发送消息事件
     * @param event
     */
    @Event
    public void sendMessageEvent(SendMessageEvent event){
        //是否转图片
        Contact contact = event.getSendTo();
        if (contact instanceof Group){
            Group group = (Group) contact;
            GroupConf conf = ConfManager.getInstance().get(group.getId());
            if (conf.isLongMsgToImageEnable()){
                //判断消息是否过长
                Message message = event.getMessage();
                int textLength = 0;//字数
                int imageNum = 0;
                int lineNum = 0;
                String msgText = "";

                for (MessageItem item:message.getBody()){
                    if (item instanceof Text){
                        Text text = (Text) item;
                        textLength += text.getText().length();
                        msgText += "\n" + text.getText();
                    }else if(item instanceof Image){
                        imageNum++;
                    }
                }
                lineNum = msgText.split("\n").length;

                if (textLength >= conf.getLongMsgToImageTextLengthMax()
                        || imageNum > conf.getLongMsgToImageImageNumMax()
                        || lineNum > conf.getLongMsgToImageLineNumMax()){
                    //取消发送
                    event.setCancel(true);
                    event.getSendTo().sendMessage(MyYuQ.getMif().text("消息过长，正在处理").toMessage());
                    try {
                        File imageFile = new File(FileHandle.imageCachePath,"msg_temp_" + new Date().getTime());//文件缓存路径

                        ImageIO.write(ImageHandle.messageToImage(event.getMessage(), conf), "png", imageFile);

                        event.getSendTo().sendMessage(MyYuQ.getMif().imageByFile(imageFile).toMessage());
                    }catch (UninitializedPropertyAccessException e){
                        SfLog.getInstance().e(this.getClass(), e);
                        event.getSendTo().sendMessage(MyYuQ.getMif().text("消息转图片失败，原消息发送中..").toMessage());
                        event.setCancel(false);
                    }catch (IOException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getSendTo().sendMessage(MyYuQ.getMif().text("消息转图片失败，原消息发送中..").toMessage());
                        event.setCancel(false);
                    }
                }
            }
        }
    }

    /**
     * bot发送的消息记录
     * @param event
     */
    @Event
    public void sendMessagePostEvent(SendMessageEvent.Post event){
        Message message = event.getMessage();
        message.setSource(event.getMessageSource());

        MessageSource messageSource = event.getMessageSource();
        Contact contact = event.getSendTo();

        //检查消息是否发送成功
        try {
            int id = messageSource.getId();
            SfLog.getInstance().d(this.getClass(), "消息Id：" + id);
        }catch (ArrayIndexOutOfBoundsException e){
            if (event.getMessage() instanceof MyMessage) {
                MyMessage myMessage = (MyMessage) event.getMessage();
                if (myMessage.isFlag()){
                    SfLog.getInstance().e(this.getClass(), "消息发送失败,无法发送");
                    contact.sendMessage(MyYuQ.getMif().text("消息发送失败,无法发送").toMessage());
                    return;
                }
            }

            if (contact instanceof Group){
                Group group = (Group) contact;
                GroupConf conf = ConfManager.getInstance().get(group.getId());

                group.sendMessage(MyYuQ.getMif().text("消息发送失败，转图片发送中，请稍候").toMessage());
                SfLog.getInstance().w(this.getClass(), message.sourceMessage);
                File imageFile = new File(FileHandle.imageCachePath,"msg_temp_" + new Date().getTime());//文件缓存路径
                try {
                    ImageIO.write(ImageHandle.messageToImage(message, conf), "png", imageFile);
                } catch (IOException e1) {
                    SfLog.getInstance().e(this.getClass(),e1);
                    group.sendMessage(MyYuQ.getMif().text("错误：" + e1.getMessage()).toMessage());
                }
                group.sendMessage(new MyMessage(true).lineQ().imageByFile(imageFile));
                return;
            }
        }

        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(contact.getId(), MyYuQ.getYuQ().getBotId(), message)){
            contact.sendMessage(MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
        //RepeaterManager.getInstance().add(event.getSendTo(), event.getMessage());//复读

        //撤回管理
        if (contact instanceof Group){
            Group group = (Group) contact;
            GroupReCallMessageManager.getInstance().add(group.getId(), message);
        }
    }

    /**
     * 新人进群事件
     * @param event
     */
    @Event
    public void groupMemberJoinEvent(GroupMemberJoinEvent.Join event){//群功能启用判断
        GroupConf conf = ConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        //进群提示
        if (conf.isEnable() && conf.isJoinGroupTipEnable()){
            String tip = conf.getJoinGroupTipText();
            if (!tip.trim().equals("")){
                SfLog.getInstance().d(this.getClass(), "发送入群提示，[" + event.getGroup() + " " + event.getMember() + "]Time:" + new Date().getTime() );

                SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(event.getMember(), event.getGroup());
                for (Message message:SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact,tip)){
                    event.getGroup().sendMessage(message);
                }
            }
        }

    }

    @Event
    public void g(GroupMemberJoinEvent.Invite invite){
        //TODO:进群邀请事件
    }

    /**
     * 退群事件
     * @param event
     */
    @Event
    public void groupMemberLeaveEvent(GroupMemberLeaveEvent event){
        GroupConf conf = ConfManager.getInstance().get(event.getGroup().getId());
        //群功能启用判断
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        Member member = event.getMember();
        Group group = event.getGroup();
        //退群提示
        if (conf.isEnable()){
            if (conf.isQuitGroupTipEnable()){
                String tip = conf.getQuitGroupTipText();
                if (!tip.trim().equals("")){
                    SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(event.getMember(), event.getGroup());
                    for (Message message:SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact,tip)){
                        event.getGroup().sendMessage(message);
                    }
                }else {
                    group.sendMessage(MyYuQ.getMif().text("刚刚，" + member.getNameCard() + "(" +
                            member.getName() + ")[" + member.getId() + "]离开了我们，他说过的最后一句话是：").toMessage());
                    try {
                        GroupHistoryMsg groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().queryLast(group.getId(),member.getId());

                        if (groupHistoryMsg == null){
                            group.sendMessage(MyYuQ.getMif().text("他好像还没说过话").toMessage());
                            return;
                        }else {
                            group.sendMessage(Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg()));
                        }
                    }catch (Exception e){
                        group.sendMessage(MyYuQ.getMif().text("出现了一点错误喵：" + e.getMessage()).toMessage());
                    }
                }
            }
            //退群拉黑
            if (conf.isQuitJoinBlackListEnable()){
                if (conf.isDataBaseEnable()){
                    try {
                        BlackListDao blackListDao;
                        try {
                            blackListDao = new BlackListDao(DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID()));
                        } catch (Exception e) {
                            SfLog.getInstance().e(this.getClass(),e);
                            return;
                        } catch (IllegalModeException e) {
                            SfLog.getInstance().e(this.getClass(),e);
                            return;
                        }

                        blackListDao.insert(new BlackList(new Date(),member.getId(),group.getId(),"退群自动拉黑"));
                        if (conf.isAddGroupBlackListTip()){
                            String tip = conf.getAddGroupBlackListTipText();
                            if (!tip.trim().equals("")){
                                SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(event.getMember(), event.getGroup());
                                for (Message message:SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact,tip)){
                                    event.getGroup().sendMessage(message);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        group.sendMessage(MyYuQ.getMif().text("退群拉黑失败:" + e.getMessage()).toMessage());
                        SfLog.getInstance().e(this.getClass(),e);
                    } catch (IllegalAccessException e) {
                        group.sendMessage(MyYuQ.getMif().text("退群拉黑失败:" + e.getMessage()).toMessage());
                        SfLog.getInstance().e(this.getClass(),e);
                    }
                }else {
                    group.sendMessage(MyYuQ.getMif().text("退群拉黑失败，数据库未启用").toMessage());
                }
            }
        }

    }

    /**
     * 踢人事件
     * @param event
     */
    @Event
    public void groupMemberKickEvent(GroupMemberLeaveEvent.Kick event){
        //群功能启用判断
        GroupConf conf = ConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        //踢人提示
        if (conf.isEnable() && conf.isKickGroupTipEnable()){
            String tip = conf.getKickGroupTipText();
            if (!tip.trim().equals("")){
                SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(event.getMember(), event.getGroup());
                sfMsgCodeContact.save("Operator", event.getOperator());
                for (Message message:SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact,tip)){
                    event.getGroup().sendMessage(message);
                }
            }
        }
    }

    /**
     * 申请入群事件
     * @param event
     */
    @Event
    public void groupMemberRequestEvent(GroupMemberRequestEvent event){
        //群功能启用判断
        GroupConf conf = ConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        SfLog.getInstance().d(this.getClass(),"[" + event.getQq() + "]申请加入群聊[" + event.getGroup() + "]");
        //自动同意入群
        if (conf.isEnable() && conf.isAutoAgreeJoinGroupEnable()){
            //是否验证黑名单
            if (conf.isBlackListGroupEnable()){
                //黑名单验证
                if (!conf.isDataBaseEnable()){
                    event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库未启用，自动同意入群已停止").toMessage());
                    return;
                }else {
                    try {
                        BlackListDao blackListDao = new BlackListDao(DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID()));
                        if (conf.isBlackListGroupEnable()){
                            //全局黑名单
                            if (conf.isGlobalBlackListGroupEnable()){
                                if(blackListDao.exist(event.getQq().getId())){
                                    //拒绝
                                    event.setRejectMessage("黑名单用户");
                                    event.setAccept(false);
                                    event.setCancel(true);
                                    event.getGroup().sendMessage(MyYuQ.getMif().text("[全局]黑名单用户[" + event.getQq().getName() + "](" + event.getQq().getId() +
                                            ")，尝试加入本群，已自动拒绝").toMessage());
                                }else {
                                    //同意
                                    event.setAccept(true);
                                    event.setCancel(true);
                                    SfLog.getInstance().d(this.getClass(),"已自动同意[" + event.getQq() + "]加入群聊[" + event.getGroup() + "]");
                                }
                            }else {
                                if(blackListDao.exist(event.getGroup().getId(), event.getQq().getId())){
                                    //拒绝
                                    event.setRejectMessage("黑名单用户");
                                    event.setAccept(false);
                                    event.setCancel(true);
                                    event.getGroup().sendMessage(MyYuQ.getMif().text("[群]黑名单用户[" + event.getQq().getName() + "](" + event.getQq().getId() +
                                            ")，尝试加入本群，已自动拒绝").toMessage());
                                }else {
                                    //同意
                                    event.setAccept(true);
                                    event.setCancel(true);
                                    SfLog.getInstance().d(this.getClass(),"已自动同意[" + event.getQq() + "]加入群聊[" + event.getGroup() + "]");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库错误：" + e.getMessage()).toMessage());
                    } catch (IllegalModeException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库错误：" + e.getMessage()).toMessage());
                    } catch (ClassNotFoundException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库错误：" + e.getMessage()).toMessage());
                    } catch (MarkIllegalLengthException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库错误：" + e.getMessage()).toMessage());
                    }

                }
            }else {
                //同意
                event.setAccept(true);
                event.setCancel(true);
                SfLog.getInstance().d(this.getClass(),"已自动同意[" + event.getQq() + "]加入群聊[" + event.getGroup() + "]");
            }
        }else {
            SfLog.getInstance().d(this.getClass(),"自动同意入群未开启");
        }
    }

    /**
     * 无Action响应事件
     * @param event
     */
    @Event
    public void noActionResponseEvent(NoActionResponseEvent event){
        GroupConf groupConf = event.getGroupConf();
        //自动回复
        if (groupConf.isAutoReplyEnable() && groupConf.isDataBaseEnable()){
            try {
                ReplyDao replyDao = new ReplyDao(DataBaseManager.getInstance().getDataBase(groupConf.getDataBaseConfig().getID()));
                String str = replyDao.queryKey(event.getContact().getId(),Message.Companion.toCodeString(event.getMessage()));
                if (str != null){
                    try{
                        SFMsgCodeContact sfMsgCodeContact = new SFMsgCodeContact(event.getBotActionContact());
                        for (Message message: SFMessage.getInstance().sfCodeToMessage(sfMsgCodeContact, str)){
                            event.getContact().sendMessage(message);
                        }
                        return;
                    }catch (SendMessageFailedByCancel e){
                        SfLog.getInstance().e(this.getClass(),"消息发送取消");
                    }
                    SfLog.getInstance().d(this.getClass(),"自动回复:：" + str);
                }
            }catch (SQLServerException e){
                SfLog.getInstance().e(this.getClass(), e.getMessage());
            }catch (SQLException e) {
                SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
            } catch (IllegalModeException e) {
                SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
            } catch (ClassNotFoundException e) {
                SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
            } catch (MarkIllegalLengthException e) {
                SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
            }
        }

        //判断功能是否启用
        if (groupConf != null){
            if (!groupConf.isQingYunKeApiChat()){
                return;
            }
        }

        Message message = event.getMessage();
        String strMsg = QingYunKeApi.getMsgText(message);

        //判断消息是否开头@Bot或者包含Bot名称
        At at = null;
        if (message.getBody().size() > 0){
            MessageItem firstItem = message.getBody().get(0);
            if (firstItem instanceof At){
                at = (At) firstItem;
            }
        }
        //满足聊天条件
        if ((at != null && at.getUser() == MyYuQ.getYuQ().getBotId())
                || strMsg.contains(MyYuQ.getBotName())){
            SfLog.getInstance().w(this.getClass(), "聊天Api：聊天响应");
            strMsg = strMsg.replace(MyYuQ.getBotName(), "菲菲");
            try {
                Result result = QingYunKeApi.chat(strMsg);
                if (result.getResult() != Result.SUCCESS){
                    SfLog.getInstance().e(this.getClass(), "青云客Api请求异常>>" + result.getContent() + ":" + result.getResult());
                }else {
                    SfLog.getInstance().d(this.getClass(), "聊天Api消息返回");
                    event.getContact().sendMessage(result.getMsg());
                }
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), "青云客Api请求异常", e);
            }
        }
    }

    /**
     * 路由寻路后事件
     * @param event
     */
    @Event
    public void actionContextInvokeEventPost(ActionContextInvokeEvent.Post event){
        if (event.getContext().getActionInvoker() == null){
            eventBus.post(new NoActionResponseEvent(event));
        }
    }

    @Event
    public void clickBotEvent(ClickBotEvent event){
        System.out.println(event.getClass().getTypeName());
        String msgs[] = {"唔","干嘛","rua","唉呀","哼"};
        event.getOperator().sendMessage(MyYuQ.getMif().text(msgs[MyYuQ.getRandom(0, msgs.length - 1)]).toMessage());
    }

    @Event
    public void clickEvent(ClickEvent event){
        System.out.println(event.getOperator().getName() + event.getAction() + event.getSuffix());
    }
}
