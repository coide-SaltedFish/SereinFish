package sereinfish.bot.entity.lolicon.sf;

import sereinfish.bot.mlog.SfLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Request {
    String api = "";
    int num = 1;
    int isR18 = 0;
    String keyWord = "";
    String apikey = "";

    public Request(String api, int num, int isR18, String keyWord, String apikey) {
        this.api = api;
        this.num = num;
        this.isR18 = isR18;
        this.keyWord = keyWord;
        this.apikey = apikey;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getIsR18() {
        return isR18;
    }

    public void setIsR18(int isR18) {
        this.isR18 = isR18;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getUrl(){
        String url = api;
        url += "?";
        url += "num=" + num;
        url += "&isR18=" + isR18;
        if (keyWord != null && !keyWord.trim().equals("")){
            try {
                url += "&keyWord=" + URLEncoder.encode(keyWord,"utf-8");
            } catch (UnsupportedEncodingException e) {
                SfLog.getInstance().e(this.getClass(),e);
            }
        }

        return url;
    }
}
