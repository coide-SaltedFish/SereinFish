package sereinfish.bot.entity.lolicon.sf;

public class Request {
    String api = "";
    int num = 1;
    int isR18 = 0;
    String apikey = "";

    public Request(String api, int num, int isR18, String apikey) {
        this.api = api;
        this.num = num;
        this.isR18 = isR18;
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

        return url;
    }
}
