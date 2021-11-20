package sereinfish.bot.cache.day;

import lombok.Data;
import sereinfish.bot.entity.random.DrawConf;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 保存仅生效一天的数据
 */
public class OnlyDay {
    Map<Long,Map<String,Data>> drawConfGroupMap = new HashMap<>();

    public Data get(long group, String id){
        if (!drawConfGroupMap.containsKey(group)){
            Map<String, Data> dateMap = new HashMap<>();
            dateMap.put(id, new Data());
            drawConfGroupMap.put(group, dateMap);
        }

        if (!drawConfGroupMap.get(group).containsKey(id)){
            drawConfGroupMap.get(group).put(id, new Data());
        }
        return drawConfGroupMap.get(group).get(id);
    }

    public void add(long group, String id, Object o){
        if (!drawConfGroupMap.containsKey(group)){
            Map<String, Data> dateMap = new HashMap<>();
            dateMap.put(id, new Data(o));
            drawConfGroupMap.put(group, dateMap);
        }
        save();
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.configPath, "/OnlyDayConf.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, OnlyDay.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"OnlyDayConf配置保存失败");
        }
    }

    /**
     * 得到配置
     * @return
     * @throws IOException
     */
    public static OnlyDay read() throws IOException {
        File confFile = new File(FileHandle.configPath, "/OnlyDayConf.json");
        if (confFile.exists() && confFile.isFile()){
            String json = FileHandle.read(confFile);
            return MyYuQ.toClass(json, OnlyDay.class);
        }else {
            OnlyDay onlyDay = new OnlyDay();
            onlyDay.save();
            return onlyDay;
        }
    }

    public class Data{
        private int year = Calendar.getInstance().get(Calendar.YEAR);
        private int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        private String content = "";//内容

        public Data() {
        }

        public Data(String content) {
            this.content = content;
        }

        public Data(Object o) {
            setContent(o);
        }

        public void setContent(Object o){
            update();
            content = MyYuQ.toJson(o, o.getClass());
        }

        public <T>T getContent(Type type){
            update();
            return MyYuQ.toClass(content, type);
        }

        public String getContent(){
            update();
            return content;
        }

        /**
         * 更新
         * @return
         */
        public void update(){
            if (year != Calendar.getInstance().get(Calendar.YEAR)){
                year = Calendar.getInstance().get(Calendar.YEAR);
                content = "";
            }

            if (dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                content = "";
            }
        }
    }
}
