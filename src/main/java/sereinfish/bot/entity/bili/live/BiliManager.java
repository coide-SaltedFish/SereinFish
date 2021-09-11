package sereinfish.bot.entity.bili.live;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bili.live.entity.FollowConf;
import sereinfish.bot.entity.bili.live.entity.info.Contribution;
import sereinfish.bot.entity.bili.live.entity.info.UserInfo;
import sereinfish.bot.entity.bili.live.entity.live.LiveRoom;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

/**
 * 哔哩哔哩直播相关功能管理器
 *
 * 通过uid获取直播间id
 * https://api.live.bilibili.com/room/v2/Room/room_id_by_uid?uid=1234
 *
 *
 */
public class BiliManager {
    private boolean isConfInit = false;//配置是否初始化

    public static BiliManager manager;

    private BiliManager(){
        initConf();
    }

    public static BiliManager getInstance(){
        if (manager == null){
            throw new NullPointerException("哔哩哔哩直播相关功能管理器尚未初始化");
        }
        return manager;
    }

    public static BiliManager init(){
        manager = new BiliManager();
        return manager;
    }

    /**
     * 得到用户信息
     * @param uid
     * @return
     * @throws IOException
     */
    public static UserInfo getUserInfo(long uid) throws IOException {
        String api = "https://api.bilibili.com/x/space/acc/info?mid=" + uid + "&jsonp=jsonp";
        String json = OkHttpUtils.getStr(api);
        UserInfo userInfo = MyYuQ.toClass(json, UserInfo.class);

        return userInfo;
    }

    /**
     * 得到用户最新发布的视频
     * @param mid
     * @throws IOException
     */
    public static Contribution getUserVideos(long mid) throws IOException {
        String api = "http://api.bilibili.com/x/space/arc/search?mid=" + mid + "&order=pubdate&pn=1&ps=1";
        String json = OkHttpUtils.getStr(api);
        Contribution contribution = MyYuQ.toClass(json, Contribution.class);
        return contribution;
    }

    /**
     * 更新查询
     */
    public void check(){
        liveCheck();
        contributionCheck();
    }

    /**
     * 新投稿检测
     */
    private void contributionCheck(){
        for (Group group:MyYuQ.getGroups()){
            GroupConf groupConf = ConfManager.getInstance().get(group.getId());
            try {
                FollowConf followConf = FollowConf.get(group.getId());
                if (groupConf.isBiliFollowEnable()){
                    //读取配置列表
                    for (int i = 0; i < followConf.getFollows().size(); i++) {
                        FollowConf.BiliUser biliUser = followConf.getFollows().get(i);
                        //获取最新视频
                        Contribution contribution = getUserVideos(biliUser.getMid());
                        if (contribution.getCode() == Contribution.SUCCESS){
                            Contribution.Data.List.VList vList[] = contribution.getData().getList().getVlist();
                            if (vList != null && vList.length > 0){
                                //如果得到的时间大于之前查询的时间
                                if (vList[0].getCreated() > biliUser.getLastVideosTime()){
                                    if (isConfInit){
                                        //发送更新消息
                                        group.sendMessage(getVideosUpdateTip(vList[0]));
                                    }
                                    //更新配置文件
                                    biliUser.setLastVideosTime(vList[0].getCreated());
                                }
                            }else {
                                SfLog.getInstance().w(this.getClass(), "视频列表为空："
                                        + biliUser.getMid()
                                        + ">>"
                                        + vList);
                            }
                        }else {
                            SfLog.getInstance().e(this.getClass(), "视频更新列表请求失败:"
                                    + biliUser.getMid() + ">>"
                                    + contribution.getCode()
                                    + ":"
                                    + contribution.getMessage());
                        }
                    }
                }
                followConf.save();
            } catch (IOException e) {
                SfLog.getInstance().e(BiliManager.class, "配置读取失败", e);
            }
        }
    }

