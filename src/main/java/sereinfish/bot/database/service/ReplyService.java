package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.ReplyDao;
import sereinfish.bot.database.entity.Reply;

import javax.inject.Inject;
import java.util.List;

public class ReplyService {
    @Inject
    private ReplyDao dao;

    @Transactional(dbList = "user")
    public Reply findByUuid(String uuid){
        return dao.findByUuid(uuid);
    }

    @Transactional(dbList = "user")
    public List<Reply> findByKeyAndSource(String key, long group){
        return dao.findByKeyAndSource(key, group);
    }

    @Transactional(dbList = "user")
    public List<Reply> findBySource(long group){
        return dao.findBySource(group);
    }

    @Transactional(dbList = "user")
    public boolean exist(String uuid){
        return dao.findByUuid(uuid) != null;
    }

    @Transactional(dbList = "user")
    public Reply get(int id){
        return dao.get(id);
    }

    @Transactional(dbList = "user")
    public void delete(int id){
        dao.delete(id);
    }

    @Transactional(dbList = "user")
    public void save(Reply d){
        dao.save(d);
    }

    @Transactional(dbList = "user")
    public void saveOrUpdate(Reply d){
        dao.saveOrUpdate(d);
    }
}
