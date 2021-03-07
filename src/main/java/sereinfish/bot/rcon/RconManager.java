package sereinfish.bot.rcon;

import java.util.ArrayList;

public class RconManager {
    private ArrayList<Rcon> rcons = new ArrayList<>();

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

    public ArrayList<Rcon> getRcons(){
        return rcons;
    }
}
