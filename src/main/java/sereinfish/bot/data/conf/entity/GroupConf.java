package sereinfish.bot.data.conf.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.data.conf.annotation.Control;
import sereinfish.bot.data.conf.ControlType;
import sereinfish.bot.ui.context.ControlManager;

import java.io.File;
import java.io.IOException;

/**
 * 群配置
 */

@Getter
@Setter
@NoArgsConstructor
public class GroupConf{
    @NonNull
    long group;//群号

    public GroupConf(long group){
        this.group = group;
    }

    @Control(group = "启用", name = "启用", type = ControlType.CheckBox, tip = "选择是否启用此群")
    boolean enable = false;
    //数据库
    @Control(group = "数据库", name = "数据库选择", type = ControlType.DataBaseSelect, tip = "选择是否启用此群")
    DataBaseConfig dataBaseConfig = null;
    //群提示开关
    @Control(group = "群提示开关", name = "启用进群提示", type = ControlType.CheckBox, tip = "启用新人进群提示")
    boolean joinGroupTipEnable = false;

    @Control(group = "群提示开关", name = "启用退群提示", type = ControlType.CheckBox, tip = "启用退群提示")
    boolean quitGroupTipEnable = false;

    @Control(group = "群提示开关", name = "启用踢人提示", type = ControlType.CheckBox, tip = "启用踢人提示")
    boolean kickGroupTipEnable = false;

    @Control(group = "群提示开关", name = "启用加黑提示", type = ControlType.CheckBox, tip = "启用加黑提示")
    boolean addGroupBlackListTip = false;
    //群提示编辑
    @Control(group = "群提示编辑", name = "进群提示", type = ControlType.Edit, tip = "编辑进群提示")
    String joinGroupTipText = "";

    @Control(group = "群提示编辑", name = "退群提示", type = ControlType.Edit, tip = "编辑退群提示")
    String quitGroupTipText = "";

    @Control(group = "群提示编辑", name = "踢人提示", type = ControlType.Edit, tip = "编辑踢人提示")
    String kickGroupTipText = "";

    @Control(group = "群提示编辑", name = "加黑提示", type = ControlType.Edit, tip = "编辑加黑提示")
    String addGroupBlackListTipText = "";
    //自动同意入群
    @Control(group = "自动同意入群", name = "自动同意入群", type = ControlType.CheckBox, tip = "在有人申请入群时自动同意申请(需管理员权限)")
    boolean autoAgreeJoinGroupEnable = false;
    //黑名单
    @Control(group = "黑名单", name = "启用黑名单", type = ControlType.CheckBox, tip = "启用黑名单功能")
    boolean blackListGroupEnable = false;

    @Control(group = "黑名单", name = "启用全局黑名单", type = ControlType.CheckBox, tip = "启用全局黑名单功能")
    boolean globalBlackListGroupEnable = false;

    @Control(group = "黑名单", name = "退群自动拉黑", type = ControlType.CheckBox, tip = "在有人退群时自动拉入黑名单")
    boolean quitJoinBlackListEnable = false;
    //复读
    @Control(group = "复读", name = "启用复读", type = ControlType.CheckBox, tip = "bot复读功能开关")
    boolean reReadEnable = false;

    @Control(group = "复读", name = "复读条件", type = ControlType.Edit_IntNum, tip = "bot复读所需消息条数")
    int reReadNum = 2;
    //自动回复
    @Control(group = "自动回复", name = "启用自动回复", type = ControlType.CheckBox, tip = "自动查表根据关键词进行回复")
    boolean autoReplyEnable = false;

    @Control(group = "自动回复", name = "启用全局问答", type = ControlType.CheckBox, tip = "自动回复时使用全局问答列表")
    boolean globalAutoReplyEnable = false;
    //SauceNaoAPI
    @Control(group = "SauceNaoAPI", name = "启用", type = ControlType.CheckBox, tip = "启用SauceNao搜图功能")
    boolean SauceNaoAPIEnable = false;

