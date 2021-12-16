package sereinfish.bot.database.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import sereinfish.bot.database.dao.GroupJoinInfoDao;
import sereinfish.bot.database.entity.GroupJoinInfo;

import javax.inject.Inject;
import java.util.List;

public class GroupJoinInfoService {
    @Inject
    GroupJoinInfoDao dao;

    @Transactional
    public void save(GroupJoinInfo info){
        dao.save(info);
    }

    @Transactional
    public void accept(long group, long qq){
        List<GroupJoinInfo> groupJoinInfoList = findByQqOfGroup(group, qq);
        GroupJoinInfo groupJoinInfo = null;
        for (GroupJoinInfo i:groupJoinInfoList){
            if (groupJoinInfo == null){
                groupJoinInfo = i;
            }else if (i.getTime() > groupJoinInfo.getTime()){
                groupJoinInfo = i;
            }
        }

        if (groupJoinInfo != null){
            groupJoinInfo.setAccept(true);
            dao.update(groupJoinInfo);
        }else {
            GroupJoinInfo info = new GroupJoinInfo(group, qq, "");
            info.setAccept(true);
            dao.save(info);
        }
    }

    @Transactional
    public void saveOrUpdate(GroupJoinInfo info){
        List<GroupJoinInfo> groupJoinInfoList = findByQqOfGroup(info.getSource(), info.getQq());
        GroupJoinInfo groupJoinInfo = null;
        for (GroupJoinInfo i:groupJoinInfoList){
            if (groupJoinInfo == null){
                groupJoinInfo = i;
            }else if (i.getTime() > groupJoinInfo.getTime()){
                groupJoinInfo = i;
            }
        }

        //如果是已经同意的
        if (groupJoinInfo != null && groupJoinInfo.isAccept()){
            save(info);//保存为新
        }else if(groupJoinInfo != null){
            //更新
            groupJoinInfo.setJoinMessage(info.getJoinMessage());
            groupJoinInfo.setTime(info.getTime());
            groupJoinInfo.setInvitation(info.isInvitation());
            dao.update(groupJoinInfo);
        }else{
            dao.save(info);
        }
    }

    @Transactional
    public List<GroupJoinInfo> findByGroup(long group){
        return dao.findBySource(group);
    }

    @Transactional
    public List<GroupJoinInfo> findByQqOfGroup(long group, long qq){
        return dao.findBySourceAndQq(group, qq);
    }
}
