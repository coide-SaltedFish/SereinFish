package sereinfish.bot.entity.mc;

import lombok.Getter;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import java.io.IOException;

public class MojangAPI {

    /**
     * 得到账户信息
     * @param name
     * @return
     * @throws IOException
     */
    public static AccountInfo getAccountInfo(String name) throws IOException {
        String api = "https://api.mojang.com/users/profiles/minecraft/" + name;

        return MyYuQ.toClass(OkHttpUtils.getStr(api), AccountInfo.class);
    }

    /**
     * 得到玩家用过的所有uuid
     * @param uuid
     * @return
     */
    public static NameInfo[] getNamesInfo(String uuid) throws IOException {
        String api = "https://api.mojang.com/user/profiles/" + uuid + "/names";

        return MyYuQ.toClass(OkHttpUtils.getStr(api), NameInfo[].class);
    }

    @Getter
    public class NameInfo{
        String name;
        long changedToAt = 0;
    }

    @Getter
    public class AccountInfo{
        String name;
        String id;
    }
}
