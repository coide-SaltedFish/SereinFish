package sereinfish.bot.database.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sereinfish.bot.database.dao.annotation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DBHandle(tableName = "xiaomi_motion_account")
public class Account {

    public class Type{
        public static final int XIAOMI_MOTION = 0;//小米运动
    }

    @Field(name = "type",type = "int", isNotNull = true)
    int type;

    @Field(name = "time",type = "bigint", isNotNull = true)
    long time;

    @Primary
    @Mark(type = MarkType.DELETE, condition = {"="})
    @Field(name = "qq",type = "bigint", isNotNull = true)
    long qq;

    @Field(name = "account", type = "nvarchar", size = 10000, isNotNull = true, isChar = true)
    String account;

    @Field(name = "password", type = "nvarchar", size = 10000, isNotNull = true, isChar = true)
    String password;

    @Field(name = "token", type = "nvarchar", size = 10000, isNotNull = true, isChar = true)
    String token;



    @Override
    public String toString(){
        return "[类型：" + type +
                ",时间：" + time +
                ",QQ：" + qq +
                ",账号：" + account +
                ",密码：" + password + "]";
    }
}
