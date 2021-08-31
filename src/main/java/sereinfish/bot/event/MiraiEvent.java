package sereinfish.bot.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupMessagePostSendEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import sereinfish.bot.event.group.repeater.RepeaterManager;

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
        //RepeaterManager.getInstance().add(event.getGroup(),event.getMessage());
    }

    /**
     * 主动发送消息后
     * @param event
     */
    @EventHandler
    public void groupMessagePostSendEvent(GroupMessagePostSendEvent event){
        //RepeaterManager.getInstance().add(event.getTarget(), event.getMessage());//复读
    }


    /**
     * 戳一戳事件
     * @param event
     */
    @EventHandler
    public void nudgeEvent(Event event){
    }
}
