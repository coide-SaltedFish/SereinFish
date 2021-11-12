package sereinfish.bot.entity.calendar.holiday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import javax.inject.Inject;
import java.io.IOException;

public class HolidayManager {
    private Logger logger = LoggerFactory.getLogger(HolidayManager.class);
    private String api = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=%E6%B3%95%E5%AE%9A%E8%8A%82%E5%81%87%E6%97%A5&co=&resource_id=39042&t=1617089118959&ie=utf8&oe=gbk&cb=op_aladdin_callback&format=json&tn=wisetpl&cb=jQuery110203576901702188473_1617089118772&_=1617089118773";

    private Holidays holidays;

//    public HolidayManager(){
//        //获取数据
//        try {
//            init();
//            SfLog.getInstance().d(this.getClass(), "节假日数据初始化完成");
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(), "节假日数据获取失败", e);
//        }
//    }

    /**
     * 数据初始化
     */
    @Inject
    public void init() {
        try {
            String dataStr = OkHttpUtils.getStr(api);
            String json = dataStr.substring(dataStr.indexOf("(") + 1, dataStr.lastIndexOf(")"));
            holidays = MyYuQ.toClass(json, Holidays.class);
            logger.info("节假日数据初始化完成");
        } catch (Exception e) {
            logger.error("节假日数据获取失败", e);
        }

    }

    /**
     * 得到某一年的所有节假日
     * @param year
     * @return
     */
    public Holidays.Holiday.List[] getYearHolidays(int year){
        if (holidays == null){
            return new Holidays.Holiday.List[]{};
        }

        for (Holidays.HolidaysData data:holidays.getData()){
            for (Holidays.Holiday holiday:data.getHoliday()){
                if (holiday.getYear() == year){
                    return holiday.getList();
                }
            }
        }
        return new Holidays.Holiday.List[]{};
    }


}
