package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.GroupHistoryMsgDao;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.GroupHistoryMsg;

import javax.inject.Inject;
import java.util.List;

public class GroupHistoryMsgService {
    @Inject
    GroupHistoryMsgDao dao;

    @Transactional
    public List<GroupHistoryMsg> findAll(){
        return dao.findAll();
    }

    @Transactional
    public int size(){
        return dao.findAll().size();
    }

    @Transactional
    public GroupHistoryMsg findByGroupAndMid(long  group, int mid){
        return dao.findBySourceAndMid(group, mid);
    }

    @Transactional
    public GroupHistoryMsg findLastByGroupAndQQ(long group, long qq){
        return dao.queLast(group, qq);
    }

    @Transactional
    public GroupHistoryMsg get(int id){
        return dao.get(id);
    }

    @Transactional
    public void delete(int id){
        dao.delete(id);
    }

    @Transactional
    public void save(GroupHistoryMsg d){
        dao.save(d);
    }

    @Transactional
    public void saveOrUpdate(GroupHistoryMsg d){
        dao.saveOrUpdate(d);
    }
}
