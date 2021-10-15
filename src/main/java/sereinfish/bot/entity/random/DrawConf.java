package sereinfish.bot.entity.random;

import sereinfish.bot.file.FileHandle;
import sereinfish.bot.job.conf.JobConf;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DrawConf {
    Map<Long, DrawConfGroup> drawConfGroupMap = new HashMap<>();

    public DrawConfGroup get(long group){
        if (!drawConfGroupMap.containsKey(group)){
            drawConfGroupMap.put(group, new DrawConfGroup());
        }
        return drawConfGroupMap.get(group);
    }

    public boolean isDoDraw(long group, long member, int num){
        if (!drawConfGroupMap.containsKey(group)){
            drawConfGroupMap.put(group, new DrawConfGroup());
        }

        boolean flag = drawConfGroupMap.get(group).get(member).isDoDraw(num);
        save();
        return flag;
    }

    public void add(long group, long member, String draw){
        if (!drawConfGroupMap.containsKey(group)){
            drawConfGroupMap.put(group, new DrawConfGroup());
        }

        drawConfGroupMap.get(group).add(member, draw);
        save();
    }

    public String getDraw(long group, long member){
        if (!drawConfGroupMap.containsKey(group)){
            drawConfGroupMap.put(group, new DrawConfGroup());
        }

        return drawConfGroupMap.get(group).getDraw(member);
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.configPath, "/drawConf.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, DrawConf.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),"Draw配置保存失败");
        }
    }

    /**
     * 得到配置
     * @return
     * @throws IOException
     */
    public static DrawConf read() throws IOException {
        File confFile = new File(FileHandle.configPath, "/drawConf.json");
        if (confFile.exists() && confFile.isFile()){
            String json = FileHandle.read(confFile);
            return MyYuQ.toClass(json, DrawConf.class);
        }else {
            DrawConf drawConf = new DrawConf();
            drawConf.save();
            return drawConf;
        }
    }

    public class DrawConfGroup{
        private Map<Long, DrawConfMember> drawConfMemberMap = new HashMap<>();

        public void add(long member, String draw){
            DrawConfMember drawConfMember = get(member);
            drawConfMember.setNewDraw(draw);
        }

        public String getDraw(long member){
            if (!drawConfMemberMap.containsKey(member)){
                drawConfMemberMap.put(member, new DrawConfMember());
            }
            return drawConfMemberMap.get(member).getDraw();
        }

        public DrawConfMember get(long member){
            if (!drawConfMemberMap.containsKey(member)){
                drawConfMemberMap.put(member, new DrawConfMember());
            }
            return drawConfMemberMap.get(member);
        }
    }

    public class DrawConfMember{
        private int year = Calendar.getInstance().get(Calendar.YEAR);
        private int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        private int drawNum = 0;//抽签数量
        private String draw = "";//签

        /**
         * 是否还能再抽
         * @param num
         * @return
         */
        public boolean isDoDraw(int num){
            if (year != Calendar.getInstance().get(Calendar.YEAR)){
                year = Calendar.getInstance().get(Calendar.YEAR);
                drawNum = 0;
                draw = "";
            }

            if (dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                drawNum = 0;
                draw = "";
            }

            return drawNum < num;
        }

        public void setNewDraw(String draw){
            if (year != Calendar.getInstance().get(Calendar.YEAR)){
                year = Calendar.getInstance().get(Calendar.YEAR);
                drawNum = 0;
                draw = "";
            }

            if (dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                drawNum = 0;
                draw = "";
            }

            drawNum++;
            this.draw = draw;
        }

        public String getDraw(){
            return draw;
        }
    }
}
