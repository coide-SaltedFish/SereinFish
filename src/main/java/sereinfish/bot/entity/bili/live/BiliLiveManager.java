package sereinfish.bot.entity.bili.live;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bili.live.entity.FollowConf;
import sereinfish.bot.entity.bili.live.entity.info.UserInfo;
import sereinfish.bot.entity.bili.live.entity.live.LiveRoom;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
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
public class BiliLiveManager {
    private boolean isConfInit = false;//配置是否初始化

    public static BiliLiveManager manager;

    private BiliLiveManager(){
        initConf();
    }

    public static BiliLiveManager getInstance(){
        if (manager == null){
            throw new NullPointerException("哔哩哔哩直播相关功能管理器尚未初始化");
        }
        return manager;
    }

    public static BiliLiveManager init(){
        manager = new BiliLiveManager();
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
     * 更新查询
     */
    public void check(){
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
                SfLog.getInstance().e(BiliLiveManager.class, "配置读取失败", e);
            }

        }
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
        messageLineQ.textLine(userInfo.getData().getLive_room().getTitle());
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
        messageLineQ.textLine(userInfo.getData().getName() + " 下播了");
        messageLineQ.imageByUrl(userInfo.getData().getLive_room().getCover());
        if (userInfo.getData().getLive_room().getRoundStatus() == LiveRoom.ROUND_STATUS_OPEN){
            messageLineQ.textLine(userInfo.getData().getLive_room().getTitle());
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
