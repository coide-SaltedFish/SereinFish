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
    public GroupConf init(){
        //
        ArrayList<Control> tipList = new ArrayList<>();//提示
        tipList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_JoinGroupTip, "进群提示", false, "是否在有人进群时发出公屏提醒"));
        tipList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_QuitGroupTip, "退群提示", false, "是否在有人退群时发出公屏提醒"));
        tipList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_KickTip, "被踢提示", false, "是否在有人被踢出群聊时发出公屏提醒"));
        tipList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AddBlackTip, "加黑提示", false, "是否在有人被添加黑名单时发出公屏提醒"));
        confMaps.put("群提示开关",tipList);
        //
        ArrayList<Control> tipMsgList = new ArrayList<>();
        tipMsgList.add(new Control(GroupControlType.Edit,GroupControlId.Edit_JoinGroupTip,"进群提示","","修改进群提示"));
        tipMsgList.add(new Control(GroupControlType.Edit,GroupControlId.Edit_QuitGroupTip,"退群提示","","修改退群提示"));
        tipMsgList.add(new Control(GroupControlType.Edit,GroupControlId.Edit_KickTip,"被踢提示","","修改被踢提示"));
        tipMsgList.add(new Control(GroupControlType.Edit,GroupControlId.Edit_AddBlackTip,"加黑提示","","修改加黑提示"));
        confMaps.put("群提示编辑",tipMsgList);

        //
        ArrayList<Control> toolList = new ArrayList<>();//群功能开关
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AutoAgreeJoinGroup,"自动同意入群", false,"在有人申请入群时自动同意申请"));
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_QuitJoinBlackList,"退群拉黑", false,"在有人退群时自动拉入黑名单"));
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AutoReply,"自动回复", false,"自动查表根据关键词进行回复"));
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_QuitJoinBlackList,"全局问答", false,"自动回复是使用全局问答列表"));
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_BlackList,"黑名单", false,"启用黑名单功能"));
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_GlobalBlackList,"全局黑名单", false,"黑名单功能使用全局黑名单"));
        confMaps.put("群功能开关",toolList);
        //
        ArrayList<Control> rconList = new ArrayList<>();
        rconList.add(new Control(GroupControlType.ComboBox,GroupControlId.ComboBox_RCON,"RCON",0,"选择要连接的RCON"));
        confMaps.put("RCON",rconList);

        //
        ArrayList<Control> databaseList = new ArrayList<>();

        confMaps.put("数据库",databaseList);

        return this;
    }

    public long getGroup() {
        return group;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public Map<String, ArrayList<Control>> getConfMaps() {
        return confMaps;
    }

    /**
     * 组件
     */
    public class Control{
        GroupControlType type;//组件类型
        GroupControlId id;//组件id
        String name;//组件名称
        Object value;//值
        String tip;//组件提示

        ArrayList<String> list = new ArrayList<>();//为下拉列表组件提供的变量

        public Control() {
        }

        public Control(GroupControlType type, GroupControlId id, String name, Object value, String tip) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.value = value;
            this.tip = tip;
        }

        public Control(GroupControlType type, GroupControlId id, String name, Object value, String tip, ArrayList<String> list) {
            this.type = type;
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

        public void setValue(Object value) {
            this.value = value;
        }

        public String getTip() {
            return tip;
        }

        public ArrayList<String> getList() {
            return list;
        }

        public GroupControlType getType() {
            return type;
        }


    }
}
