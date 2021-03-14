package sereinfish.bot.ui.list;

import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 列表缓存机制，避免重绘制面板
 */
public class CellManager {
    private Map<String, JPanel> panelMap = new HashMap<>();

    private Map<String,Map<Long,Object>> controlMap = new HashMap<>();

    /**
     * 对应面板是否存在
     * @param id
     * @return
     */
    public boolean exist(long id){
        return panelMap.containsKey(id + "");
    }

    /**
     * 对应面板是否存在
     * @param id
     * @return
     */
    public boolean exist(String id){
        return panelMap.containsKey(id);
    }

    /**
     * 面板添加
     * @param id
     * @param panel
     */
    public void add(long id,JPanel panel){
        panelMap.put(MyYuQ.stringToMD5(id+""),panel);
    }

    /**
     * 面板添加
     * @param id
     * @param panel
     */
    public void add(String id,JPanel panel){
        panelMap.put(id,panel);
    }

    /**
     * 得到对应面板
     * @param id
     * @return
     */
    public JPanel get(long id){
        return panelMap.get(id + "");
    }

    /**
     * 得到对应面板
     * @param id
     * @return
     */
    public JPanel get(String id){
        return panelMap.get(id);
    }

    /**
     * 保存控件
     * @param id
     * @param key
     * @param o
     */
    public void addControl(long id,long key,Object o){
        if (controlMap.containsKey(id)){
            controlMap.get(id).put(key,o);
        }else {
            Map<Long,Object> map = new HashMap<>();
            map.put(key,o);

            controlMap.put(id + "",map);
        }
    }

    /**
     * 保存控件
     * @param id
     * @param key
     * @param o
     */
    public void addControl(String id,long key,Object o){
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
        return controlMap.get(id + "").get(key);
    }

    /**
     * 得到控件
     * @param id
     * @param key
     * @return
     */
    public Object getControl(String id,long key){
        if (!controlMap.containsKey(id)){
            return null;
        }
        return controlMap.get(id).get(key);
    }
}
