package sereinfish.bot.entity.conf;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.rcon.RconConf_s;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 群组配置
 */
public class GroupConf {
    private long group;//所属群
    private boolean isEnable;//是否启用群
    private DataBaseConfig dataBaseConfig;//数据库
    private RconConf_s rcon;//RCON

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
        toolList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_ReRead,"复读", false,"bot复读功能"));
        confMaps.put("群功能开关",toolList);
        //
        ArrayList<Control> rconList = new ArrayList<>();
        rconList.add(new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_RCON,"启用RCON",false,"启用rcon相关功能"));
        confMaps.put("RCON",rconList);

        //
        ArrayList<Control> databaseList = new ArrayList<>();
        confMaps.put("数据库",databaseList);

        return this;
    }

    /**
     * 得到控件
     * @param id
     * @return
     */
    public Control getControl(GroupControlId id){
        for (Map.Entry<String,ArrayList<Control>> entry:confMaps.entrySet()){
            for (Control control:entry.getValue()){
                if (control.getId() == id){
                    return control;
                }
            }
        }
        return null;
    }

    public DataBase getDataBase(){
        if (dataBaseConfig == null){
            return null;
        }
        return DataBaseManager.getInstance().getDataBase(dataBaseConfig.getID());
    }

    public boolean isDataBaseEnable(){
        if (dataBaseConfig == null){
            return false;
        }
        return DataBaseManager.getInstance().exist(dataBaseConfig.getID());
    }

    public long getGroup() {
        return group;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public Map<String, ArrayList<Control>> getConfMaps() {
        return confMaps;
    }

    public RconConf_s getRcon() {
        return rcon;
    }

    public void setRcon(RconConf_s rcon) {
        this.rcon = rcon;
    }

    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    /**
     * 组件
     */
    public class Control<T>{
        GroupControlType type;//组件类型
        GroupControlId id;//组件id
        String name;//组件名称
        Object value;//值
        String tip;//组件提示

        public Control() {
        }

        public Control(GroupControlType type, GroupControlId id, String name, Object value, String tip) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.value = value;
            this.tip = tip;
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

        public GroupControlType getType() {
            return type;
        }
    }
}
