package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.Reply;

import java.util.List;

@Dao
public interface ReplyDao extends YuDao<Reply, Integer> {
    List<Reply> findAll();

    Reply findByUuid(String uuid);

    List<Reply> findByKeyAndSource(String key, long source);

    List<Reply> findBySource(long source);
}
