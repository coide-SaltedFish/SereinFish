package sereinfish.bot.myYuq.time;

import sereinfish.bot.mlog.SfLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time{
	public static final String LOG_TIME="yyyy-MM-dd HH:mm:ss:mss";
	public static final String DATE_FORMAT="yyyy-MM-dd";
	public static final String RUN_TIME = "dd:HH:mm:ss";
	
	
	public static String dateToString(Date time,String style) {
		Date date = new Date( );
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat (style);
		return simpleDateFormat.format(time);
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
}