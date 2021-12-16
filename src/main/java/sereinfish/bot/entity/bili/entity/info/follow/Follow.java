package sereinfish.bot.entity.bili.entity.info.follow;

import lombok.Getter;
import sereinfish.bot.entity.bili.entity.info.Data;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 获取用户关注列表
 */
@Getter
public class Follow {
    private FollowInfo followInfo;
    private ArrayList<sereinfish.bot.entity.bili.entity.info.Data> list = new ArrayList<>();

    public Follow(long mid) throws Exception {
        //先获取关注信息
        followInfo = MyYuQ.toClass(OkHttpUtils.getStr("https://api.bilibili.com/x/relation/stat?vmid=" + mid), FollowInfo.class);
        //获取所有关注列表
        int pn = (followInfo.getData().getFollowing() / 20) + 1;//获取页数

        for (int i = 0; i < pn; i++){
            Data data = MyYuQ.toClass(OkHttpUtils.getStr("https://api.bilibili.com/x/relation/followings?vmid=" + mid + "&pn=" + i + "&ps=20&order=desc&jsonp=jsonp&order_type=attention"), Data.class);
            if (data.getCode() == 22115){
                throw new Exception(data.getMessage() + "：" + data.getCode());
            }
            if (data.getData() != null)
                list.addAll(Arrays.asList(data.getData().getList()));
        }
    }

    @Getter
    public class FollowInfo{
        int code;
        String message;
        int ttl;
        Data data;

        @Getter
        public class Data{
            long mid;
            int following;
            int whisper;
            int black;
            int follower;
        }
    }

    @Getter
    public class Data{
        int code;
        String message;
        int ttl;

        List data;

        @Getter
        public class List{
            sereinfish.bot.entity.bili.entity.info.Data[] list;
        }
    }
}
