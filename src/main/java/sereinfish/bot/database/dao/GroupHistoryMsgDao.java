package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.icecreamqaq.yudb.jpa.annotation.Select;
import sereinfish.bot.database.entity.GroupHistoryMsg;

import java.util.List;

@Dao
public interface GroupHistoryMsgDao extends YuDao<GroupHistoryMsg, Integer> {
    List<GroupHistoryMsg> findAll();

    GroupHistoryMsg findBySourceAndMid(long source, int mid);

    @Select("FROM GroupHistoryMsg WHERE time = (SELECT MAX(time) FROM GroupHistoryMsg WHERE source = ?0 AND qq = ?1)" )
    GroupHistoryMsg queLast(long source, long qq);
}
