package sereinfish.bot.database.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.WhiteList;

import java.util.List;

@Dao
public interface WhiteListDao extends YuDao<WhiteList, Integer> {
    List<WhiteList> findAll();

}
