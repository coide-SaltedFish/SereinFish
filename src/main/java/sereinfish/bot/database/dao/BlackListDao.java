package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.icecreamqaq.yudb.jpa.annotation.Execute;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.BlackList;

import java.util.List;

@Dao
public interface BlackListDao extends YuDao<BlackList, Integer> {
    List<BlackList> findAll();

    BlackList findByQq(long qq);

    BlackList findByQqAndSource(long qq, long source);

    /**
     * 获取指定群黑名单
     * @param source
     * @return
     */
    List<BlackList> findBySource(long source);

    //删除指定群指定黑名单
    @Execute(value = "DELETE FROM BlackList WHERE group = ?0, qq = ?1", nativeQuery = true)
    void delBlackList(long group, long qq);
}
