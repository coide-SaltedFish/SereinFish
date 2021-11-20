package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;
import sereinfish.bot.database.dao.GroupHistoryMsgDao;
import sereinfish.bot.database.entity.Account;
import sereinfish.bot.database.entity.GroupHistoryMsg;

import javax.inject.Inject;
import java.util.ArrayList;
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

    /**
     * 得到指定时间节点后的所有记录
     * @param group
     * @return
     */
    @Transactional
    public List<String> findLastTimeByGroupToRainCode(long group, long time){
        List<String> list = new ArrayList<>();
        for (GroupHistoryMsg msg:dao.queSourceAndTime(group, time)){
            Message message = Message.Companion.toMessageByRainCode(msg.getRainCodeMsg());
            StringBuilder str = new StringBuilder();
            for (MessageItem item:message.getBody()){
                if (item instanceof Text){
                    Text text = (Text) item;
                    str.append(text.getText());
                }

            }
            list.add(str.toString());
        }

        return list;
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
