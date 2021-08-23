package sereinfish.bot.entity.lolicon;

import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.mlog.SfLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

@Getter
public class Lolicon {
    public static final int NO_R18 = 0;
    public static final int R18 = 1;
    public static final int PLAIN_AND_R18 = 2;

//    public static final int ERR = -1;
//    public static final int SUCCESS = 0;
//    public static final int APIKEY_ERR = 401;
//    public static final int QUOTA_ERR = 429;

    String error;
    ArrayList<Setu> data;

    @Getter
    public static class Setu{
        int pid;
        int p;
        int uid;
        String title;
        String author;
        boolean r18;
        int width;
        int height;
        String[] tags;
        String ext;
        long uploadDate;
        Object urls;


        public Setu() {
        }

        public String getUrl(){
            String url = urls.toString().substring(1, urls.toString().length() - 1);
            return url.toString().split("=")[1];
        }
    }

    @Getter
    @Setter
    public static class Request{
        int r18 = 0;
        int num = 1;//一次返回的数量
        int[] uids;//作者uid
        String keyword;
        String proxy;//是否使用原图连接

        public Request(int r18, int num, int[] uids, String keyword, String proxy) {
            this.r18 = r18;
            if (num < 1){
                this.num = 1;
            }

            if (num > 100){
                num = 100;
            }
            this.num = num;
            this.uids = uids;
            this.keyword = keyword;
            this.proxy = proxy;
        }

        /**
         * 生成并得到url
         * @return
         */
        public String getUrl(){
            String api = "https://api.lolicon.app/setu/v2?";

            api += "r18=" + r18;

            api += "&num=" + num;

            if (proxy != null && !proxy.trim().equals("")){
                api += "&proxy=" + proxy;
            }

            if (uids != null){
                for (int uid:uids){
                    api += "&uid=" + uid;
                }
            }

            if (keyword != null){
                if (keyword.contains("#")){
                    String[] tags = keyword.split("#");
                    for (String tag:tags){
                        if (!tag.equals("")){
                            try {
                                api += "&tag=" + URLEncoder.encode(tag,"utf-8");
                            } catch (UnsupportedEncodingException e) {
                                SfLog.getInstance().e(this.getClass(),e);
                            }
                        }
                    }
                }else {
                    if (!keyword.equals("")){
                        try {
                            api += "&keyword=" + URLEncoder.encode(keyword,"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            SfLog.getInstance().e(this.getClass(),e);
                        }
                    }
                }

//                String orTags = "";
//                for (int i = 0; i < tags.length; i++){
//                    if (i != 0){
//                        orTags += "|";
//                        orTags += tags[i];
//                    }
//                }
//
//                api += "&tag=" + orTags;
            }


            return api;
        }
    }
}
