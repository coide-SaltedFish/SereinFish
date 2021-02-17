package sereinfish.bot.database;

/**
 * 数据库配置信息
 */
public class DataBaseConfig {
    //数据库模式
    public final static int SQL_SERVER = 0;
    public final static int MY_SQL = 1;
    public final static int SQLITE = 2;

    private int state;//连接类型
    private String account;//账号
    private String password;//密码
    private String baseName;//数据库名
    private String ip;//服务器地址
    private int port;//端口

    public DataBaseConfig(int state) {
        this.state = state;
    }

    public DataBaseConfig(int state, String account, String password, String baseName, String ip, int port) {
        this.state = state;
        this.account = account;
        this.password = password;
        this.baseName = baseName;
        this.ip = ip;
        this.port = port;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
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
}
