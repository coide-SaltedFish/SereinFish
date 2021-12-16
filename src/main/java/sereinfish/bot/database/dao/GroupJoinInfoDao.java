package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import sereinfish.bot.database.entity.GroupJoinInfo;

import java.util.List;

@Dao
public interface GroupJoinInfoDao extends YuDao<GroupJoinInfo, Integer> {

    List<GroupJoinInfo> findBySource(long source);

    List<GroupJoinInfo> findBySourceAndQq(long source, long qq);
}
