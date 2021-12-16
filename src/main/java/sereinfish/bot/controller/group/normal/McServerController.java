package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import org.xbill.DNS.*;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.ServerListPing;
import sereinfish.bot.net.mc.rcon.Rcon;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * mc服务器相关指令
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "mc服务器相关")
public class McServerController {

    @Action("\\^[.!！][Ss]tate$\\ {addr}")
    @Synonym("\\^[.!！][Ss]tate$\\")
    @MenuItem(name = "获取服务器状态", usage = "[.!！][Ss]tate {addr} | [.!！][Ss]tate", description = "获取指定地址服务器信息或者获取本群绑定服务器信息")
    public Message ping(GroupConf groupConf, Group group, String addr) throws IOException {
        //前置检查
        if (groupConf.isRconEnable()){
            if (!groupConf.isMcServerState()){
                throw new DoNone();
            }
        }else {
            throw new DoNone();
        }
        //得到Rcon
        Rcon rcon = null;
        String name = "";
        if (addr.matches("([.!！][Ss]tate)")){
            if (groupConf.getMcServerAddr().equals("")){
                return MyYuQ.getMif().text("错误，地址尚未配置，请使用 state {addr} 命令").toMessage();
            }else {
                if (groupConf.getSelectGroupRcon() != null){
                    RconConf rconConf = groupConf.getSelectGroupRcon();
                    if (rconConf != null){
                        rcon = RconManager.getInstance().getRcon(rconConf.getID());
                    }
                }

                addr = groupConf.getMcServerAddr();

                name = groupConf.getMcServerName();
                if (name.equals("")){
                    name = addr;
                }
            }
        }else {
            name = addr;
        }

        group.sendMessage(MyYuQ.getMif().text("信息获取中，请稍后").toMessage());

        File file = new File(FileHandle.imageCachePath,"serverState_temp");
        ServerListPing serverListPing = new ServerListPing();
        try {
            //仅SRV解析
            if (addr.indexOf(":") != -1){
                String addrs[] = addr.split(":");
                if (addrs.length != 2){
                    return Message.Companion.toMessageByRainCode("输入的地址可能有误");
                }
                int port = -1;
                try{
                    port = Integer.valueOf(addrs[1]);
                }catch (Exception e){
                    return Message.Companion.toMessageByRainCode("输入的地址可能有误，解析错误");
                }

                Lookup lookup = new Lookup(addrs[0], Type.A);//A解析
                Record[] records = lookup.run();
                if (lookup.getResult() != Lookup.SUCCESSFUL){
                    try {
                        InetAddress inetAddress = InetAddress.getByName(addrs[0]);
                        String hostname = inetAddress.getHostAddress();
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  port);
                        serverListPing.setAddress(inetSocketAddress);
                    }catch (Exception e){
                        SfLog.getInstance().e(this.getClass(), e);
                        return Message.Companion.toMessageByRainCode("地址 " + addrs[0] + " 解析失败");
                    }
                }else {
                    ARecord record = (ARecord) records[0];
                    String hostname = record.getAddress().getHostAddress();
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  port);
                    serverListPing.setAddress(inetSocketAddress);
                }
            }else {
                Lookup lookup = new Lookup("_minecraft._tcp." + addr, Type.SRV);//SRV解析
                Record[] records = lookup.run();

                if (lookup.getResult() != Lookup.SUCCESSFUL){
                    lookup = new Lookup(addr, Type.A);//A解析
                    records = lookup.run();
                    if (lookup.getResult() != Lookup.SUCCESSFUL){
                        try {
                            InetAddress inetAddress = InetAddress.getByName(addr);
                            String hostname = inetAddress.getHostAddress();
                            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  25565);
                            serverListPing.setAddress(inetSocketAddress);
                        }catch (Exception e){
                            SfLog.getInstance().e(this.getClass(), e);
                            return Message.Companion.toMessageByRainCode("地址 " + addr + " 解析失败");
                        }
                    }else {
                        ARecord record = (ARecord) records[0];
                        String hostname = record.getAddress().getHostAddress();
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  25565);
                        serverListPing.setAddress(inetSocketAddress);
                    }
                }else {
                    SRVRecord srv = (SRVRecord) records[0];
                    String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname,  srv.getPort());
                    serverListPing.setAddress(inetSocketAddress);
                }
            }
            ServerListPing.StatusResponse response = serverListPing.fetchData();

            BufferedImage stateImage = null;
            try {
                stateImage = ServerListPing.getServerInfoImage(name, response, rcon, groupConf);
            } catch (Exception e) {
                SfLog.getInstance().e(this.getClass(), e);
                return MyYuQ.getMif().text("服务器信息获取失败：" + e.getMessage()).toMessage();
            }
            ImageIO.write(stateImage, "PNG", file);

            return new Message().lineQ().plus(group.uploadImage(file)).getMessage();
        } catch (TextParseException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return Message.Companion.toMessageByRainCode("失败：" + e.getMessage());
        }catch (Exception e){
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
