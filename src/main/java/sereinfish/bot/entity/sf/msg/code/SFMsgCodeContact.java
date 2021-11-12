package sereinfish.bot.entity.sf.msg.code;

import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import lombok.Data;
import lombok.NonNull;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.calendar.holiday.HolidayManager;
import sereinfish.bot.entity.calendar.holiday.Holidays;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Data
public class SFMsgCodeContact {
    HolidayManager holidayManager = MyYuQ.getHolidayManager();

    Map<String,Object> map = new HashMap<>();

    Contact sender;
    Contact source;

    public SFMsgCodeContact(Contact contact){
        //bot
        map.put("BOT_NAME", MyYuQ.getBotName());
        map.put("BOT_QQ_NAME", MyYuQ.getYuQ().getBotInfo().getName());
        map.put("BOT_ID", MyYuQ.getYuQ().getBotId());

        if (contact != null){
            try {
                Image image = contact.uploadImage(CacheManager.getMemberHeadImageFile(MyYuQ.getYuQ().getBotId()));
                map.put("BOT_AVATAR", image);
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(), "bot 头像对象注入失败", e);
            }
        }

        //时间相关
        map.put("YEAR", Calendar.getInstance().get(Calendar.YEAR));

        map.put("MONTH", Calendar.getInstance().get(Calendar.MONTH) + 1);

        map.put("DAY_OF_YEAR", Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        map.put("DAY_OF_MONTH", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


        //一周第一天是否为星期天
        boolean isFirstSunday = (Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY);
        //获取周几
        int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        //若一周第一天为星期天，则-1
        if(isFirstSunday){
            weekDay = weekDay - 1;
            if(weekDay == 0){
                weekDay = 7;
            }
        }
        map.put("DAY_OF_WEEK", weekDay);

        map.put("HOUR", Calendar.getInstance().get(Calendar.HOUR));
        map.put("HOUR_OF_DAY", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        map.put("MINUTE", Calendar.getInstance().get(Calendar.MINUTE));

        //节假日数据注入
        for (Holidays.Holiday.List list:holidayManager.getYearHolidays(Calendar.getInstance().get(Calendar.YEAR))){
            map.put("今年" + list.getName(), list.getDayOfYear());
            map.put("今年" + list.getName() + "_DATE", list.getDate().getTime());
        }

        for (Holidays.Holiday.List list:holidayManager.getYearHolidays(Calendar.getInstance().get(Calendar.YEAR) + 1)){
            map.put("明年" + list.getName(), list.getDayOfLastYear());
            map.put("明年" + list.getName() + "_DATE", list.getDate().getTime());
        }

    }

    public SFMsgCodeContact(BotActionContext botActionContext) {
        this(botActionContext.getSource());
        this.botActionContext = botActionContext;

        sender = botActionContext.getSender();
        source = botActionContext.getSource();
    }

    public SFMsgCodeContact(Contact sender, Contact source) {
        this(source);
        this.sender = sender;
        this.source = source;
    }

    BotActionContext botActionContext;
    Message reMessage = new Message();
    String parameter;

    public Message getReMessage(){
        Message message = new Message();
        message.setRecallDelay(reMessage.getRecallDelay());
        return message;
    }

    public void save(String key, Object o){
        map.put(key, o);
    }

    public Object get(String key){
        return map.get(key);
    }

    public boolean containsKey(Object key){
        return map.containsKey(key);
    }

    public Object getOrDefault(String key, Object defaultValue){
        return map.getOrDefault(key, defaultValue);
    }

    public Group getGroup(){
        if (source instanceof Group){
            return (Group) source;
        }
        return null;
    }
}
