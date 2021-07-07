package sereinfish.bot.net.mc.rcon;

import lombok.Getter;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.rcon.ex.AuthenticationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RconManager {
    private Map<String,Rcon> rcons = new LinkedHashMap<>();//Rcon连接列表

    private static RconManager rconManager;
    private RconManager(){}

    public static RconManager init(){
        rconManager = new RconManager();
        //读取已保存的连接进行连接
        rconManager.loadConf();

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
     * 加载配置文件
     */
    public void loadConf(){
        rcons = new LinkedHashMap<>();
        for (RconConf conf:RconManager.readConf().getRconConfs()){
            try {
                rcons.put(conf.getID(), link(conf));
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), e);
            } catch (AuthenticationException e) {
                SfLog.getInstance().e(this.getClass(), e);
            }
        }
    }

    /**
     * 连接新的RCON
     * @param conf_s
     * @return
     */
    public Rcon link(RconConf conf_s) throws IOException, AuthenticationException {
        Rcon rcon = new Rcon(conf_s);
        rcons.put(conf_s.getID(),rcon);
        saveConf();
        return rcon;
    }

    public Map<String, Rcon> getRcons() {
        return rcons;
    }

    /**
     * 保存Rcon配置
     */
    public void saveConf(){
        ArrayList<RconConf> rconConfs = new ArrayList<>();
        for (Map.Entry<String,Rcon> entry:rcons.entrySet()){
            rconConfs.add(entry.getValue().getConfig());
        }
        try {
            writeConf(new RconManager.Conf(rconConfs));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"Rcon配置保存失败",e);
        }
    }

    /**
     * 写入RCON配置列表
     * @param configs
     */
    public static void writeConf(RconManager.Conf configs) throws IOException {
        FileHandle.write(FileHandle.rconConfFile, MyYuQ.toJson(configs, RconManager.Conf.class));
    }

    /**
     * 读取RCON配置列表
     * @return
     */
    public static RconManager.Conf readConf(){
        try {
            RconManager.Conf conf = MyYuQ.toClass(FileHandle.read(FileHandle.rconConfFile), RconManager.Conf.class);
            if (conf == null){
                conf = new RconManager.Conf();
            }
            return conf;
        } catch (IOException e) {
            return new RconManager.Conf();
        }
    }

    @Getter
    static class Conf{
        public ArrayList<RconConf> rconConfs = new ArrayList<>();

        public Conf(){}

        public Conf(ArrayList<RconConf> confs){
            rconConfs = confs;
        }
    }
}
