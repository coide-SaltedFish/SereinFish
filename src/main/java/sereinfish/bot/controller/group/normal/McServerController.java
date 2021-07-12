package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import org.xbill.DNS.*;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.ServerListPing;
import sereinfish.bot.net.mc.rcon.Rcon;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.net.mc.rcon.ex.AuthenticationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * mc服务器相关指令
 */
@GroupController
public class McServerController {

    @Before
    public GroupConf before(Group group){
        return GroupConfManager.getInstance().get(group.getId());
    }


    @Action("\\[.!！][Ss]tate$\\ {addr}")
    @Synonym("\\[.!！][Ss]tate$\\")
    public Message ping(GroupConf groupConf, Group group, String addr) throws IOException {
        //前置检查
        if ((Boolean) groupConf.getControl(GroupControlId.CheckBox_EnableRcon).getValue()){
            if (!(Boolean) groupConf.getControl(GroupControlId.CheckBox_McServerState).getValue()){
                throw new DoNone();
            }
        }else {
            throw new DoNone();
        }
        if (addr.matches("([.!！][Ss]tate)")){
            if (((String) groupConf.getControl(GroupControlId.Edit_Small_Plain_McServerAddr).getValue()).equals("")){
                return MyYuQ.getMif().text("错误，地址尚未配置，请使用 state {addr} 命令").toMessage();
            }else {
                addr = (String) groupConf.getControl(GroupControlId.Edit_Small_Plain_McServerAddr).getValue();
            }
        }

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

            ServerListPing.StatusResponse response = serverListPing.fetchData();
            //得到Rcon
            Rcon rcon = null;
            if (groupConf.getControl(GroupControlId.SelectRcon).getValue() != null){
                RconConf rconConf = MyYuQ.toClass((String) groupConf.getControl(GroupControlId.SelectRcon).getValue(), RconConf.class);
                if (rconConf != null){
                    rcon = RconManager.getInstance().getRcon(rconConf.getID());
                }
            }

            BufferedImage stateImage = null;
            try {
                stateImage = ServerListPing.getServerInfoImage(response, rcon);
            } catch (Exception e) {
                SfLog.getInstance().e(this.getClass(), e);
                return MyYuQ.getMif().text("服务器信息获取失败：" + e.getMessage()).toMessage();
            }
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
