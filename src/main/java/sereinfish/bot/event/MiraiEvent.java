package sereinfish.bot.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import org.jetbrains.annotations.NotNull;
import sereinfish.bot.event.group.repeater.RepeaterManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

public class MiraiEvent extends SimpleListenerHost {

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        //处理事件处理时抛出的异常
    }

    @EventHandler
    public void messageEvent(MessageEvent event){
    }

    /**
     * 群消息事件
     * @param event
     */
    @EventHandler
    public void groupMessageEvent(GroupMessageEvent event){
        //复读
        RepeaterManager.getInstance().add(event.getSender().getId(), event.getGroup(),event.getMessage());
    }

    /**
     * 主动发送消息后
     * @param event
     */
    @EventHandler
    public void groupMessagePostSendEvent(GroupMessagePostSendEvent event){
        //发送是否成功
        if (event.getException() == null){
            SfLog.getInstance().d(this.getClass(), event.getMessage().toString());
            RepeaterManager.getInstance().add(MyYuQ.getYuQ().getBotId(), event.getTarget(), event.getMessage());//复读
        }else {
            //发送失败
            event.getTarget().sendMessage("消息发送失败：" + event.getException().getMessage());
        }


    }

    /**
     * 新成员进群事件
     * @param event
     */
    @EventHandler
    public void memberJoinEventActive(MemberJoinEvent.Invite event){
        System.out.println(event.getInvitor().getNameCard() + " 邀请 " + event.getMember().getNameCard());
    }


    /**
     * 戳一戳事件
     * @param event
     */
    @EventHandler
    public void nudgeEvent(NudgeEvent event){

        //Bot被戳事件
        if (event.getTarget().getId() == MyYuQ.getYuQ().getBotId()){
            SfLog.getInstance().d(this.getClass(), "戳一戳事件：" + event.getFrom().getNick() + " " + event.getAction() + " " + event.getTarget().getNick());
            String[] replyList = {"干嘛？", "唔", "...", "略略略", "？", "在哦", "不要戳我", "哼哼"};
            if (MyYuQ.getRandom(0, 100) < (100 / replyList.length)){
                //戳回去
                event.getFrom().nudge().sendTo(event.getSubject());
            }else {
                event.getSubject().sendMessage(replyList[MyYuQ.getRandom(0, replyList.length - 1)]);
            }
        }
    }
}
