package sereinfish.bot.database.dao;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.dao.annotation.*;
import sereinfish.bot.database.dao.annotation.Character;
import sereinfish.bot.database.entity.DataBase;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO<E>{
    private Class table;//表对象
    private String tableName;//表名
    private DataBase dataBase;

    /**
     * 初始化数据库操作对象
     */
    public DAO(DataBase dataBase, Class<E> t) throws SQLException {
        this.dataBase = dataBase;
        this.table = t;
        tableName = ((DBHandle)table.getAnnotation(DBHandle.class)).tableName();

        createTable();//尝试创建数据表
    }

    /**
     * 创建表
     */
    public void createTable() throws SQLException {
        if (tableExist(tableName)){
            return;
        }
        //生成数据库创建语句
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE " + tableName + " (");
        int i = 0;
        for (Field field:table.getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){//如果是字段
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);//得到注解
                if (i != 0){
                    stringBuilder.append(",");
                }
                //生成一个语句
                stringBuilder.append(dField.name() + " ");//名称
                stringBuilder.append(dField.type());//类型
                if(dField.size() > -1){
                    stringBuilder.append("(" + dField.size() + ")");//大小
                }else if (dField.size() == SizeEnum.MAX){
                    if (dataBase.getDataBaseConfig().getState() == DataBaseConfig.MY_SQL){
                        stringBuilder.append("(4000)");//大小
                    }else {
                        stringBuilder.append("(max)");//大小
                    }
                }
                stringBuilder.append(" ");
                //主键
                if (field.isAnnotationPresent(Primary.class)){
                    stringBuilder.append("PRIMARY KEY ");
                }
                //编码
                if (dataBase.getDataBaseConfig().getState() == DataBaseConfig.MY_SQL){
                    if(field.isAnnotationPresent(Character.class)){
                        Character character = field.getAnnotation(Character.class);
                        stringBuilder.append("CHARACTER SET '" + character.encoding() + "' ");//编码
                    }
                }
                if (dField.isNotNull()){
                    stringBuilder.append("NOT NULL");
                }else {
                    stringBuilder.append("DEFAULT " + dField.default_());
                }

                i++;
            }
        }
        stringBuilder.append(")");
        executeDAO(stringBuilder.toString());
    }

    /**
     * 插入值
     * @param value
     */
    public void insert(E value) throws IllegalAccessException, SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
        String val = "";
        int i = 0;
        for (Field field:table.getDeclaredFields()){
            if (!field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                continue;
            }
            if (i != 0){
                sql.append(",");
            }
            sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
            try {
                val = field.get(value).toString();
            } catch (IllegalAccessException e) {
                field.setAccessible(true);
                val = field.get(value).toString();
            }
            if (dField.isChar()){
                val = "?";
            }
            sql.append(val);
            i++;
        }
        sql.append(")");
        PreparedStatement preparedStatement = dataBase.getConnection().prepareStatement(sql.toString());
        //文本处理
        i = 1;
        for (Field field:table.getDeclaredFields()){
            if (!field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                continue;
            }
            sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
            if (dField.isChar()){
                try {
                    val = field.get(value).toString();
                } catch (IllegalAccessException e) {
                    field.setAccessible(true);
                    val = field.get(value).toString();
                }
                preparedStatement.setString(i,val);
                i++;
            }
        }
        preparedStatement.execute();
    }

    /**
     * 删除
     * @param value
     */
    public void delete(E value) throws IllegalAccessException, SQLException {
        //生成sql语句
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " ");
        String val = "";
        //分析删除mark
        int i = 0;
        for(Field field:table.getDeclaredFields()){
            if (!field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                continue;
            }
            if (field.isAnnotationPresent(Mark.class)){
                Mark mark = field.getAnnotation(Mark.class);
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                if (isMarkType(mark.type(),MarkType.DELETE)){
                    if (i == 0){
                        sql.append("WHERE" + " ");
                    }else {
                        sql.append(" AND ");
                    }
                    sql.append(dField.name() + " " + mark.condition()[i] + " ");

                    try {
                         val = field.get(value).toString();
                    } catch (IllegalAccessException e) {
                        field.setAccessible(true);
                        val = field.get(value).toString();
                    }
                    if (dField.isChar()){
                        val = "?";
                    }
                    sql.append(val);
                    i++;
                }
            }
        }
        PreparedStatement preparedStatement = dataBase.getConnection().prepareStatement(sql.toString());
        //文本处理
        i = 1;
        for (Field field:table.getDeclaredFields()){
            if (!field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                continue;
            }
            if (field.isAnnotationPresent(Mark.class)){
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                Mark mark = field.getAnnotation(Mark.class);
                if (dField.isChar() && isMarkType(mark.type(),MarkType.DELETE)){
                    try {
                        val = field.get(value).toString();
                    } catch (IllegalAccessException e) {
                        field.setAccessible(true);
                        val = field.get(value).toString();
                    }
                    preparedStatement.setString(i,val);
                    i++;
                }
            }
        }
        preparedStatement.execute();
    }

    /**
     * 查询
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ArrayList<E> query() throws SQLException, IllegalAccessException, InstantiationException {
        ArrayList<E> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;//命令
        ResultSet resultSet = executeQueryDAO(sql);
        //读取
        while(resultSet.next()) {
            //qq,uuid,time,uuid,time
            E record = (E)table.newInstance();

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

    /**
     * 条件查询
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ArrayList<E> query(String condition) throws SQLException, IllegalAccessException, InstantiationException {
        ArrayList<E> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + "Where " + condition;//命令
        ResultSet resultSet = executeQueryDAO(sql);
        //读取
        while(resultSet.next()) {
            E record = (E)table.newInstance();

            for (Field field:record.getClass().getDeclaredFields()){
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                try{
                    field.set(record,resultSet.getObject(dField.name()));
                }catch (Exception e){
                    field.setAccessible(true);
                    field.set(record,resultSet.getObject(dField.name()));
                }
            }

            list.add(record);
        }
        return list;
    }

    /**
     * 修改
     * @param value
     */
    public void update(E value){

    }

    /**
     * 表中记录条数
     * @return
     */
    public int size() throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        ResultSet resultSet = executeQueryDAO(sql);
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return 0;
    }

    /**
     * 得到表的所有字段名称
     * @return
     */
    public ArrayList<String> getFieldNames() throws SQLException {
        ArrayList<String> arr = new ArrayList<>();
        for (Field field:table.getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                arr.add(dField.name());
            }
        }
        return arr;
    }

    /**
     * 数据库语句执行
     * @param sql
     */
    public boolean executeDAO(String sql) throws SQLException {
        return dataBase.getStatement().execute(sql);
    }

    public int executeUpdateDAO(String sql) throws SQLException {
        return dataBase.getStatement().executeUpdate(sql);
    }

    public ResultSet executeQueryDAO(String sql) throws SQLException {
        PreparedStatement preparedStatement = dataBase.getConnection().prepareStatement(sql);//命令
        return preparedStatement.executeQuery();
    }

    /**
     * 判断标记是否包含
     * @param markTypes
     * @param markType
     * @return
     */
    private boolean isMarkType(MarkType[] markTypes,MarkType markType){
        for (MarkType m:markTypes){
            if (m.equals(markType)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断表是否存在
     * @param name
     * @return
     */
    private boolean tableExist(String name) throws SQLException {
        ResultSet resultSet = dataBase.getConnection().getMetaData().getTables(null, null, name, null );
        if (!resultSet.isClosed()){
            if (resultSet.next()) {
                return true;
            }
        }

        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public Class getType(){
        return table;
    }
}