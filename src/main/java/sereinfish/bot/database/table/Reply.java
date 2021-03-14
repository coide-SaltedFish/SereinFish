package sereinfish.bot.database.table;

import sereinfish.bot.database.dao.annotation.*;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 自动回复表格
 */
@DBHandle(tableName = "reply_n")
public class Reply {
    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    @Mark(type = {MarkType.DELETE}, condition = {"="})
    @Field(name = "id", type = "nvarchar", size = 8, isChar = true, isNotNull = true)
    private String id;

    @Field(name = "uuid", type = "nvarchar", size = 256, isChar = true, isNotNull = true)
    @Primary
    private String uuid;

    @Field(name = "qq", type = "bigint", isNotNull = true)
    private long qq;

    @Mark(type = {MarkType.QUERY}, condition = "LIKE")
    @Field(name = "group_num", type = "bigint", isNotNull = false)
    private long group;

    //是否是群私有
    @Field(name = "private", type = "int", isNotNull = true)
    private int pri;

    //是否模糊查询
    @Field(name = "is_fuzzy", type = "int", isNotNull = true)
    private int fuzzy;

    @Field(name = "key_", type = "nvarchar", size = SizeEnum.MAX, isChar = true, isNotNull = true)
    private String key;

    @Field(name = "reply", type = "nvarchar", size = SizeEnum.MAX, isChar = true, isNotNull = true)
    private String reply;

    public Reply() {
    }

    public Reply(long qq, long group, int pri, int fuzzy, String key, String reply) {
        this.uuid = MyYuQ.stringToMD5(key + ":::XX::" + reply + ":::XX::" + group);
        this.qq = qq;
        this.group = group;
        this.pri = pri;
        this.fuzzy = fuzzy;
        this.key = key;
        this.reply = reply;
    }

    public void setUUID(){
        this.uuid = MyYuQ.stringToMD5(key + ":::XX::" + reply + ":::XX::" + group);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public long getQq() {
        return qq;
    }

    public long getGroup() {
        return group;
    }

    public int getPri() {
        return pri;
    }

    public int getFuzzy() {
        return fuzzy;
    }

    public String getKey() {
        return key;
    }

    public String getReply() {
        return reply;
    }
}
