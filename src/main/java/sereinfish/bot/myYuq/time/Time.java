package sereinfish.bot.myYuq.time;

import sereinfish.bot.mlog.SfLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time{
	public static final String LOG_TIME="yyyy-MM-dd HH:mm:ss:mss";
	public static final String DATE_FORMAT="yyyy-MM-dd";
	public static final String RUN_TIME = "dd:HH:mm:ss";
	public static final String DAY_TIME = "HH:mm:ss";
	
	
	public static String dateToString(Date time,String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat (style);
		return simpleDateFormat.format(time);
	}

	public static String dateToString(long time,String style) {
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat (style);
		return simpleDateFormat.format(new Date(time));
	}
	
	
	public static Date stringToDate(String time,String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat (style);
		Date date = null;
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			SfLog.getInstance().e(Time.class,"时间转换错误,格式应为："+style,e);
		}
		return date;
	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
	 *
	 * @param nowTime 当前时间
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime()
				|| nowTime.getTime() == endTime.getTime()) {
			return true;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}
}