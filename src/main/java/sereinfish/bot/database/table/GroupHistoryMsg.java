package sereinfish.bot.database.table;

import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.dao.annotation.DBHandle;
import sereinfish.bot.database.dao.annotation.Field;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 群历史消息数据表
 */
@DBHandle(tableName = "history_group_msg")
public class GroupHistoryMsg {
    @Field(name = "time", type = "bigint", isNotNull = true)
    private long time;//发送时间

    @Field(name = "group_num",type = "bigint", isNotNull = true)
    private long group;//来自群

    @Field(name = "qq", type = "bigint", isNotNull = true)
    private long qq;//来自qq

    @Field(name = "id",type = "int",isNotNull = true)
    private int id;

    @Field(name = "msg", type = "nvarchar", size = 10000, isNotNull = true, isChar = true)
    private String msg;//消息内容

    public GroupHistoryMsg() {
    }

    public GroupHistoryMsg(long time, long group, long qq, int id, String msg) {
        this.time = time;
        this.group = group;
        this.qq = qq;
        this.id = id;
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public long getGroup() {
        return group;
    }

    public long getQq() {
        return qq;
    }

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public Message getMessage(){
        return Message.Companion.toMessageByRainCode(msg);
    }
}
