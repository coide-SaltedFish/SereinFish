package sereinfish.bot.entity.conf;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.rcon.RconConf_s;
import sereinfish.bot.ui.panel.GroupConfPanel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 群组配置
 */
public class GroupConf {
    private int v = GroupConfPanel.NOW_V;
    private long group;//所属群
    private boolean isEnable = false;//是否启用群
    private DataBaseConfig dataBaseConfig;//数据库
    private RconConf_s rcon;//RCON

    private Map<String, Map<GroupControlId,Control>> confMaps = new LinkedHashMap<>();//控件开关列表

    public GroupConf(long group) {
        this.group = group;
        isEnable = false;
        init();
    }

    /**
     * 在版本号更新时会执行此处
     */
    @Deprecated
    public void update(){
        confMaps.remove("数据库");
        confMaps.get("SETU").put(GroupControlId.CheckBox_LoliconMD5Image,new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_LoliconMD5Image,"MD5发送模式",false,"有效加快发送速度且能避免mirai的5000ms异常"));
    }

    /**
     * 初始化对象
     */
    public GroupConf init(){
        //
        Map<GroupControlId,Control> tipList = new LinkedHashMap<>();//提示
        tipList.put(GroupControlId.CheckBox_JoinGroupTip, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_JoinGroupTip, "进群提示", false, "是否在有人进群时发出公屏提醒"));
        tipList.put(GroupControlId.CheckBox_QuitGroupTip, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_QuitGroupTip, "退群提示", false, "是否在有人退群时发出公屏提醒"));
        tipList.put(GroupControlId.CheckBox_KickTip, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_KickTip, "被踢提示", false, "是否在有人被踢出群聊时发出公屏提醒"));
        tipList.put(GroupControlId.CheckBox_AddBlackTip, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AddBlackTip, "加黑提示", false, "是否在有人被添加黑名单时发出公屏提醒"));
        confMaps.put("群提示开关",tipList);
        //
        Map<GroupControlId,Control> tipMsgList = new LinkedHashMap<>();
        tipMsgList.put(GroupControlId.Edit_JoinGroupTip, new Control(GroupControlType.Edit,GroupControlId.Edit_JoinGroupTip,"进群提示","","修改进群提示"));
        tipMsgList.put(GroupControlId.Edit_QuitGroupTip, new Control(GroupControlType.Edit,GroupControlId.Edit_QuitGroupTip,"退群提示","","修改退群提示"));
        tipMsgList.put(GroupControlId.Edit_KickTip, new Control(GroupControlType.Edit,GroupControlId.Edit_KickTip,"被踢提示","","修改被踢提示"));
        tipMsgList.put(GroupControlId.Edit_AddBlackTip, new Control(GroupControlType.Edit,GroupControlId.Edit_AddBlackTip,"加黑提示","","修改加黑提示"));
        confMaps.put("群提示编辑",tipMsgList);

        //
        Map<GroupControlId,Control> toolList = new LinkedHashMap<>();//群功能开关
        toolList.put(GroupControlId.CheckBox_AutoAgreeJoinGroup, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AutoAgreeJoinGroup,"自动同意入群", false,"在有人申请入群时自动同意申请"));
        toolList.put(GroupControlId.CheckBox_QuitJoinBlackList, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_QuitJoinBlackList,"退群拉黑", false,"在有人退群时自动拉入黑名单"));
        toolList.put(GroupControlId.CheckBox_AutoReply, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_AutoReply,"自动回复", false,"自动查表根据关键词进行回复"));
        toolList.put(GroupControlId.CheckBox_GlobalAutoReply, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_GlobalAutoReply,"全局问答", false,"自动回复是使用全局问答列表"));
        toolList.put(GroupControlId.CheckBox_BlackList, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_BlackList,"黑名单", false,"启用黑名单功能"));
        toolList.put(GroupControlId.CheckBox_GlobalBlackList, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_GlobalBlackList,"全局黑名单", false,"黑名单功能使用全局黑名单"));
        toolList.put(GroupControlId.CheckBox_ReRead, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_ReRead,"复读", false,"bot复读功能"));
        confMaps.put("群功能开关",toolList);
        //
        Map<GroupControlId,Control> setuList = new LinkedHashMap<>();//setu
        setuList.put(GroupControlId.CheckBox_SetuEnable, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_SetuEnable,"启用",false,"启用LoliconAPI"));
        setuList.put(GroupControlId.CheckBox_SetuR18, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_SetuR18,"R18",false,"Lolicon API R18"));
        setuList.put(GroupControlId.CheckBox_PlainAndR18, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_PlainAndR18,"混合模式",false,"R18与非R8混合"));
        setuList.put(GroupControlId.CheckBox_LoliconLocalImage, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_LoliconLocalImage,"本地模式",false,"额度用完后使用缓存的图片"));
        setuList.put(GroupControlId.CheckBox_LoliconMD5Image, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_LoliconMD5Image,"MD5发送模式",false,"有效加快发送速度且能避免mirai的5000ms异常"));
        setuList.put(GroupControlId.Edit_SetuKey, new Control(GroupControlType.Edit,GroupControlId.Edit_SetuKey,"API KEY","","修改API KEY"));
        setuList.put(GroupControlId.Button_jumpLolicon, new Control(GroupControlType.Button, GroupControlId.Button_jumpLolicon, "Lolicon", "https://api.lolicon.app/#/setu?id=telegram-bot/", "跳转到Lolicon"));

        setuList.put(GroupControlId.CheckBox_SFLoliconEnable, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_SetuEnable,"启用SF加速",false,"启用SFLoliconAPI"));
        setuList.put(GroupControlId.CheckBox_SFLoliconKey, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_SetuEnable,"向SF加速服务器上传Key",false,"向SF加速服务器上传Key"));
        setuList.put(GroupControlId.Edit_SFLoliconApi, new Control(GroupControlType.Edit,GroupControlId.Edit_SetuKey,"SF服务器api","","SF服务器api"));

        confMaps.put("SETU",setuList);
        //
        Map<GroupControlId,Control> rconList = new LinkedHashMap<>();
        rconList.put(GroupControlId.CheckBox_RCON, new Control(GroupControlType.CheckBox,GroupControlId.CheckBox_RCON,"启用RCON",false,"启用rcon相关功能"));
        confMaps.put("RCON",rconList);

        return this;
    }

    /**
     * 得到控件
     * @param id
     * @return
     */
    public Control getControl(GroupControlId id){
        for (Map.Entry<String,Map<GroupControlId,Control>> entry:confMaps.entrySet()){
            if (entry.getValue().containsKey(id)){
                return entry.getValue().get(id);
            }
        }
        return null;
    }

    /**
     * 得到控件
     * @param groupName
     * @param name
     * @return
     */
    public Control getControl(String groupName, String name){
        if (confMaps.containsKey(groupName)){
            for (Map.Entry<GroupControlId,Control> entry:confMaps.get(groupName).entrySet()){
                if (entry.getValue().getName().equals(name)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }


    /**
     * 得到控件
     * @param groupName
     * @param name
     * @return
     */
    public boolean setControlValue(String groupName, String name, Object value){
        if (confMaps.containsKey(groupName)){
            for (Map.Entry<GroupControlId,Control> entry:confMaps.get(groupName).entrySet()){
                if (entry.getValue().getName().equals(name)){
                    entry.getValue().setValue(value);
                    return GroupConfManager.getInstance().put(this);
                }
            }
        }
        return false;
    }

    public DataBase getDataBase(){
        if (dataBaseConfig == null){
            return null;
        }
        try {
            return DataBaseManager.getInstance().getDataBase(dataBaseConfig.getID());
        } catch (SQLException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (IllegalModeException e) {
            SfLog.getInstance().e(this.getClass(),e);
        } catch (ClassNotFoundException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }
        return null;
    }

    public boolean isDataBaseEnable(){
        if (dataBaseConfig == null){
            return false;
        }
        return DataBaseManager.getInstance().exist(dataBaseConfig.getID());
    }

    /**
     * 得到控件组列表
     * @return
     */
    public ArrayList<String> getGroupNames(){
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Map<GroupControlId,Control>> entry:confMaps.entrySet()){
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * 得到控件组内控件列表
     * @param groupName
     * @return
     */
    public ArrayList<String> getGroupControlNames(String groupName){
        if (confMaps.containsKey(groupName)){
            ArrayList<String> list = new ArrayList<>();
            for (Map.Entry<GroupControlId,Control> entry:confMaps.get(groupName).entrySet()){
                if (entry.getValue().getValue() instanceof Boolean){
                    list.add(entry.getValue().getName() + ":[" + entry.getValue().getValue() + "]");
                }
            }
            return list;
        }
        return null;
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

    public Map<String, Map<GroupControlId,Control>> getConfMaps() {
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

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
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
