package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import sereinfish.bot.database.entity.Account;

import java.util.List;

@Dao
public interface AccountDao extends YuDao<Account, Integer> {
    List<Account> findAll();

    Account findByTypeAndQq(int type, long qq);
}
