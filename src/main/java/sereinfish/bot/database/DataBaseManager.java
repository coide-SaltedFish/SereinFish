package sereinfish.bot.database;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.database.entity.DataBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接管理
 */
public class DataBaseManager {
    private Map<Long,DataBase> dataBases = new HashMap<>();//数据库连接列表
    private static DataBaseManager dataBaseManager;//单例模式

    private DataBaseManager(){

    }

    /**
     * 初始化管理器
     * @return
     */
    public static DataBaseManager init(){
        dataBaseManager = new DataBaseManager();
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
    public void linkDataBase(long group){

    }

    /**
     * 连接池添加数据库
     * @param group
     * @param dataBase
     */
    public void addDataBase(long group,DataBase dataBase) throws SQLException {
        if (dataBases.containsKey(group)){
            closeDataBase(group);
        }
        dataBases.put(group,dataBase);
    }

    /**
     * 关闭数据库
     * @param group
     */
    public void closeDataBase(long group) throws SQLException {
        dataBases.get(group).close();
    }

    /**
     * 得到数据库
     * @param group
     * @return
     */
    public DataBase getDataBase(long group){
        return dataBases.get(group);
    }
}
