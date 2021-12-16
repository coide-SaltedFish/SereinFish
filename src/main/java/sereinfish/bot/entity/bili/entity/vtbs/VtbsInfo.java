package sereinfish.bot.entity.bili.entity.vtbs;

import lombok.Getter;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

public class VtbsInfo {
    public static VtbsInfo INSTANCE;
    private String api;

    private Data[] datas = new Data[]{};

    private VtbsInfo() throws IOException {

        updateApi();

        if (api == null){
            SfLog.getInstance().e(this.getClass(), "无接口可用");
            return;
        }

        datas = MyYuQ.toClass(OkHttpUtils.getStr(api + "/v1/vtbs"), Data[].class);
    }

    public static void init() throws IOException {
        INSTANCE = new VtbsInfo();
    }

    private void updateApi() throws IOException {
        api = null;
        //获取api接口
        String[] apis = MyYuQ.toClass(OkHttpUtils.getStr("https://vtbs.musedash.moe/meta/cdn"), String[].class);
        //检测哪个api能用
        for (String a:apis){
            try {
                if (OkHttpUtils.getStr(a + "/meta/ping").equals("pong")){
                    api = a;
                    SfLog.getInstance().d(this.getClass(), "接口选定：" + a);
                    break;
                }
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(), "接口不可用：" + a + "\n" + e.getMessage());
            }
        }
        if (api == null){
            SfLog.getInstance().e(this.getClass(), "接口未选定：null");
        }
    }

    public void updateData() throws Exception {
        if (api == null){
            throw new Exception("无接口可用");
        }

        datas = MyYuQ.toClass(OkHttpUtils.getStr(api + "/v1/vtbs"), Data[].class);
    }

    public Data[] getDatas() {
        return datas;
    }

    @Getter
    public static class Data{
        long mid;
        String uuid;
    }
}
