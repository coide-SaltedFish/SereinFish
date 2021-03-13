package sereinfish.bot.entity.lolicon;

import java.util.ArrayList;

public class Lolicon {
    public static final int NO_R18 = 0;
    public static final int R18 = 1;
    public static final int PLAIN_AND_R18 = 2;

    public static final int ERR = -1;
    public static final int SUCCESS = 0;
    public static final int APIKEY_ERR = 401;
    public static final int QUOTA_ERR = 429;

    int code;
    String msg;
    int quota;
    int quota_min_ttl;
    int count;
    ArrayList<Setu> data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getQuota() {
        return quota;
    }

    public int getQuota_min_ttl() {
        return quota_min_ttl;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Setu> getData() {
        return data;
    }

    public static class Setu{
        int pid;
        int p;
        int uid;
        String title;
        String author;
        String url;
        boolean r18;
        int width;
        int height;
        String[] tags;

        public Setu() {
        }

        public Setu(boolean r18) {
            this.r18 = r18;
        }

        public int getPid() {
            return pid;
        }

        public int getP() {
            return p;
        }

        public int getUid() {
            return uid;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getUrl() {
            return url;
        }

        public boolean isR18() {
            return r18;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String[] getTags() {
            return tags;
        }
    }

    public static class Request{
        String apikey = "";
        int r18 = 0;
        String keyword;
        int num = 1;//一次返回的数量
        String proxy;//是否使用原图连接
        boolean size1200 = true;//是否使用缩略图

        public Request(String apikey, int r18, String keyword, int num, String proxy, boolean size1200) {
            this.apikey = apikey;
            this.r18 = r18;
            this.keyword = keyword;
            this.num = num;
            this.proxy = proxy;
            this.size1200 = size1200;
        }

        public String getApikey() {
            return apikey;
        }

        public void setApikey(String apikey) {
            this.apikey = apikey;
        }

        public int getR18() {
            return r18;
        }

        public void setR18(int r18) {
            this.r18 = r18;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public boolean isSize1200() {
            return size1200;
        }

        public void setSize1200(boolean size1200) {
            this.size1200 = size1200;
        }

        /**
         * 生成并得到url
         * @return
         */
        public String getUrl(){
            boolean flag = false;
            String api = "https://api.lolicon.app/setu/?";

            api += "r18=" + r18;
            if (apikey != null && !apikey.trim().equals("")){
                api += "&apikey=" + apikey;
                flag = true;
            }

            if (keyword != null && !keyword.trim().equals("")){
                api += "&keyword=" + keyword;
            }

            api += "&num=" + num;

            if (proxy != null && !proxy.trim().equals("")){
                api += "&proxy=" + proxy;
            }

            api += "&size1200=" + size1200;

            return api;
        }
    }
}
