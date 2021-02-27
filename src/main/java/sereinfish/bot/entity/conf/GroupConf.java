package sereinfish.bot.entity.conf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 群组配置
 */
public class GroupConf {
    private long group;//所属群
    private boolean isEnable;//是否启用群
    private Map<String, ArrayList<Control>> confMaps = new LinkedHashMap<>();

    public GroupConf(long group) {
        this.group = group;
        isEnable = false;
        init();
    }

    /**
     * 初始化对象
     */
    public void init(){
        //
        ArrayList<Control> tipList = new ArrayList<>();//提示
        tipList.add(new Control(GroupControlId.CheckBox_JoinGroupTip, "进群提示", false, "是否在有人进群时发出公屏提醒"));
        tipList.add(new Control(GroupControlId.CheckBox_QuitGroupTip, "退群提示", false, "是否在有人退群时发出公屏提醒"));
        tipList.add(new Control(GroupControlId.CheckBox_KickTip, "被踢提示", false, "是否在有人被踢出群聊时发出公屏提醒"));
        tipList.add(new Control(GroupControlId.CheckBox_AddBlackTip, "加黑提示", false, "是否在有人被添加黑名单时发出公屏提醒"));
        confMaps.put("群提示开关",tipList);
        //

    }

    public long getGroup() {
        return group;
    }

    public boolean isEnable() {
        return isEnable;
    }

    /**
     * 组件
     */
    public class Control{
        GroupControlId id;//组件id
        String name;//组件名称
        Object value;//值
        String tip;//组件提示

        ArrayList<String> list = new ArrayList<>();//为下拉列表组件提供的变量

        public Control() {
        }

        public Control(GroupControlId id, String name, Object value, String tip) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.tip = tip;
        }

        public Control(GroupControlId id, String name, Object value, String tip, ArrayList<String> list) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.tip = tip;
            this.list = list;
        }

        public GroupControlId getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public String getTip() {
            return tip;
        }

        public ArrayList<String> getList() {
            return list;
        }
    }
}
