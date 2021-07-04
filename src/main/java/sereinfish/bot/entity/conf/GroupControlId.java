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
    CheckBox_ReRead,//复读
    //setu
    CheckBox_SetuEnable,//是否启用setu
    Edit_SetuKey,//API Key
    CheckBox_SetuR18,//是否启用R18
    CheckBox_PlainAndR18,//混合模式
    CheckBox_LoliconMD5Image,//图片发送MD5码，高速模式
    CheckBox_LoliconLocalImage,//本地图片
    Button_jumpLolicon,//跳转至lolicon
    CheckBox_SFLoliconEnable,//启用SF加速
    CheckBox_SFLoliconKey,//在启用加速时上传lolicon key
    Edit_SFLoliconApi,//SF加速api
    //wiki
    CheckBox_wikiEnable,
    CheckBox_wikiMcEnable,//mc wiki开关
    CheckBox_wikiBaiduEnable,//baidu 查询开关
    //SauceNAO
    CheckBox_SauceNAOEnable,//启用SauceNAO
    Edit_SauceNAOApiKey,//key

    //Bili
    CheckBox_BiliEnable,//启用bili相关命令
    CheckBox_BiliBvExplain,//将bv号解析

    //
    CheckBox_RCON,//rcon功能
    CheckBox_Ping,//ping功能

    //消息
    CheckBox_LongMsgToImageEnable,//长文本转图片发送
    ComBox_FontSelect,//长文本转图片字体选择
}
