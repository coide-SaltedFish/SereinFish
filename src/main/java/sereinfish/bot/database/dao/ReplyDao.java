package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.icecreamqaq.yudb.jpa.annotation.Select;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.Reply;

import java.util.List;

@Dao
public interface ReplyDao extends YuDao<Reply, Integer> {
    List<Reply> findAll();

    Reply findByUuid(String uuid);

    Reply findByUuidAndSource(String uuid, long source);

    @Select(value = "FROM Reply WHERE reKey LIKE ?0 AND source = ?1", nativeQuery = true)
    List<Reply> finLikeKeyAndSource(String key, long source);

    List<Reply> findBySource(long source);
}
