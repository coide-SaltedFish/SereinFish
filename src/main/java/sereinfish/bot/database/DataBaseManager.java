package sereinfish.bot.database;

import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 数据库连接管理
 */
public class DataBaseManager {
    private ArrayList<DataBase> dataBases = new ArrayList<>();//数据库连接列表
    private static DataBaseManager dataBaseManager;//单例模式

    private DataBaseManager(){

    }

    /**
     * 初始化管理器
     * @return
     */
    public static DataBaseManager init(){
        dataBaseManager = new DataBaseManager();
        dataBaseManager.loadConf();
        return dataBaseManager;
    }

    public static DataBaseManager getInstance(){
        if (dataBaseManager == null){
            throw new NullPointerException("数据库连接池管理器未初始化");
        }
        return dataBaseManager;
    }

    /**
     * 连接数据库
     */
    public void linkDataBase(DataBaseConfig config) throws IllegalModeException, SQLException, ClassNotFoundException {
        DataBase dataBase = new DataBase(config);
        addDataBase(dataBase);
    }

    /**
     * 连接池添加数据库
     * @param dataBase
     */
    public void addDataBase(DataBase dataBase) throws SQLException {
        dataBases.add(dataBase);
        saveConf();
    }

    /**
     * 检测连接池中是否已连接该数据库
     * @param id
     * @return
     */
    public boolean exist(String id){
        for (DataBase dataBase:dataBases){
            if (dataBase.getDataBaseConfig().getID().equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * 得到已连接数据库对象
     * @param id
     * @return
     */
    public DataBase getDataBase(String id) throws SQLException, IllegalModeException, ClassNotFoundException {
        for (DataBase dataBase:dataBases){
            if (dataBase.getDataBaseConfig().getID().equals(id)){
                //数据库是否连接正常检查
                if (dataBase.getConnection().isClosed()){
                    dataBases.remove(dataBase);
                    linkDataBase(dataBase.getDataBaseConfig());
                }
                return dataBase;
            }
        }
        return null;
    }

    /**
     * 关闭数据库
     */
    public void closeDataBase(DataBase dataBase) throws SQLException {
        dataBase.close();
        dataBases.remove(dataBase);
        saveConf();
    }

    public ArrayList<DataBase> getDataBases() {
        return dataBases;
    }

    /**
     * 保存数据库配置
     */
    public void saveConf(){
        ArrayList<DataBaseConfig> dataBaseConfigs = new ArrayList<>();
        for (DataBase dataBase:dataBases){
            dataBaseConfigs.add(dataBase.getDataBaseConfig());
        }
        try {
            writeConf(new Conf(dataBaseConfigs));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"数据库配置保存失败",e);
        }
    }

    /**
     * 读取数据库配置
     */
    public void loadConf(){
        for (DataBaseConfig config:readConf().getConfigs()){
            try {
                linkDataBase(config);
            } catch (IllegalModeException e) {
                SfLog.getInstance().e(this.getClass(),"数据库连接失败：" + config.getBaseName(),e);
            } catch (SQLException e) {
                SfLog.getInstance().e(this.getClass(),"数据库连接失败：" + config.getBaseName(),e);
            } catch (ClassNotFoundException e) {
                SfLog.getInstance().e(this.getClass(),"数据库连接失败：" + config.getBaseName(),e);
            }
        }
    }

    /**
     * 写入数据库配置列表
     * @param configs
     */
    public static void writeConf(Conf configs) throws IOException {
        FileHandle.write(FileHandle.dataBaseConfFile, MyYuQ.toJson(configs,Conf.class));
    }

    /**
     * 读取数据库配置列表
     * @return
     */
    public static Conf readConf(){
        try {
            Conf conf = MyYuQ.toClass(FileHandle.read(FileHandle.dataBaseConfFile),Conf.class);
            if (conf == null){
                conf = new Conf();
            }
            return conf;
        } catch (IOException e) {
            return new Conf();
        }
    }

    static class Conf{
        ArrayList<DataBaseConfig> configs = new ArrayList<>();

        public Conf() {
        }

        public Conf(ArrayList<DataBaseConfig> configs) {
            this.configs = configs;
        }

        public ArrayList<DataBaseConfig> getConfigs() {
            return configs;
        }
    }
}
