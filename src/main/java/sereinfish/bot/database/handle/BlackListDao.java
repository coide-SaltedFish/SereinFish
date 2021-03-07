package sereinfish.bot.database.handle;

import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.table.BlackList;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 黑名单管理
 */
public class BlackListDao extends DAO<BlackList> {
    String tableName;

    /**
     * 初始化数据库操作对象
     * @param dataBase
     */
    public BlackListDao(DataBase dataBase) throws SQLException {
        super(dataBase, BlackList.class);
    }

    /**
     * 检测是否存在于黑名单
     * @param qq
     * @return
     */
    public boolean exist(long qq) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE qq = " + qq;
        ResultSet resultSet = executeQueryDAO(sql);
        return resultSet.next();
    }

    /**
     * 检测是否存在某群的黑名单
     * @param group
     * @param qq
     * @return
     */
    public boolean exist(long group, long qq) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE qq = " + qq + " AND group = " + group;
        ResultSet resultSet = executeQueryDAO(sql);
        return resultSet.next();
    }
}
