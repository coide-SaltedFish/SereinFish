package sereinfish.bot.database;

import com.icecreamqaq.yuq.entity.Group;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    }

    /**
     * 关闭数据库
     */
    public void closeDataBase(DataBase dataBase) throws SQLException {
        dataBase.close();
        dataBases.remove(dataBase);
    }

    public ArrayList<DataBase> getDataBases() {
        return dataBases;
    }
}
