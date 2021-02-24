package sereinfish.bot.file.msg;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageSource;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 历史消息保存
 */
public class GroupHistoryMsgDBManager extends DAO<GroupHistoryMsg>{
    SfLog sfLog = SfLog.getInstance();

    //单例
    private static GroupHistoryMsgDBManager groupHistoryMsgDBManager;
    private GroupHistoryMsgDBManager(DataBase dataBase) throws SQLException {
        super(dataBase,GroupHistoryMsg.class);
    }

    public static GroupHistoryMsgDBManager init() throws IllegalModeException, SQLException, ClassNotFoundException {
        //连接数据库
        DataBase dataBase = new DataBase(new DataBaseConfig(DataBaseConfig.SQLITE,"","","groupMsg","",0));

        groupHistoryMsgDBManager = new GroupHistoryMsgDBManager(dataBase);
        return groupHistoryMsgDBManager;
    }

    public static GroupHistoryMsgDBManager getInstance(){
        if (groupHistoryMsgDBManager == null){
            throw new NullPointerException("群历史信息管理器尚未初始化");
        }
        return groupHistoryMsgDBManager;
    }

    /**
     * 添加记录
     * @param group
     * @param message
     */
    public boolean add(Group group, long qq, Message message){
        GroupHistoryMsg groupHistoryMsg = new GroupHistoryMsg(new Date().getTime(), group.getId(),
                qq, message.getSource().getId(),Message.Companion.toCodeString(message));
        try {
            insert(groupHistoryMsg);
        } catch (IllegalAccessException e) {
            sfLog.e(this.getClass(),"消息记录失败[group：" + group + ",message:" + message.getSourceMessage(),e);
            return false;
        } catch (SQLException throwables) {
            sfLog.e(this.getClass(),"消息记录失败[group：" + group + ",message:" + message.getSourceMessage(),throwables);
            return false;
        }

        return true;
    }

    /**
     * 添加记录
     * @param group
     * @param message
     */
    public boolean add(long group, long qq, Message message){
        GroupHistoryMsg groupHistoryMsg = new GroupHistoryMsg(new Date().getTime(), group,
                qq, message.getSource().getId(),Message.Companion.toCodeString(message));
        try {
            insert(groupHistoryMsg);
        } catch (IllegalAccessException e) {
            sfLog.e(this.getClass(),"消息记录失败[group：" + group + ",message:" + message.getSourceMessage(),e);
            return false;
        } catch (SQLException throwables) {
            sfLog.e(this.getClass(),"消息记录失败[group：" + group + ",message:" + message.getSourceMessage(),throwables);
            return false;
        }

        return true;
    }

    /**
     * 查询记录
     * @param group
     * @param qq
     * @param id
     * @return
     */
    public GroupHistoryMsg query(long group,long qq, int id) throws SQLException {
        PreparedStatement preparedStatement = getDataBase().getConnection().prepareStatement("SELECT * FROM " + getTableName() +
                " Where group_num = ? AND qq = ? AND id = ?");
        preparedStatement.setLong(1,group);
        preparedStatement.setLong(2,qq);
        preparedStatement.setInt(3,id);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return new GroupHistoryMsg(resultSet.getLong("time"),resultSet.getLong("group_num"),resultSet.getLong("qq"),
                    resultSet.getInt("id"),resultSet.getString("msg"));
        }
        return null;
    }

    /**
     * 查询最后一条消息
     * @param group
     * @param qq
     * @return
     * @throws SQLException
     */
    public GroupHistoryMsg queryLast(long group, long qq) throws SQLException {
        GroupHistoryMsg groupHistoryMsg = null;

        String tableName = GroupHistoryMsgDBManager.getInstance().getTableName();
        String sql = "SELECT * FROM " + tableName + " WHERE time = (SELECT MAX(time) FROM " + tableName + " WHERE group_num = " + group + " AND qq = " + qq + ")" ;
        ResultSet resultSet = GroupHistoryMsgDBManager.getInstance().executeQueryDAO(sql);


        if (resultSet.next()){
            groupHistoryMsg = new GroupHistoryMsg(resultSet.getLong("time"),resultSet.getLong("group_num"),resultSet.getLong("qq"),
                    resultSet.getInt("id"),resultSet.getString("msg"));
        }
        return groupHistoryMsg;
    }
}
