package sereinfish.bot.database.handle;

import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.database.table.BlackList;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * 黑名单管理
 */
public class BlackListDao extends DAO<BlackList> {
    String tableName;

    /**
     * 初始化数据库操作对象
     * @param dataBase
     */
    public BlackListDao(DataBase dataBase) throws SQLException, MarkIllegalLengthException {
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
        String sql = "SELECT * FROM " + getTableName() + " WHERE qq = " + qq + " AND group_num = " + group;
        ResultSet resultSet = executeQueryDAO(sql);
        return resultSet.next();
    }

    /**
     * 删除
     * @param qq
     * @throws SQLException
     */
    public void delete(long group, long qq) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE group_num = ? AND qq = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setLong(1 ,group);
        preparedStatement.setLong(2 ,qq);
        preparedStatement.execute();
    }

    /**
     * 查询
     * @param group
     * @return
     */
    public ArrayList<BlackList> query(long group) throws SQLException, IllegalAccessException {
        ArrayList<BlackList> list = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE group_num = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setLong(1, group);

        ResultSet resultSet = preparedStatement.executeQuery();
        //读取
        while(resultSet.next()) {
            BlackList record = new BlackList();

            for (Field field:record.getClass().getDeclaredFields()){
                if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                    sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                    try{
                        field.set(record,resultSet.getObject(dField.name()));
                    }catch (Exception e){
                        field.setAccessible(true);
                        field.set(record,resultSet.getObject(dField.name()));
                    }
                }
            }

            list.add(record);
        }
        return list;
    }
}
