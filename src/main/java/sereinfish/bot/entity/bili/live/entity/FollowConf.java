package sereinfish.bot.entity.bili.live.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.entity.bili.live.BiliManager;
import sereinfish.bot.entity.bili.live.entity.info.UserInfo;
import sereinfish.bot.entity.bili.live.entity.live.LiveRoom;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Getter
public class FollowConf {
    private long group;
    private ArrayList<BiliUser> follows = new ArrayList<>();//关注用户列表

    public FollowConf(long group) {
        this.group = group;
    }

    /**
     * 添加
     * @param mid
     */
    public String add(long mid) throws IOException {
        for (BiliUser biliUser:follows){
            if (biliUser.getMid() == mid){
                return "用户已存在";
            }
        }

        BiliUser biliUser = new BiliUser(mid, BiliUser.TYPE_LIVE, BiliUser.LIVE_CLOSE, new Date().getTime());
        //更新配置
        UserInfo userInfo = BiliManager.getUserInfo(mid);
        if (userInfo.getData().getLive_room().getLiveStatus() == LiveRoom.LIVE_STATUS_OPEN){
            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_ENABLE);
        }else if (userInfo.getData().getLive_room().getRoundStatus() == LiveRoom.ROUND_STATUS_OPEN){
            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_ROUND_ENABLE);
        }else {
            biliUser.setLastLiveState(FollowConf.BiliUser.LIVE_CLOSE);
        }
        follows.add(biliUser);
        save();
        SfLog.getInstance().d(this.getClass(),"Bili关注配置初始化完成");

        return "添加成功";
    }

    /**
     * 取消关注
     * @param mid
     * @return
     */
    public String delete(long mid) throws IOException {
        for (int i = 0; i < follows.size(); i++){
            BiliUser biliUser = follows.get(i);
            if (biliUser.getMid() == mid){
                UserInfo userInfo = BiliManager.getUserInfo(mid);
                follows.remove(i);

                return "已取消关注:\n" + userInfo.getData().getName();
            }
        }

        return "用户未关注";
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.groupDataPath, group + "/BiliFollowConf.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, FollowConf.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "群：" + group + "B站关注配置保存失败");
        }
    }

    /**
     * 得到配置
     * @param group
     * @return
     */
    public static FollowConf get(long group) throws IOException {
        File confFile = new File(FileHandle.groupDataPath, group + "/BiliFollowConf.json");

        if (confFile.exists() && confFile.isFile()){
            String json = FileHandle.read(confFile);
            return MyYuQ.toClass(json, FollowConf.class);
        }else {
            FollowConf followConf = new FollowConf(group);
            followConf.save();
            return followConf;
        }
    }

    /**
     * B站用户关注对象
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public class BiliUser{
        public static final int TYPE_ALL = 0;//所有
        public static final int TYPE_LIVE = 1;//直播
        public static final int TYPE_DYNAMIC = 2;//动态
        public static final int TYPE_VIDEO = 3;//视频

        //直播间状态
        public static final int LIVE_ENABLE = 0;//直播
        public static final int LIVE_ROUND_ENABLE = 1;//轮播
        public static final int LIVE_CLOSE = 2;//未直播

        long mid;//用户ID
        int type;//类型

        int lastLiveState;//上次查询直播状态

        long lastVideosTime;//上次查询视频更新的时间戳
    }
}
