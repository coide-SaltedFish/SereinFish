package sereinfish.bot.net.mc.rcon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.myYuq.MyYuQ;

@AllArgsConstructor
@Getter
@Setter
public class RconConf {
    private String name;
    private String ip;
    private int port;
    private String password;

    public String getID(){
        return MyYuQ.stringToMD5(ip+password+port);
    }
}