    @Control(group = "SauceNaoAPI", name = "Api Key", type = ControlType.Edit, tip = "SauceNao搜图功能的api key")
    String SauceNaoApiKey = "";

    @Control(group = "SauceNaoAPI", name = "数据库索引", type = ControlType.Edit_IntNum, tip = "指定搜索的数据库")
    int SauceNaoApiDb = 5;

    @Control(group = "SauceNaoAPI", name = "查看索引列表", type = ControlType.WebLink, tip = "跳转到数据库掩码列表")
    String SauceNaoApiDbList = "https://saucenao.com/tools/examples/api/index_details.txt";
    //pixiv
    @Control(group = "Pixiv", name = "启用", type = ControlType.CheckBox, tip = "启用p站相关功能")
    boolean pixivEnable = false;

    @Control(group = "Pixiv", name = "代理", type = ControlType.Edit_IntNum, tip = "选择代理方式")
    int pixivProxy = Illust.PROXY_PIXIVCAT;

    @Control(group = "Pixiv", name = "最大获取数量", type = ControlType.Edit_IntNum, tip = "设置一次命令最大图片的发送数量")
    int pixivGetMaxNum = 2;

    @Control(group = "Pixiv", name = "榜单最大获取数量", type = ControlType.Edit_IntNum, tip = "设置一次命令榜单最大图片的发送数量")
    int pixivRankGetMaxNum = 5;

    //Lolicon
    @Control(group = "Lolicon", name = "启用", type = ControlType.CheckBox, tip = "启用LoliconAPI")
    boolean loliconEnable = false;

    @Control(group = "Lolicon", name = "权限", type = ControlType.Authority_ComboBox, tip = "设置Lolicon相关命令的执行权限")
    int loliconPermissions = Permissions.NORMAL;

    @Control(group = "Lolicon", name = "代理", type = ControlType.Edit_Small_Plain, tip = "设置Lolicon的在线反代，为空则为默认")
    String loliconProxy = "";

    @Control(group = "Lolicon", name = "强制撤回", type = ControlType.CheckBox, tip = "不论是否R18，都进行撤回")
    boolean setuMastReCallEnable = false;

    @Control(group = "Lolicon", name = "最大发送数量", type = ControlType.Edit_IntNum, tip = "设置一次命令最大图片的发送数量")
    int setuSendMaxNum = 20;

    @Control(group = "Lolicon", name = "撤回时间(s)", type = ControlType.Edit_IntNum, tip = "撤回时间，大于110秒默认等于110秒")
    int setuReCallTime = 60;

    @Control(group = "Lolicon", name = "R18", type = ControlType.CheckBox, tip = "Lolicon API R18")
    boolean setuR18Enable = false;

    @Control(group = "Lolicon", name = "混合模式", type = ControlType.CheckBox, tip = "R18与非R8混合")
    boolean plainAndR18Enable = false;

    @Control(group = "Lolicon", name = "MD5发送模式", type = ControlType.CheckBox, tip = "有效加快发送速度且能避免mirai的5000ms异常，但可能造成发送失败")
    boolean loliconMD5ImageEnable = false;

    @Control(group = "Lolicon", name = "Lolicon", type = ControlType.WebLink, tip = "跳转到Lolicon")
    String jumpToLoliconButton = "https://api.lolicon.app/#/setu?id=telegram-bot/";
    //快捷搜索
    @Control(group = "快捷搜索", name = "Mc Wiki", type = ControlType.CheckBox, tip = "启用mc wiki相关快捷搜索")
    boolean mcWikiEnable = false;

    @Control(group = "快捷搜索", name = "百度", type = ControlType.CheckBox, tip = "启用百度相关快捷搜索")
    boolean baiduEnable = false;

