package sereinfish.bot.entity.calendar.holiday;

import lombok.Data;
import sereinfish.bot.myYuq.time.Time;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

@Data
public class Holidays {
    //https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=%E6%B3%95%E5%AE%9A%E8%8A%82%E5%81%87%E6%97%A5&co=&resource_id=39042&t=1617089118959&ie=utf8&oe=gbk&cb=op_aladdin_callback&format=json&tn=wisetpl&cb=jQuery110203576901702188473_1617089118772&_=1617089118773

    int status;
    long t;
    String set_cache_time;
    HolidaysData[] data;

    @Data
    public class HolidaysData{
        String ExtendedLocation;
        String OriginQuery;
        int SiteId;
        int StdStg;
        int StdStl;
        long _select_time;
        long _update_time;
        int _version;
        String appinfo;
        String cambrian_appid;
        int disp_type;
        String fetchkey;

        Holiday[] holiday;
    }

    @Data
    public class Holiday{
        int year;
        List[] list;

        @Data
        public class List{
            String date;
            String name;

            /**
             * 得到在当年的第几天
             * @return
             */
            public Date getDate(){
                //1900-1-30
                return Time.stringToDate(this.date, "yyyy-MM-dd");
            }

            /**
             * 得到在当年的第几天
             * @return
             */
            public int getDayOfYear(){
                //1900-1-30
                Date date = Time.stringToDate(this.date, "yyyy-MM-dd");
                Calendar cal=Calendar.getInstance();
                cal.setTime(date);
                return cal.get(Calendar.DAY_OF_YEAR);
            }

            /**
             * 得到在去年的第几天
             * @return
             */
            public int getDayOfLastYear(){
                //1900-1-30
                Date date = Time.stringToDate(this.date, "yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(calendar.get(Calendar.YEAR) - 1, 0, 1);

                return Time.differentDaysByMillisecond(date, calendar.getTime());
            }
        }
    }
}
