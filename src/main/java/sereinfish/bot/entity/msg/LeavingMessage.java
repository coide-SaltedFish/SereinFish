package sereinfish.bot.entity.msg;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 留言实体类
 */
public class LeavingMessage {
    private long group;
    private Map<Long, Map<Long, ArrayList<Msg>>> map = new HashMap<>();

    public LeavingMessage(long group) {
        this.group = group;
    }

    /**
     * 添加留言
     * @param group
     * @param msg
     * @return
     */
    public String add(long group, Msg msg){
        if (!map.containsKey(group)){
            map.put(group, new HashMap<>());
        }

        if (!map.get(group).containsKey(msg.getSender())){
            map.get(group).put(msg.getSender(), new ArrayList<>());
        }

        //检查同一对象的留言数量
        if (getUserLMNum(group, msg.getReceive()) > 5){
            return "这人的留言信箱已经塞满了qwq";
        }

        //查看是否已有留言
        ArrayList<Msg> msgs = map.get(group).get(msg.getSender());
        for (int i = 0; i < msgs.size(); i++){
            Msg m = msgs.get(i);
            if (m.getReceive() == msg.getReceive()){
                msgs.remove(i);
                msgs.add(msg);

                save();
                return "已重置留言";
            }
        }

        msgs.add(msg);

        save();
        return "留言已添加";
    }

    /**
     * 得到指定对象的留言数量
     * @param group
     * @param member
     * @return
     */
    private int getUserLMNum(long group, long member){
        int num = 0;
        for (Map.Entry<Long, ArrayList<Msg>> entry:map.get(group).entrySet()){
            for (Msg msg:entry.getValue()){
                if (msg.getReceive() == member){
                    num++;
                }
            }
        }

        return num;
    }

    /**
     * 删除留言
     * @param group
     * @param sender
     * @param receive
     * @return
     */
    public String delete(long group, long sender, long receive){
        if (map.containsKey(group)){
            if (map.get(group).containsKey(sender)){
                ArrayList<Msg> msgs = map.get(group).get(sender);
                for (int i = 0; i < msgs.size(); i++){
                    Msg m = msgs.get(i);
                    if (m.getReceive() == receive){
                        msgs.remove(i);

                        save();
                        return "已删除留言";
                    }
                }
            }
        }
        return "未找到留言";
    }

    /**
     * 检查是否有留言
     * @param group
     * @param sender
     */
    public void check(long group, long sender){
        if (map.containsKey(group)){

            for (Map.Entry<Long, ArrayList<Msg>> entry:map.get(group).entrySet()){
                for (int i = 0; i < entry.getValue().size(); i++){
                    Msg msg = entry.getValue().get(i);

                    if (msg.getReceive() == sender){
                        Group group1 = MyYuQ.getYuQ().getGroups().get(group);
                        group1.sendMessage(new Message().lineQ().at(sender).textLine("")
                                .at(msg.getSender())
                                .text(" 在 " + Time.dateToString(msg.getTime(), "yyyy-MM-dd HH:mm:ss") + " 给你留言："));
                        group1.sendMessage(msg.getMessage());

                        entry.getValue().remove(i);
                        save();
                    }
                }
            }
        }
    }

    /**
     * 检测任务
     */
    public static void checkRunnable(long group, long sender){
        try {
            LeavingMessage.get(group).check(group, sender);
        } catch (IOException e) {
            SfLog.getInstance().e(LeavingMessage.class, e);
        }
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.groupDataPath, group + "/LeavingMessage.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, LeavingMessage.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "群：" + group + "留言配置保存失败");
        }
    }

    /**
     * 得到配置
     * @param group
     * @return
     */
    public static LeavingMessage get(long group) throws IOException {
        File confFile = new File(FileHandle.groupDataPath, group + "/LeavingMessage.json");

        if (confFile.exists() && confFile.isFile()){
            String json = FileHandle.read(confFile);
            return MyYuQ.toClass(json, LeavingMessage.class);
        }else {
            LeavingMessage leavingMessage = new LeavingMessage(group);
            leavingMessage.save();
            return leavingMessage;
        }
    }

    /**
     * 留言的消息
     */
    @Getter
    @AllArgsConstructor
    public static class Msg{
        long time;
        long sender;
        long receive;

        String msg;


        public Message getMessage(){
            return Message.Companion.toMessageByRainCode(msg);
        }

        public void setMsg(Message msg){
            this.msg = Message.Companion.toCodeString(msg);
        }
    }
}
