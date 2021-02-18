package sereinfish.bot.ui.list;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 列表缓存机制，避免重绘制面板
 */
public class CellManager {
    private Map<Long, JPanel> panelMap = new HashMap<>();

    private Map<Long,Map<Long,Object>> controlMap = new HashMap<>();

    /**
     * 对应面板是否存在
     * @param id
     * @return
     */
    public boolean exist(long id){
        return panelMap.containsKey(id);
    }

    /**
     * 面板添加
     * @param id
     * @param panel
     */
    public void add(long id,JPanel panel){
        panelMap.put(id,panel);
    }

    /**
     * 得到对应面板
     * @param id
     * @return
     */
    public JPanel get(long id){
        return panelMap.get(id);
    }

    /**
     * 保存控件
     * @param id
     * @param key
     * @param o
     */
    public void addControl(long id,Long key,Object o){
        if (controlMap.containsKey(id)){
            controlMap.get(id).put(key,o);
        }else {
            Map<Long,Object> map = new HashMap<>();
            map.put(key,o);

            controlMap.put(id,map);
        }
    }

    /**
     * 得到控件
     * @param id
     * @param key
     * @return
     */
    public Object getControl(long id,long key){
        if (!controlMap.containsKey(id)){
            return null;
        }
        return controlMap.get(id).get(key);
    }
}
