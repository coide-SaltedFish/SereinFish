package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.AccountDao;
import sereinfish.bot.database.entity.Account;

import javax.inject.Inject;
import java.util.List;

public class AccountService {

    @Inject
    AccountDao dao;

    @Transactional
    public List<Account> findAll(){
        return dao.findAll();
    }

    @Transactional
    public Account findByTypeAndQq(int type, long qq){
        return dao.findByTypeAndQq(type, qq);
    }

    @Transactional
    public Account get(int id){
        return dao.get(id);
    }

    @Transactional
    public void delete(int id){
        dao.delete(id);
    }

    @Transactional
    public void save(Account account){
        dao.save(account);
    }

    @Transactional
    public void saveOrUpdate(Account account){
        dao.saveOrUpdate(account);
    }
}
