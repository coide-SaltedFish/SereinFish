package sereinfish.bot.entity.aSoul;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.myYuq.time.Time;
import java.util.Date;


/**
 * 枝网
 */
public class ASoulCnKi {

    /**
     * 生成报告
     * @param result
     * @return
     */
    public static String getReport(ASoulCnKiResult result){
        if (result.getCode() != 0){
            return "失败了捏：" + result.getMessage();
        }

        if (result.getData().getRelated().length <= 0){
            return "没找到捏,查询到总文字复制比为：" + (result.getData().getRate() * 100) + "%捏";
        }

        ASoulCnKiResult.Data.Related related = result.getData().getRelated()[0];

        String report = "枝网文本复制检测报告(简洁)\n" +
                "查重时间: " +
                Time.dateToString(new Date().getTime(), "yyyy/MM/dd HH:mm:ss") +
                "\n总文字复制比: " +
                (result.getData().getRate() * 100) +
                "%\n" +
                "相似小作文:  " +
                related.getReply_url() +
                "\n作者:" +
                related.getReply().getM_name() +
                "\n发表时间:" +
                Time.dateToString(related.getReply().getCtime() * 1000, "yyyy/MM/dd HH:mm:ss") +
                "\n" +
                "\n" +
                "查重结果仅作参考，请注意辨别是否为原创\n" +
                "数据来源：枝网查重(https://asoulcnki.asia/)";

        return report;
    }

    @AllArgsConstructor
    public static class ASoulRequest{
        private String text;
    }

    @Getter
    public class ASoulCnKiResult{
        private int code;
        private String message;
        private Data data;

        @Getter
        public class Data{
            private float rate;
            private long start_time;
            private long end_time;
            private Related[] related;

            @Getter
            public class Related{
                private float rate;
                private Reply reply;
                private String reply_url;
            }
        }
    }

    @Getter
    public class Reply{
        private String rpid;
        private int type_id;
        private String dynamic_id;
        private long mid;
        private long uid;
        private String oid;
        private long ctime;
        private String m_name;
        private String content;
        private int like_num;
        private String origin_rpid;
        private int similar_count;
        private int similar_like_sum;
    }
}
