package sereinfish.bot.file.account;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.database.table.Account;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.mlog.SfLog;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AccountManager extends DAO<Account> {
    SfLog sfLog = SfLog.getInstance();
    //单例
    private static AccountManager accountManager;

    /**
     * 初始化数据库操作对象
     * @param dataBase
     */
    private AccountManager(DataBase dataBase) throws SQLException, MarkIllegalLengthException {
        super(dataBase, Account.class);
    }

    public static AccountManager init() throws IllegalModeException, SQLException, ClassNotFoundException, MarkIllegalLengthException {
        //连接数据库
        DataBase dataBase = new DataBase(new DataBaseConfig(DataBaseConfig.SQLITE,"","","account","",0));

        accountManager = new AccountManager(dataBase);
        return accountManager;
    }

    public static AccountManager getInstance(){
        if (accountManager == null){
            throw new NullPointerException("账号管理器尚未初始化");
        }
        return accountManager;
    }

    /**
     * 添加记录
     * @param account
     */
    public boolean add(Account account){
        try {
            insert(account);
        } catch (IllegalAccessException e) {
            sfLog.e(this.getClass(),"账号添加失败：" + account.toString(),e);
            return false;
        } catch (SQLException throwables) {
            sfLog.e(this.getClass(),"账号添加失败：" + account.toString(),throwables);
            return false;
        }
        return true;
    }

    /**
     * 查询记录
     * @return
     */
    public Account query(int type, long qq) throws SQLException, IllegalAccessException {
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement("SELECT * FROM " + getTableName() +
                " Where type = ? AND qq = ?");
        preparedStatement.setInt(1,type);
        preparedStatement.setLong(2,qq);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            Account record = new Account();
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

            return record;
        }
        return null;
    }

    /**
     * token修改
     * @param token
     * @return
     */
    public boolean changerTaken(String token, long qq) throws SQLException {
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement("UPDATE " + getTableName() +
                " SET token = ? WHERE qq = ?");
        preparedStatement.setString(1, token);
        preparedStatement.setLong(2, qq);

        return preparedStatement.execute();
    }
}
