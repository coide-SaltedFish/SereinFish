package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.BlackListDao;
import sereinfish.bot.database.entity.BlackList;

import javax.inject.Inject;
import java.util.List;

public class BlackListService {
    @Inject
    BlackListDao dao;

    @Transactional(dbList = "user")
    public List<BlackList> findAll(){
        return dao.findAll();
    }

    /**
     * 获取指定群黑名单
     * @param group
     * @return
     */
    @Transactional(dbList = "user")
    public List<BlackList> findByGroup(long group){
        return dao.findBySource(group);
    }

    /**
     * 判断是否已存在
     * @param qq
     * @return
     */
    @Transactional(dbList = "user")
    public boolean exist(long qq){
        return  (dao.findByQq(qq) != null);
    }

    /**
     * 判断是否已存在
     * @param qq
     * @return
     */
    @Transactional(dbList = "user")
    public boolean existGroup(long group, long qq){
        return  (dao.findByQq(qq) != null);
    }

    /**
     * 删除指定群指定黑名单
     * @param group
     * @param qq
     */
    @Transactional(dbList = "user")
    public void deleteByGroupAndQq(long group, long qq){
        dao.delBlackList(group, qq);
    }

    @Transactional(dbList = "user")
    public BlackList get(int id){
        return dao.get(id);
    }

    @Transactional(dbList = "user")
    public void delete(int id){
        dao.delete(id);
    }

    @Transactional(dbList = "user")
    public void save(BlackList d){
        dao.save(d);
    }

    @Transactional(dbList = "user")
    public void saveOrUpdate(BlackList d){
        dao.saveOrUpdate(d);
    }
}
