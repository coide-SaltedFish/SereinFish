package sereinfish.bot.event.group;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SendMessageFailedByCancel;
import com.icecreamqaq.yuq.event.*;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.handle.ReplyDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.event.group.repeater.RepeaterManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * 群事件监听
 */
@EventListener
public class OnGroupMessageEvent {

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
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        //启用群命令
        if (!conf.isEnable()){
            if (Message.Companion.toCodeString(event.getMessage()).equals("SereinFish Bot 开")){
                //权限判断
                if (AuthorityManagement.getInstance().authorityCheck(event.getSender(),AuthorityManagement.ADMIN)){
                    //开启群
                    conf.setEnable(true);
                    GroupConfManager.getInstance().put(conf);
                    event.getGroup().sendMessage(MyYuQ.getMif().text("本群[启用]开关状态已设置为[true]").plus(MyYuQ.getMif().at(event.getSender())));
                }
            }
        }

        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }else {
            RepeaterManager.getInstance().add(event.getGroup().getId(),event.getMessage());
            //自动回复
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_AutoReply).getValue() && conf.isDataBaseEnable()){
                try {
                    ReplyDao replyDao = new ReplyDao(DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID()));
                    String str = replyDao.queryKey(event.getGroup().getId(),Message.Companion.toCodeString(event.getMessage()));
                    if (str != null){
                        try{
                            event.getGroup().sendMessage(Message.Companion.toMessageByRainCode(str));
                        }catch (SendMessageFailedByCancel e){
                            SfLog.getInstance().e(this.getClass(),"消息发送取消");
                        }
                        SfLog.getInstance().d(this.getClass(),"自动回复:：" + str);
                    }
                } catch (SQLException e) {
                    SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
                } catch (IllegalModeException e) {
                    SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
                } catch (ClassNotFoundException e) {
                    SfLog.getInstance().e(this.getClass(),"自动回复失败：",e);
                }
            }
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
            GroupConf conf = GroupConfManager.getInstance().get(group.getId());
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_LongMsgToImageEnable).getValue()){
                //判断消息是否过长
                int length = 0;
                Message message = event.getMessage();
                for (MessageItem item:message.getBody()){
                    if (item instanceof Text){
                        Text text = (Text) item;
                        length += text.getText().length();
                    }
                }
                if (length >= 350){
                    //取消发送
                    event.setCancel(true);
                    event.getSendTo().sendMessage(MyYuQ.getMif().text("消息过长，正在处理").toMessage());
                    File imageFile = new File(FileHandle.imageCachePath,"msg_temp_" + new Date().getTime());//文件缓存路径
                    try {
                        ImageIO.write(ImageHandle.messageToImage(event.getMessage(), conf), "png", imageFile);
                    } catch (IOException e) {
                        SfLog.getInstance().e(this.getClass(),e);
                        event.getSendTo().sendMessage(MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
                    }
                    event.getSendTo().sendMessage(MyYuQ.getMif().imageByFile(imageFile).toMessage());
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
        //检查消息是否发送成功
        if (event.getMessageSource().getId() < 0){
            Contact contact = event.getSendTo();
            if (contact instanceof Group){
                Group group = (Group) contact;
                GroupConf conf = GroupConfManager.getInstance().get(group.getId());

                event.getSendTo().sendMessage(MyYuQ.getMif().text("消息发送失败，转图片发送中，请稍候").toMessage());
                File imageFile = new File(FileHandle.imageCachePath,"msg_temp_" + new Date().getTime());//文件缓存路径
                try {
                    ImageIO.write(ImageHandle.messageToImage(event.getMessage(), conf), "png", imageFile);
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                    event.getSendTo().sendMessage(MyYuQ.getMif().text("错误：" + e.getMessage()).toMessage());
                }
                event.getSendTo().sendMessage(MyYuQ.getMif().imageByFile(imageFile).toMessage());
                return;
            }
        }

        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getSendTo().getId(), MyYuQ.getYuQ().getBotId(), event.getMessage())){
            event.getSendTo().sendMessage(MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
        RepeaterManager.getInstance().add(event.getSendTo().getId(),message);
    }

    /**
     * 新人进群事件
     * @param event
     */
    @Event
    public void groupMemberJoinEvent(GroupMemberJoinEvent.Join event){//群功能启用判断
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        //进群提示
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_JoinGroupTip).getValue()){
            String tip = (String) conf.getControl(GroupControlId.Edit_JoinGroupTip).getValue();
            if (!tip.trim().equals("")){
                SfLog.getInstance().d(this.getClass(), "发送入群提示，[" + event.getGroup() + " " + event.getMember() + "]Time:" + new Date().getTime() );
                event.getGroup().sendMessage(Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,event.getMember(),null,event.getGroup())));
                return;
            }
        }

    }

    @Event
    public void g(GroupMemberJoinEvent.Invite invite){
        //TODO:进群邀请事件
        System.out.println(invite.getInviter().getId());
        System.out.println(invite.getMember().getId());
    }

    /**
     * 退群事件
     * @param event
     */
    @Event
    public void groupMemberLeaveEvent(GroupMemberLeaveEvent event){
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        //群功能启用判断
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        Member member = event.getMember();
        Group group = event.getGroup();
        //退群提示
        if (conf.isEnable()){
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_QuitGroupTip).getValue()){
                String tip = (String) conf.getControl(GroupControlId.Edit_QuitGroupTip).getValue();
                if (!tip.trim().equals("")){
                    group.sendMessage(Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,member,null,group)));
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
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_QuitJoinBlackList).getValue()){
                if (conf.isDataBaseEnable()){
                    try {
                        BlackListDao blackListDao = new BlackListDao(conf.getDataBase());
                        blackListDao.insert(new BlackList(new Date(),member.getId(),group.getId(),"退群自动拉黑"));
                        if ((Boolean) conf.getControl(GroupControlId.CheckBox_AddBlackTip).getValue()){
                            String tip = (String) conf.getControl(GroupControlId.Edit_AddBlackTip).getValue();
                            if (!tip.trim().equals("")){
                                group.sendMessage(Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,member,null,group)));
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
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        //踢人提示
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_KickTip).getValue()){
            String tip = (String) conf.getControl(GroupControlId.Edit_KickTip).getValue();
            if (!tip.trim().equals("")){
                event.getGroup().sendMessage(Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,event.getMember(),null,event.getGroup())));
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
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
            return;
        }

        SfLog.getInstance().d(this.getClass(),"[" + event.getQq() + "]申请加入群聊[" + event.getGroup() + "]");
        //自动同意入群
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_AutoAgreeJoinGroup).getValue()){
            //是否验证黑名单
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_BlackList).getValue()){
                //黑名单验证
                if (!conf.isDataBaseEnable()){
                    event.getGroup().sendMessage(MyYuQ.getMif().text("黑名单数据库未启用，自动同意入群已停止").toMessage());
                    return;
                }else {
                    try {
                        BlackListDao blackListDao = new BlackListDao(DataBaseManager.getInstance().getDataBase(conf.getDataBaseConfig().getID()));
                        if ((Boolean) conf.getControl(GroupControlId.CheckBox_BlackList).getValue()){
                            //全局黑名单
                            if ((Boolean) conf.getControl(GroupControlId.CheckBox_GlobalBlackList).getValue()){
                                if(blackListDao.exist(event.getQq().getId())){
                                    //拒绝
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
                                return;
                            }else {
                                if(blackListDao.exist(event.getGroup().getId(), event.getQq().getId())){
                                    //拒绝
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
                                return;
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
                    }

                }
            }else {
                //同意
                event.setAccept(true);
                event.setCancel(true);
                SfLog.getInstance().d(this.getClass(),"已自动同意[" + event.getQq() + "]加入群聊[" + event.getGroup() + "]");
                return;
            }
        }else {
            SfLog.getInstance().d(this.getClass(),"自动同意入群未开启");
        }
    }

    @Event
    public void clickBotEvent(ClickBotEvent event){
        String msgs[] = {"唔","干嘛","rua","唉呀","哼"};
        event.getOperator().sendMessage(MyYuQ.getMif().text(msgs[MyYuQ.getRandom(0, msgs.length - 1)]).toMessage());
    }
}
