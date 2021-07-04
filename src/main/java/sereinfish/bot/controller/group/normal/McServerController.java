package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.message.Message;
import org.xbill.DNS.*;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.ServerListPing;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * mc服务器相关指令
 */
@GroupController
public class McServerController {

    @Action("\\[.!！][Pp]ing\\ {addr}")
    public Message ping(String addr) throws IOException {
        ServerListPing serverListPing = new ServerListPing();
        try {
            Lookup lookup = new Lookup(addr);
            Record[] records = lookup.run();

            if (lookup.getResult() != Lookup.SUCCESSFUL){
                return Message.Companion.toMessageByRainCode("地址 " + addr + " 解析失败：" + lookup.getResult());
            }
            System.out.println(records[0].getType());
            SRVRecord srv = (SRVRecord) records[0];
            String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  srv.getPort());
            serverListPing.setAddress(inetSocketAddress);

            return Message.Companion.toMessageByRainCode(serverListPing.fetchData().getVersion().getName());
        } catch (TextParseException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return Message.Companion.toMessageByRainCode("失败：" + e.getMessage());
        }
    }
}
