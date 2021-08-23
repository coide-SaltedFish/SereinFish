package sereinfish.bot.database.table;

import sereinfish.bot.database.dao.annotation.*;

import java.util.Date;

/**
 * 黑名单
 */
@DBHandle(tableName = "blacklist_n")
public class BlackList {
    @Primary
    @Field(name = "time",type = "bigint", isNotNull = true)
    private long time;

    @Field(name = "qq", type = "bigint", isNotNull = true)
    @Mark(type = {MarkType.UPDATE}, condition = {"="})
    private long qq;

    @Field(name = "group_num", type = "bigint", isNotNull = false)
    @Mark(type = {MarkType.UPDATE}, condition = {"="})
    private long group;

    @Field(name = "remarks", type = "nvarchar", size = SizeEnum.MAX, isChar = true, isNotNull = false)
    private String remarks;//备注

    public BlackList() {
    }

    public BlackList(long time, long qq, long group, String remarks) {
        this.time = time;
        this.qq = qq;
        this.group = group;
        this.remarks = remarks;
    }

    public BlackList(Date time, long qq, long group, String remarks) {
        this.time = time.getTime();
        this.qq = qq;
        this.group = group;
        this.remarks = remarks;
    }

    public long getTime() {
        return time;
    }

    public Date getDate(){
        Date date = new Date(time);
        return date;
    }

    public long getQq() {
        return qq;
    }

    public long getGroup() {
        return group;
    }

    public String getRemarks() {
        return remarks;
    }
}
