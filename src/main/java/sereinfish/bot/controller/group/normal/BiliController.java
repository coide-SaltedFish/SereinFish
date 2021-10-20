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
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bili.BiliManager;
import sereinfish.bot.entity.bili.entity.FollowConf;
import sereinfish.bot.entity.bili.entity.info.UserInfo;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.utils.OkHttpUtils;
import sereinfish.bot.utils.Result;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * bilibili 相关
 */
@GroupController
@Menu(type = Menu.Type.GROUP, name = "哔哩哔哩")
public class BiliController {

    @Before
    public void before(GroupConf groupConf){
    }

    @Action("\\^[Bb][Vv].*\\")
    @QMsg(at = true)
    public Message bvToAv(Group group, Message message, GroupConf groupConf) throws IOException {
        //启用判断
        if (!groupConf.isBiliBvExplainEnable()){
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

            if (!desc.endsWith("\n")){
                desc += "\n";
            }

            MessageLineQ messageLineQ = new Message().lineQ();
            File imageFile = NetHandle.imageDownload(map.get("pic"), "bili_" + bv);
            Image image = group.uploadImage(imageFile);
            messageLineQ.plus(image);//封面
            messageLineQ.textLine("标题：" + map.get("title"));
            messageLineQ.textLine("描述：" + desc);
            messageLineQ.textLine("链接：" + map.get("url"));

            return messageLineQ.getMessage();
        }else return Message.Companion.toMessage(result.getMessage());
    }

    @Action("加B站关注 {mid}")
    @Synonym("加b站关注 {mid}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "加B站关注", usage = "加B站关注 {mid}", description = "Bot会关注up主的一些动态并发到群里", permission = Permissions.GROUP_ADMIN)
    public String biliFollowAdd(Member sender, Group group, long mid){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            throw new DoNone();
        }

        try {
            UserInfo userInfo = BiliManager.getUserInfo(mid);

            FollowConf followConf = FollowConf.get(group.getId());
            return followConf.add(mid) + "\n" + userInfo.getData().getName();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return "添加失败：" + e.getMessage();
        }
    }

    @Action("取消B站关注 {mid}")
    @Synonym("取消b站关注 {mid}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "取消B站关注", usage = "取消B站关注 {mid}", description = "取消Bot的对指定up的B站关注", permission = Permissions.GROUP_ADMIN)
    public String biliFollowDelete(Member sender, Group group, long mid){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            throw new DoNone();
        }

        try {
            FollowConf followConf = FollowConf.get(group.getId());
            return followConf.delete(mid);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return "删除失败：" + e.getMessage();
        }
    }

    @Action("B站关注列表")
    @Synonym("b站关注列表")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "B站关注列表", usage = "B站关注列表", description = "查看Bot的关注列表", permission = Permissions.GROUP_ADMIN)
    public Message biliFollowList(Member sender, Group group){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)){
            throw new DoNone();
        }

        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.textLine("本群关注UP主列表如下：");
        try {
            FollowConf followConf = FollowConf.get(group.getId());

            for (FollowConf.BiliUser biliUser:followConf.getFollows()){
                UserInfo userInfo = BiliManager.getUserInfo(biliUser.getMid());
                try {
                    Image image = group.uploadImage(NetHandle.imageDownload(userInfo.getData().getFace(), userInfo.getData().getMid() + "_face"));
                    messageLineQ.plus(image);
                }catch (Exception e){
                    messageLineQ.textLine("图片加载失败：" + e.getMessage());
                }
                messageLineQ.textLine("名称：" + userInfo.getData().getName());
                messageLineQ.textLine("MID:" + userInfo.getData().getMid());
            }

            messageLineQ.text("にゃ～");
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return new Message().lineQ().text("获取失败").getMessage();
        }

        return messageLineQ.getMessage();
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
