package sereinfish.bot.entity.bili.live.entity.info;

import lombok.Getter;

import java.util.Map;

/**
 * 投稿
 */
@Getter
public class Contribution {
    //0：成功
    //-400：请求错误
    //-412：请求被拦截
    public static final int SUCCESS = 0;
    public static final int ERROR = -400;
    public static final int INTERCEPT = -412;

    int code;
    String message;//错误信息
    int ttl;
    Data data;

    @Getter
    public class Data{
        List list;
        Page page;
        EpisodicButton episodic_button;

        @Getter
        public class Page{
            int count;//总计稿件数
            int pn;
            int ps;
        }

        @Getter
        public class EpisodicButton{
            String text;//按钮文字
            String uri;//播放页uri
        }

        @Getter
        public class List{
            Map<String, TId> tList;
            VList vlist[];

            @Getter
            public class TId{
                int count;
                int name;
                int tid;
            }

            @Getter
            public class VList{
                long aid;
                String author;
                String bvid;
                int comment;
                String copyright;
                long created;//投稿时间
                String description;//视频简介
                boolean hide_click;
                int is_pay;
                int is_union_video;//是否是合作视频
                String length;//视频长度
                long mid;
                String pic;//视频封面
                int play;//视频播放次数
                int review;
                String subtitle;
                String title;//视频标题
                int typeid;//分区id
                int video_review;//弹幕数
            }
        }
    }
}
