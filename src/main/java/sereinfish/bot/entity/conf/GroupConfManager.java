package sereinfish.bot.entity.conf;

import java.util.LinkedHashMap;
import java.util.Map;

public class GroupConfManager {
    private Map<Long,GroupConf> groupConfMap = new LinkedHashMap<>();//群聊配置列表

    private static GroupConfManager manager;
    private GroupConfManager(){}

    public static GroupConfManager init(){
        manager = new GroupConfManager();
        return manager;
    }

    public static GroupConfManager getInstance(){
        if (manager == null){
            throw new NullPointerException("群配置管理器尚未初始化");
        }
        return manager;
    }

    /**
     * 添加新配置
     * @param groupConf
     */
    public void put(GroupConf groupConf){
        groupConfMap.put(groupConf.getGroup(),groupConf);
    }

    /**
     * 得到群配置
     * @param group
     */
    public GroupConf get(long group){
        return groupConfMap.get(group);
    }
}
