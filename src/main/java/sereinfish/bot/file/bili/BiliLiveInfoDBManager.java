package sereinfish.bot.file.bili;

import sereinfish.bot.database.dao.DAO;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.MarkIllegalLengthException;
import sereinfish.bot.database.table.BiliLiveInfo;

import java.sql.SQLException;
import java.util.ArrayList;

public class BiliLiveInfoDBManager extends DAO<BiliLiveInfo> {
    /**
     * 初始化数据库操作对象
     *
     * @param dataBase
     */
    public BiliLiveInfoDBManager(DataBase dataBase) throws SQLException, MarkIllegalLengthException {
        super(dataBase, BiliLiveInfo.class);
    }

    /**
     * 通过群组查询
     * @param group
     * @return
     */
    public ArrayList<BiliLiveInfo> query(long group){

        return null;
    }
}
