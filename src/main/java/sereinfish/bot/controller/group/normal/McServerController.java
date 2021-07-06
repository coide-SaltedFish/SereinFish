package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import org.xbill.DNS.*;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.ServerListPing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * mc服务器相关指令
 */
@GroupController
public class McServerController {

    @Action("\\[.!！][Pp]ing\\ {addr}")
    public Message ping(Group group, String addr) throws IOException {
        group.sendMessage(MyYuQ.getMif().text("信息获取中，请稍后").toMessage());

        File file = new File(FileHandle.imageCachePath,"serverState_temp");
        ServerListPing serverListPing = new ServerListPing();
        try {
            //仅SRV解析
            Lookup lookup = new Lookup("_minecraft._tcp." + addr, Type.SRV);
            Record[] records = lookup.run();

            if (lookup.getResult() != Lookup.SUCCESSFUL){
                return Message.Companion.toMessageByRainCode("地址 " + addr + " 解析失败：" + getLookupResultName(lookup.getResult()));
            }
            SRVRecord srv = (SRVRecord) records[0];
            String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  srv.getPort());

            serverListPing.setAddress(inetSocketAddress);

            BufferedImage stateImage = ServerListPing.getServerInfoImage(serverListPing.fetchData());
            ImageIO.write(stateImage, "PNG", file);

            return MyYuQ.getMif().imageByFile(file).toMessage();
        } catch (TextParseException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return Message.Companion.toMessageByRainCode("失败：" + e.getMessage());
        }
    }

    /**
     * 得到解析结果名称
     * @param i
     * @return
     */
    private String getLookupResultName(int i){
        switch (i){
            case 0:
                return "SUCCESSFUL";
            case 1:
                return "UNRECOVERABLE";
            case 2:
                return "TRY_AGAIN";
            case 3:
                return "HOST_NOT_FOUND";
            case 4:
                return "TYPE_NOT_FOUND";
            default:
                return "未知";
        }
    }
}
