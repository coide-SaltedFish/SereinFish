package sereinfish.bot.event.group;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.*;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.handle.BlackListDao;
import sereinfish.bot.database.table.BlackList;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.SQLException;
import java.util.Date;

/**
 * 群事件监听
 */
@EventListener
public class OnGroupMessageEvent {

    /**
     * 群消息记录
     * @param event
     */
    @Event
    public void groupMessageEvent(GroupMessageEvent event){
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getGroup(), event.getSender().getId(), event.getMessage())){
            MyYuQ.sendGroupMessage(event.getGroup(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
        //群功能启用判断
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        if (!conf.isEnable()){
            event.setCancel(true);
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
            MyYuQ.sendMessage(event.getSender(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * bot发送的消息记录
     * @param event
     */
    @Event
    public void sendMessageEvent(SendMessageEvent.Post event){
        Message message = event.getMessage();
        message.setSource(event.getMessageSource());
        //消息记录
        if(!GroupHistoryMsgDBManager.getInstance().add(event.getSendTo().getId(), MyYuQ.getYuQ().getBotId(), event.getMessage())){
            MyYuQ.sendMessage(event.getSendTo(),MyYuQ.getMif().text("错误：消息记录失败，请进入bot管理界面进行查看").toMessage());
        }
    }

    /**
     * 新人进群事件
     * @param event
     */
    @Event
    public void groupMemberJoinEvent(GroupMemberJoinEvent event){
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        //进群提示
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_JoinGroupTip).getValue()){
            String tip = (String) conf.getControl(GroupControlId.Edit_JoinGroupTip).getValue();
            if (!tip.trim().equals("")){
                MyYuQ.sendGroupMessage(event.getGroup(),Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,event.getMember(),null,event.getGroup())));
            }
        }

    }

    /**
     * 退群事件
     * @param event
     */
    @Event
    public void groupMemberLeaveEvent(GroupMemberLeaveEvent event){
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        Member member = event.getMember();
        Group group = event.getGroup();
        //退群提示
        if (conf.isEnable()){
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_QuitGroupTip).getValue()){
                String tip = (String) conf.getControl(GroupControlId.Edit_QuitGroupTip).getValue();
                if (!tip.trim().equals("")){
                    MyYuQ.sendGroupMessage(group,Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,member,null,group)));
                }else {
                    MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("刚刚，" + member.getNameCard() + "(" +
                            member.getName() + ")[" + member.getId() + "]离开了我们，他说过的最后一句话是：").toMessage());
                    try {
                        GroupHistoryMsg groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().queryLast(group.getId(),member.getId());

                        if (groupHistoryMsg == null){
                            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("他好像还没说过话").toMessage());
                            return;
                        }else {
                            MyYuQ.sendGroupMessage(group,Message.Companion.toMessageByRainCode(groupHistoryMsg.getMsg()));
                        }
                    }catch (Exception e){
                        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("出现了一点错误喵：" + e.getMessage()).toMessage());
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
                                MyYuQ.sendGroupMessage(group,Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,member,null,group)));
                            }
                        }
                    } catch (SQLException e) {
                        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("退群拉黑失败:" + e.getMessage()).toMessage());
                        SfLog.getInstance().e(this.getClass(),e);
                    } catch (IllegalAccessException e) {
                        MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("退群拉黑失败:" + e.getMessage()).toMessage());
                        SfLog.getInstance().e(this.getClass(),e);
                    }
                }else {
                    MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text("退群拉黑失败，数据库未启用").toMessage());
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
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        //踢人提示
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_KickTip).getValue()){
            String tip = (String) conf.getControl(GroupControlId.Edit_KickTip).getValue();
            if (!tip.trim().equals("")){
                MyYuQ.sendGroupMessage(event.getGroup(),Message.Companion.toMessageByRainCode(MyYuQ.messageVariable(tip,event.getMember(),null,event.getGroup())));
            }
        }
    }

    /**
     * 申请入群事件
     * @param event
     */
    @Event
    public void groupMemberRequestEvent(GroupMemberRequestEvent event){
        GroupConf conf = GroupConfManager.getInstance().get(event.getGroup().getId());
        //自动同意入群
        if (conf.isEnable() && (Boolean) conf.getControl(GroupControlId.CheckBox_AutoAgreeJoinGroup).getValue()){
            //TODO:黑名单验证
            event.setAccept(true);
            event.setCancel(true);
        }
    }
}
