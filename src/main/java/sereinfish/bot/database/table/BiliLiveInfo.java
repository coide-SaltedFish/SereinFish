package sereinfish.bot.database.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sereinfish.bot.database.dao.annotation.DBHandle;
import sereinfish.bot.database.dao.annotation.Field;

@DBHandle(tableName = "bili_live_info")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BiliLiveInfo {
    @Field(name = "time", type = "bigint", isNotNull = true)
    private long time;//添加时间

    @Field(name = "group_num",type = "bigint", isNotNull = true)
    private long group;//来自群

    @Field(name = "qq", type = "bigint", isNotNull = true)
    private long qq;//来自qq

    @Field(name = "id",type = "bigint",isNotNull = true)
    private long id;//直播间ID
}
