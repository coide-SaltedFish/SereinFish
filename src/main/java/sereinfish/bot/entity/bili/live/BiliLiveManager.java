package sereinfish.bot.entity.bili.live;

import sereinfish.bot.mlog.SfLog;

/**
 * 哔哩哔哩直播相关功能管理器
 */
public class BiliLiveManager {
    private static BiliLiveManager manager;//单例
    private BiliLiveManager(){

    }

    public static BiliLiveManager init(){
        manager = new BiliLiveManager();
        return manager;
    }

    public static BiliLiveManager getInstance(){
        if (manager == null){
            throw new NullPointerException("哔哩哔哩直播间管理器未初始化");
        }
        return manager;
    }

    /**
     * 常态化运行子线程
     * 不断查询数据
     * 半分钟进行一次
     */
    private void timerRun(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){

                    sleep(1000 * 30);//休眠时间
                }
            }

            public void sleep(int ms){
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    SfLog.getInstance().e(this.getClass(), e);
                }
            }
        }).start();
    }


}
