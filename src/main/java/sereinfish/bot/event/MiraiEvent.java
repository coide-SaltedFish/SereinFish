package sereinfish.bot.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

public class MiraiEvent extends SimpleListenerHost {

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        //处理事件处理时抛出的异常
    }

    @EventHandler
    public void messageEvent(MessageEvent event){
    }

    /**
     * 戳一戳事件
     * @param event
     */
    @EventHandler
    public void nudgeEvent(Event event){
    }
}