    /**
     * 直播检测
     */
    private void liveCheck(){
        for (Group group:MyYuQ.getGroups()){
            GroupConf groupConf = ConfManager.getInstance().get(group.getId());
            try {
                FollowConf followConf = FollowConf.get(group.getId());

                //检查功能是否启用
                if (groupConf.isBiliFollowEnable()){

                    //读取配置列表
                    for (int i = 0; i < followConf.getFollows().size(); i++){
                        FollowConf.BiliUser biliUser = followConf.getFollows().get(i);
                        //获取直播间状态
                        UserInfo userInfo = getUserInfo(biliUser.getMid());

                        if (isConfInit){//初始化后进行
                            //判断直播状态是否改变
                            if (biliUser.getLastLiveState() == FollowConf.BiliUser.LIVE_ENABLE){//如果之前在直播
                                if (userInfo.getData().getLive_room().getLiveStatus() == LiveRoom.LIVE_STATUS_CLOSE){
                                    //发送下播提醒
                                    group.sendMessage(getLiveCloseTip(userInfo));
                                }
                            }else if (biliUser.getLastLiveState() == FollowConf.BiliUser.LIVE_CLOSE
                                    || biliUser.getLastLiveState() == FollowConf.BiliUser.LIVE_ROUND_ENABLE){//如果之前没直播或在轮播

                                if (userInfo.getData().getLive_room().getLiveStatus() == LiveRoom.LIVE_STATUS_OPEN){
                                    //发送上播提醒
                                    group.sendMessage(getLiveOpenTip(userInfo));
                                }

                            }
                        }
                        //更新配置
                        if (userInfo.getData().getLive_room().getLiveStatus() == LiveRoom.LIVE_STATUS_OPEN){
                            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_ENABLE);
                        }else if (userInfo.getData().getLive_room().getRoundStatus() == LiveRoom.ROUND_STATUS_OPEN){
                            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_ROUND_ENABLE);
                        }else {
                            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_CLOSE);
                        }
                    }
                }

                followConf.save();//配置保存
            } catch (IOException e) {
                SfLog.getInstance().e(BiliManager.class, "配置读取失败", e);
            }
        }
    }

    /**
     * 得到视频更新提示
     * @param vList
     * @return
     */
    public Message getVideosUpdateTip(Contribution.Data.List.VList vList){
        MessageLineQ messageLineQ = new Message().lineQ();

        messageLineQ.textLine("UP主：" + vList.getAuthor());//up
        if (vList.getIs_union_video() == 1){
            messageLineQ.textLine("发布了新的投稿视频[合作视频]");
        }else {
            messageLineQ.textLine("发布了新的投稿视频");
        }

        messageLineQ.imageByUrl(vList.getPic());//封面
        messageLineQ.textLine("投稿时间：" + Time.dateToString(vList.getCreated() * 1000, "yyyy-MM-dd HH:mm:ss"));

        messageLineQ.textLine("标题：");
        messageLineQ.textLine(vList.getTitle());
        messageLineQ.textLine("描述：");
        String desc = vList.getDescription();
        int maxLen = 40;
        if (desc.length() > maxLen){
            desc = desc.substring(0, maxLen) + "...\n";
        }
        while(!desc.equals(desc.replace("\n\n","\n"))){
            desc = desc.replace("\n\n","\n");
        }

        if (!desc.endsWith("\n")){
            desc += "\n";
        }
        messageLineQ.textLine(desc);
        messageLineQ.textLine("视频长度：" + vList.getLength());
        messageLineQ.textLine("链接：");
        messageLineQ.text("https://www.bilibili.com/video/" + vList.getBvid());

        return messageLineQ.getMessage();
    }

    /**
     * 上播提醒
     * @return
     */
    private Message getLiveOpenTip(UserInfo userInfo){
        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.textLine(userInfo.getData().getName());
        messageLineQ.textLine("开播辣！！");
        messageLineQ.imageByUrl(userInfo.getData().getLive_room().getCover());
        messageLineQ.textLine("标题：" + userInfo.getData().getLive_room().getTitle());
        messageLineQ.textLine("点击链接立刻进行围观：");
        messageLineQ.text(userInfo.getData().getLive_room().getUrl());
        return messageLineQ.getMessage();
    }

    /**
     * 下播提醒
     * @return
     */
    private Message getLiveCloseTip(UserInfo userInfo){
        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.textLine(userInfo.getData().getName());
        messageLineQ.textLine("下播了");
        messageLineQ.imageByUrl(userInfo.getData().getLive_room().getCover());
        if (userInfo.getData().getLive_room().getRoundStatus() == LiveRoom.ROUND_STATUS_OPEN){
            messageLineQ.textLine("标题：" + userInfo.getData().getLive_room().getTitle());
            messageLineQ.textLine("现在正在进行轮播:");
            messageLineQ.text(userInfo.getData().getLive_room().getUrl());
        }else {
            messageLineQ.text(userInfo.getData().getLive_room().getTitle());
        }
        return messageLineQ.getMessage();
    }

    /**
     * 配置初始化
     */
    private void initConf(){
        check();

        isConfInit = true;
    }
}
