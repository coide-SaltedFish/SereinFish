package sereinfish.bot.database.handle;

import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.table.Reply;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.myYuq.MyYuQ;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReplyDao extends DAO<Reply> {
    private static final char[] ids = "12345567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 初始化数据库操作对象
     * @param dataBase
     */
    public ReplyDao(DataBase dataBase) throws SQLException {
        super(dataBase, Reply.class);
    }

    @Override
    public void insert(Reply value) throws IllegalAccessException, SQLException {
        value.setId(getID());
        super.insert(value);
    }

    /**
     * 生成8位id
     * @return
     */
    public String getID() throws IllegalAccessException, SQLException {
        String id;
        //尝试10w次最多
        for (int i = 0; i < 100000; i++){
            id = "";
            for (int j = 0; j < 8; j++){
                id += ids[MyYuQ.getRandom(0,ids.length - 1)];
            }
            //检测是否重复
            if (!isExistId(id)){
                return id;
            }
        }
        throw new IllegalAccessException("id生成失败");
    }

    /**
     * 检测id是否重复
     * @param id
     * @return
     */
    public boolean isExistId(String id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setString(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    /**
     * 检测问答是否已存在
     * @param uuid
     * @return
     * @throws SQLException
     */
    public boolean exist(String uuid) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE uuid = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setString(1,uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    /**
     * 根据id删除
     * @param id
     */
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setString(1 ,id);
        preparedStatement.execute();
    }

    /**
     * 查询本群词库
     * @param key
     * @param group
     * @return
     * @throws SQLException
     */
    public ArrayList<Reply> query(String key, long group) throws SQLException, IllegalAccessException {
        ArrayList<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE key_ LIKE ? AND group_num = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setString(1, "%" + key + "%");
        preparedStatement.setLong(2, group);

        ResultSet resultSet = preparedStatement.executeQuery();
        //读取
        while(resultSet.next()) {
            Reply record = new Reply();

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

            replies.add(record);
        }

        return replies;
    }

    /**
     * 查询本群词库
     * @param group
     * @return
     * @throws SQLException
     */
    public ArrayList<Reply> query(long group) throws SQLException, IllegalAccessException {
        ArrayList<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE group_num = ?";
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
        preparedStatement.setLong(1, group);

        ResultSet resultSet = preparedStatement.executeQuery();
        //读取
        while(resultSet.next()) {
            Reply record = new Reply();

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

            replies.add(record);
        }

        return replies;
    }

    /**
     * 检测关键词是否存在
     * @param group
     * @param key
     * @return
     */
    public String queryKey(long group, String key) throws SQLException {
        //得到群聊配置
        GroupConf conf = GroupConfManager.getInstance().get(group);
        String sql;

        //判断是否启用全局
        if (!(Boolean) conf.getControl(GroupControlId.CheckBox_GlobalAutoReply).getValue()){
            //精确匹配
            sql = "SELECT * FROM " + getTableName() + " WHERE key_ = ? AND group_num = ? AND is_fuzzy = ?";
            PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, key);
            preparedStatement.setLong(2,group);
            preparedStatement.setInt(3,Reply.BOOLEAN_FALSE);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("reply");
            }else {
                //模糊匹配
                sql = "SELECT * FROM " + getTableName() + " WHERE key_ LIKE ? AND group_num = ? AND is_fuzzy = ?";
                preparedStatement = getDataBase().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, "%" + key + "%");
                preparedStatement.setLong(2,group);
                preparedStatement.setInt(3,Reply.BOOLEAN_TRUE);
                ArrayList<String> replys = new ArrayList<>();

                while (resultSet.next()){
                    replys.add(resultSet.getString("reply"));
                }
                if (replys.size() == 0){
                    return null;
                }else {
                    return replys.get(MyYuQ.getRandom(0,replys.size() - 1));
                }
            }
        }else {
            //精确匹配
            sql = "SELECT * FROM " + getTableName() + " WHERE key_ = ? AND private = ? AND is_fuzzy = ?";
            PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, key);
            preparedStatement.setInt(2,Reply.BOOLEAN_FALSE);
            preparedStatement.setInt(3,Reply.BOOLEAN_FALSE);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("reply");
            }else {
                sql = "SELECT * FROM " + getTableName() + " WHERE key_ LIKE ? AND private = ? AND is_fuzzy = ?";
                preparedStatement = getDataBase().getConnection().prepareStatement(sql);
                preparedStatement.setString(1, "%" + key + "%");
                preparedStatement.setInt(2,Reply.BOOLEAN_FALSE);
                preparedStatement.setInt(3,Reply.BOOLEAN_TRUE);
                ArrayList<String> replys = new ArrayList<>();

                while (resultSet.next()){
                    replys.add(resultSet.getString("reply"));
                }
                if (replys.size() == 0){
                    return null;
                }else {
                    return replys.get(MyYuQ.getRandom(0,replys.size() - 1));
                }
            }
        }
    }
}
