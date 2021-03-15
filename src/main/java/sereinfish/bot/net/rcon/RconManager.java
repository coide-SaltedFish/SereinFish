package sereinfish.bot.net.rcon;

import sereinfish.bot.net.rcon.ex.AuthenticationException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RconManager {
    private Map<String,Rcon> rcons = new LinkedHashMap<>();

    private static RconManager rconManager;
    private RconManager(){}

    public static RconManager init(){
        rconManager = new RconManager();
        return rconManager;
    }

    public static RconManager getInstance(){
        if (rconManager == null){
            throw new NullPointerException("Rcon尚未初始化");
        }
        return rconManager;
    }

    public Rcon getRcon(String id){
        return rcons.get(id);
    }

    /**
     * 连接新的RCON
     * @param conf_s
     * @return
     */
    public Rcon link(RconConf_s conf_s) throws IOException, AuthenticationException {
        Rcon rcon = new Rcon(conf_s);
        rcons.put(conf_s.getID(),rcon);
        return rcon;
    }

    public Map<String, Rcon> getRcons() {
        return rcons;
    }
}
