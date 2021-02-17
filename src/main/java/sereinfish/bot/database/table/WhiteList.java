package sereinfish.bot.database.table;

import sereinfish.bot.database.dao.annotation.*;
import sereinfish.bot.database.dao.annotation.Character;

import java.sql.Timestamp;

/**
 * 白名单
 */
@DBHandle(tableName = "whitelist")
public class WhiteList {

    @Field(name = "qq", type = "bigint",isNotNull = true)
    @Character(encoding = "utf8mb4")
    @Primary
    @Mark(type = {MarkType.DELETE,MarkType.QUERY},condition = {"=", " ="}) //标记，用于删除与查询
    private long qq;//qq号

    @Field(name = "main_uuid", type = "nvarchar",size = 37,isNotNull = false, isChar = true)
    @Character(encoding = "utf8mb4")
    @Mark(type = {MarkType.DELETE,MarkType.QUERY},condition = {"=", " ="}) //标记，用于删除与查询
    private String main_uuid;//大号uuid

    @Field(name = "main_add_time", type = "datetime",isNotNull = false, isChar = true)
    @Character(encoding = "utf8mb4")
    private Timestamp main_add_time;//大号添加时间

    @Field(name = "alt_uuid", type = "nvarchar",size = 256,isNotNull = false, isChar = true)
    @Character(encoding = "utf8mb4")
    private String alt_uuid;//小号uuid

    @Field(name = "alt_add_time", type = "datetime",isNotNull = false, isChar = true)
    @Character(encoding = "utf8mb4")
    private Timestamp alt_add_time;//小号添加时间

    public WhiteList() {
    }

    public WhiteList(long qq, String main_uuid, Timestamp main_add_time, String alt_uuid, Timestamp alt_add_time) {
        this.qq = qq;
        this.main_uuid = main_uuid;
        this.main_add_time = main_add_time;
        this.alt_uuid = alt_uuid;
        this.alt_add_time = alt_add_time;
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public String getMain_uuid() {
        return main_uuid;
    }

    public void setMain_uuid(String main_uuid) {
        this.main_uuid = main_uuid;
    }

    public Timestamp getMain_add_time() {
        return main_add_time;
    }

    public void setMain_add_time(Timestamp main_add_time) {
        this.main_add_time = main_add_time;
    }

    public String getAlt_uuid() {
        return alt_uuid;
    }

    public void setAlt_uuid(String alt_uuid) {
        this.alt_uuid = alt_uuid;
    }

    public Timestamp getAlt_add_time() {
        return alt_add_time;
    }

    public void setAlt_add_time(Timestamp alt_add_time) {
        this.alt_add_time = alt_add_time;
    }
}
