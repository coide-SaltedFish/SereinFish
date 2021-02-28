package sereinfish.bot.entity.conf;

/**
 * 群组件id
 */
public enum GroupControlId {
    //单选框
    CheckBox_JoinGroupTip,//进群提示
    CheckBox_QuitGroupTip,//退群提示
    CheckBox_KickTip,//被踢提示
    CheckBox_AddBlackTip,//拉黑提示

    //编辑框
    Edit_JoinGroupTip,//进群提示
    Edit_QuitGroupTip,//退群提示
    Edit_KickTip,//被踢提示
    Edit_AddBlackTip,//拉黑提示
    //
    CheckBox_AutoAgreeJoinGroup,//自动同意入群
    CheckBox_QuitJoinBlackList,//退群拉黑
    CheckBox_AutoReply,//自动回复
    CheckBox_GlobalAutoReply,//全局问答开关
    CheckBox_BlackList,//黑名单功能
    CheckBox_GlobalBlackList,//全局黑名单
    //
    ComboBox_RCON,//rcon下拉选择
    CheckBox_RCON,//rcon功能
    CheckBox_Ping,//ping功能

    //下拉列表
    ComboBox_DataBase,//数据库选择器
}
