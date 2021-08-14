package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.alibaba.fastjson.JSONObject;
import com.icecreamqaq.yuq.FunKt;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.utils.OkHttpUtils;
import sereinfish.bot.utils.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * bilibili 相关
 */
@GroupController
public class BiliController {

    @Before
    public void before(Group group){
        GroupConf conf = GroupConfManager.getInstance().get(group.getId());
        //启用判断
        if (!(Boolean) conf.getControl(GroupControlId.CheckBox_BiliEnable).getValue()){
            throw new DoNone();
        }
    }

    @Action("\\^[Bb][Vv].*\\")
    @QMsg(at = true)
    public Message bvToAv(Message message, Group group) throws IOException {
        GroupConf conf = GroupConfManager.getInstance().get(group.getId());
        //启用判断
        if (!(Boolean) conf.getControl(GroupControlId.CheckBox_BiliBvExplain).getValue()){
            throw new DoNone();
        }

        String bv = message.getBody().get(0).toPath();

        Pattern pattern = Pattern.compile("^[Bb][Vv][A-Za-z0-9]{10}(?=\n?)");
        Matcher matcher = pattern.matcher(bv);
        if (matcher.find()) {
            bv = matcher.group(0);
        }else {
            throw new SkipMe();
        }

        Result<Map<String, String>> result = bvToAv(bv);
        if (result.getCode() == 200){
            Map<String, String> map = result.getData();
            MessageItemFactory mif = FunKt.getMif();

            String desc = map.get("desc");
            int maxLen = 40;
            if (desc.length() > maxLen){
                desc = desc.substring(0, maxLen) + "...\n";
            }
            while(!desc.equals(desc.replace("\n\n","\n"))){
                desc = desc.replace("\n\n","\n");
            }

            return mif.imageByUrl(map.get("pic")).plus(
                    "标题：" + map.get("title") + "\n" +
                            "描述：" + desc +
                            "链接：" + map.get("url")
            );
        }else return Message.Companion.toMessage(result.getMessage());
    }

    public Result<Map<String, String>> bvToAv(String bv) throws IOException {
        if (bv.length() != 12) return Result.failure("欸，这个不认识呢：" + bv, null);
        JSONObject jsonObject = OkHttpUtils.getJson("https://api.bilibili.com/x/web-interface/view?bvid=" + bv);
        Integer code = jsonObject.getInteger("code");
        if (code == 0){
            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
            Map<String, String> map = new HashMap<>();
            map.put("pic", dataJsonObject.getString("pic"));
            map.put("dynamic", dataJsonObject.getString("dynamic"));
            map.put("title", dataJsonObject.getString("title"));
            map.put("desc", dataJsonObject.getString("desc"));
            map.put("aid", dataJsonObject.getString("aid"));
            map.put("url", "https://www.bilibili.com/video/av" + dataJsonObject.getString("aid"));
            return Result.success(map);
        }else if (code == -404) return Result.failure("エラー発生", null);
        else return Result.failure(jsonObject.getString("message"), null);
    }
}