    @Control(group = "快捷搜索", name = "PRTS", type = ControlType.CheckBox, tip = "启用PRTS相关快捷搜索")
    boolean PRTSEnable = false;
    //bili
    @Control(group = "哔哩哔哩", name = "BV号解析", type = ControlType.CheckBox, tip = "启用BV号解析功能")
    boolean biliBvExplainEnable = false;
    @Control(group = "哔哩哔哩", name = "用户更新关注", type = ControlType.CheckBox, tip = "在关注的B站UP主更新时发出提醒")
    boolean biliFollowEnable = false;
    //Mc 服务器
    @Control(group = "Mc服务器", name = "启用RCON", type = ControlType.CheckBox, tip = "启用rcon相关功能")
    boolean rconEnable = false;

    @Control(group = "Mc服务器", name = "Rcon选择", type = ControlType.SelectRcon, tip = "选择此群Rcon")
    RconConf selectGroupRcon = null;

    @Control(group = "Mc服务器", name = "启用Rcon命令", type = ControlType.CheckBox, tip = "启用后可使用QQ执行rcon命令")
    boolean RconCMDEnable = false;

    @Control(group = "Mc服务器", name = "启用state命令", type = ControlType.CheckBox, tip = "启用后可在QQ群获取服务器状态")
    boolean mcServerState = false;

    @Control(group = "Mc服务器", name = "服务器地址", type = ControlType.Edit_Small_Plain, tip = "默认使用的Mc服务器地址")
    String mcServerAddr = "";

    @Control(group = "Mc服务器", name = "服务器名称", type = ControlType.Edit_Small_Plain, tip = "默认使用的Mc服务器名称")
    String mcServerName = "";

    @Control(group = "青云客聊天Api", name = "启用", type = ControlType.CheckBox, tip = "启用青云客Api聊天功能")
    boolean qingYunKeApiChat = false;

    @Control(group = "抽签", name = "启用", type = ControlType.CheckBox, tip = "启用抽签功能")
    boolean drawEnable = false;

    @Control(group = "抽签", name = "数量", type = ControlType.Edit_IntNum, tip = "设置每天每人能抽的次数")
    int drawNum = 3;

    @Control(group = "消息转图片", name = "启用长文本转图片功能", type = ControlType.CheckBox, tip = "bot在发送长文本时将自动把消息转换为图片发送")
    boolean longMsgToImageEnable = false;

    @Control(group = "消息转图片", name = "最大字数", type = ControlType.Edit_IntNum, tip = "超过自动转图片")
    int longMsgToImageTextLengthMax = 300;

    @Control(group = "消息转图片", name = "图片数", type = ControlType.Edit_IntNum, tip = "超过自动转图片")
    int longMsgToImageImageNumMax = 3;

    @Control(group = "消息转图片", name = "行数", type = ControlType.Edit_IntNum, tip = "超过自动转图片")
    int longMsgToImageLineNumMax = 50;

    @Control(group = "消息转图片", name = "文本转图片字体", type = ControlType.Font_ComboBox, tip = "文本转图片时的文本字体")
    String longMsgToImageFont = "微软雅黑";

    @Control(group = "消息转图片", name = "文本转图片水印", type = ControlType.Edit_Small_Plain, tip = "文本转图片时的图片水印")
    String msgToImageWatermark = "SereinFish Bot";

    @Control(group = "消息转图片", name = "页边距", type = ControlType.Edit_IntNum, tip = "生成图片文字的页边距")
    int longMsgToImageMargins = 64;

    @Control(group = "消息转图片", name = "字体大小", type = ControlType.Edit_IntNum, tip = "生成字体的大小")
    int longMsgToImageFontSize = 36;

    public boolean isDataBaseEnable(){
        if(dataBaseConfig == null){
            return false;
        }

        try {
            if(DataBaseManager.getInstance().getDataBase(dataBaseConfig.getID()) == null){
                return false;
            }
        } catch (Exception e) {
            return false;
        } catch (IllegalModeException e) {
            return false;
        }

        return true;
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.groupDataPath, group + "/conf.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, GroupConf.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "群：" + group + "配置保存失败");
        }
    }
}
