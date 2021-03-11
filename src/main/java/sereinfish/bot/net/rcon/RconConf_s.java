package sereinfish.bot.net.rcon;

import sereinfish.bot.myYuq.MyYuQ;

public class RconConf_s {
    String ip;
    int port;
    String password;

    public RconConf_s(String ip, int port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }

    public String getID(){
        return MyYuQ.stringToMD5(ip+password+port);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
