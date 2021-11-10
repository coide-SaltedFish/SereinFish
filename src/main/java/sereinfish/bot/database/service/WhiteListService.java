package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.WhiteListDao;
import sereinfish.bot.database.entity.BlackList;
import sereinfish.bot.database.entity.WhiteList;

import javax.inject.Inject;
import java.util.List;

public class WhiteListService {
    @Inject
    private WhiteListDao dao;

    @Transactional(dbList = "user")
    public List<WhiteList> findAll(){
        return dao.findAll();
    }
}
