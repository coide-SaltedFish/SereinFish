package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageLineQ;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.pixiv.Pixiv;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.entity.pixivcat.PixivCat;
import sereinfish.bot.entity.sauceNAO.SauceNao;
import sereinfish.bot.entity.sauceNAO.SauceNaoAPI;
import sereinfish.bot.entity.sauceNAO.sauce.Result;
import sereinfish.bot.entity.sauceNAO.sauce.SauceNAO;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GroupController
public class SauceNaoController extends QQController {

    private int maxTime = 15000;

    @Before
    public void before(Group group, GroupConf groupConf){
        //判断是否启用
        if (!groupConf.isSauceNaoAPIEnable()){
            throw new DoNone();
        }

        //key
        if (groupConf.getSauceNaoApiKey() == null || groupConf.getSauceNaoApiKey().equals("")){
            group.sendMessage("Api key is empty!!");
        }
    }

    @Action("\\^[!！.。]搜图\\")
    public void search(Member sender, Group group, Message message, GroupConf groupConf){
        if (message.getReply() == null){
            Image image = null;
            for (MessageItem messageItem:message.getBody()){
                if (messageItem instanceof Image){
                    image = (Image) messageItem;
                    break;
                }
            }

            if (image == null){
                group.sendMessage("找不到要进行搜索的图片，请确认指令正确");
                return;
            }
            sNSearch(sender, message, groupConf, group, image.getUrl());
        }else {
            GroupHistoryMsg groupHistoryMsg = null;
            try {
                groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(), message.getReply().getId());
                if (groupHistoryMsg == null){
                    group.sendMessage("找不到该消息，可能是消息未被记录:" + message.getReply().getId());
                    return;
                }
                Message replayMsg = groupHistoryMsg.getMessage();
                for (MessageItem messageItem:replayMsg.getBody()){
                    if (messageItem instanceof Image){
                        Image image = (Image) messageItem;
                        sNSearch(sender, message, groupConf, group, "http://gchat.qpic.cn/gchatpic_new/0/-0-" + image.getId().substring(0, image.getId().lastIndexOf(".")) + "/0");
                        break;
                    }
                }
            } catch (SQLException e) {
                SfLog.getInstance().e(this.getClass(),e);
                group.sendMessage("发生错误了：" + e.getMessage());
            }
        }
    }

    @Action("搜图")
    @QMsg(mastAtBot = true)
    public void atSearch(Member sender, Group group, Message message, GroupConf groupConf, ContextSession session){
        Message message1 = MyYuQ.getMif().text("请输入消息内容(" + (maxTime / 1000) + "s)").toMessage();
        message1.setReply(message.getSource());
        reply(message1);

        try{
            Message msg = session.waitNextMessage(maxTime);
            for (MessageItem messageItem:msg.getBody()){
                if (messageItem instanceof Image){
                    Image image = (Image) messageItem;
                    sNSearch(sender, msg, groupConf, group, image.getUrl());
                    return;
                }
            }
        }catch (WaitNextMessageTimeoutException e){
            group.sendMessage(MyYuQ.getMif().text("已超时取消").toMessage());
        }
    }

    @Action("搜头像 {member}")
    @QMsg(mastAtBot = true)
    public void headImageSearch(Member sender, Group group, Message message, GroupConf groupConf, String member){
        long qq = -1;
        try {
            if (member.startsWith("At_")){
                member = member.substring("At_".length());
            }
            //是数字
            qq = Long.valueOf(member);
            if (!group.getMembers().containsKey(qq) && qq != MyYuQ.getYuQ().getBotId()){
                throw new SkipMe();
            }
        }catch (Exception e) {
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())) {
                qq = group.getBot().getId();
            } else {
                for (Map.Entry<Long, Member> entry : group.getMembers().entrySet()) {
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)) {
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1) {
                throw new SkipMe();
            }
        }
        String url = "http://q1.qlogo.cn/g?b=qq&nk=" + qq + "&s=640";
        sNSearch(sender, message, groupConf, group, url);
    }


    /**
     * 搜图
     * @param group
     * @param imageUrl
     */
    private void sNSearch(Member sender, Message message, GroupConf groupConf, Group group, String imageUrl){
        Message msg = new Message().lineQ().text("正在搜索~").getMessage();
        msg.setReply(message.getSource());
        group.sendMessage(msg);

        SauceNao sauceNao = new SauceNao(SauceNao.OUT_TYPE_JSON,
                groupConf.getSauceNaoApiKey(),
                5,
                groupConf.getSauceNaoApiDb(),
                1,
                2,
                imageUrl);
        try {
            SauceNAO sauceNAO = SauceNaoAPI.search(sauceNao);
            //判断是否成功
            if (sauceNAO.getHeader().getStatus() == SauceNAO.SUCCESS){
                if (sauceNAO.getResults().size() > 0){
                    MessageLineQ messageLineQ = new Message().lineQ();
                    if (sender != null){
                        messageLineQ.at(sender).textLine("");
                    }
                    messageLineQ.textLine("成功找到了图片：");
                    for(Result result:sauceNAO.getResults()) {//得到结果
                        messageLineQ.textLine("相似度：" + result.getHeader().getSimilarity() + "%");
                       if (result.getHeader().getIndex_name().contains("-")){
                           messageLineQ.textLine("索引名称：" + result.getHeader().getIndex_name().split("-")[0].trim());
                       }else {
                           messageLineQ.textLine("索引名称：" + result.getHeader().getIndex_name());
                       }
                        messageLineQ.text("链接：");
                        if (result.getData().getExt_urls().length > 0){
                            for (String url:result.getData().getExt_urls()){
                                messageLineQ.textLine(url);
                            }
                        }else {
                            messageLineQ.text("无链接");
                        }
                        if (result.getData().getPixiv_id() != 0){
                            if (result.getData().getMember_id() != 0){
                                messageLineQ.textLine("作者链接：https://www.pixiv.net/users/" + result.getData().getMember_id());
                            }
                            messageLineQ.textLine("Pixiv ID：" + result.getData().getPixiv_id());
                            messageLineQ.textLine("作者ID：" + result.getData().getMember_id());
                            messageLineQ.textLine("作者名称：" + result.getData().getMember_name());

                            final Message noImage = MyYuQ.getMif().text("").plus(messageLineQ.getMessage());

                            //是pixiv图片再进行
                            if (groupConf.getSauceNaoApiDb() == 5){
                                PixivEntity pixivEntity = Pixiv.getIllust(result.getData().getPixiv_id());//图片信息
                                if (pixivEntity.isError()){
                                    messageLineQ.text("图片加载错误：" + pixivEntity.getError().getUser_message());
                                    group.sendMessage(messageLineQ.getMessage());
                                }else {
                                    //标签检测
                                    if (!Pixiv.isR18(pixivEntity.getIllust().getTags())){
                                        //获取链接
                                        //获取分p
                                        String p = "0";
                                        Pattern pattern = Pattern.compile("_p[0-9]{1,3}");
                                        Matcher matcher = pattern.matcher(result.getHeader().getIndex_name());
                                        if(matcher.find()) {
                                            p = matcher.group();
                                            pattern = Pattern.compile("[0-9]{1,3}");
                                            matcher = pattern.matcher(p);
                                            if(matcher.find()) {
                                                p = matcher.group();
                                            }
                                        }
                                        //获取链接
                                        String finalP = p;
                                        //pixiv cat
                                        PixivCat.getPixivCat("https://www.pixiv.net/artworks/" + result.getData().getPixiv_id(), new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                SfLog.getInstance().e(SauceNaoController.this.getClass(), e);
                                                group.sendMessage(new Message().lineQ().
                                                        textLine("唔，预览图获取失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息")
                                                        .text("(Err:" + e.getMessage()).getMessage() + ")");
                                                group.sendMessage(messageLineQ.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String string = response.body().string();
                                                //System.out.println(string);
                                                PixivCat pixivCat = MyYuQ.toClass(string, PixivCat.class);

                                                if (pixivCat.isSuccess()){
                                                    try {
                                                        int imageP = Integer.parseInt(finalP);
                                                        String proxyUrl = "";
                                                        if (pixivCat.getOriginal_urls_proxy() != null){
                                                            if (imageP < pixivCat.getOriginal_urls_proxy().length){
                                                                proxyUrl = pixivCat.getOriginal_urls_proxy()[imageP];
                                                            }else {
                                                                group.sendMessage(new Message().lineQ().text("唔，找不到预览图，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                                group.sendMessage(messageLineQ.getMessage());
                                                                return;
                                                            }
                                                        }else if(pixivCat.getOriginal_url_proxy() != null){
                                                            proxyUrl = pixivCat.getOriginal_url_proxy();
                                                        }else {
                                                            group.sendMessage(new Message().lineQ().text("唔，找不到预览图，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                            group.sendMessage(messageLineQ.getMessage());
                                                            return;
                                                        }

                                                        //原图
                                                        messageLineQ.textLine("原图（也许是?）：");
                                                        messageLineQ.imageByUrl(proxyUrl);

                                                        try {
                                                            group.sendMessage(messageLineQ.getMessage());
                                                        }catch (IllegalStateException e){
                                                            SfLog.getInstance().e(this.getClass(), e);
                                                            group.sendMessage(new Message().lineQ().text("唔，预览图上传失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                            group.sendMessage(noImage);
                                                        }

                                                    }catch (Exception e){
                                                        group.sendMessage(new Message().lineQ().text("唔，预览图分P获取失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                        group.sendMessage(messageLineQ.getMessage());
                                                    }
                                                }else {
                                                    messageLineQ.text("代理获取失败：" + pixivCat.getError());
                                                    group.sendMessage(messageLineQ.getMessage());
                                                }
                                            }
                                        });
                                    }else {
                                        messageLineQ.textLine("预览图片包含R18标签，这里" + MyYuQ.getBotName() + "就不进行展示了哦");
                                        //获取链接
                                        //获取分p
                                        String p = "0";
                                        Pattern pattern = Pattern.compile("_p[0-9]{1,3}");
                                        Matcher matcher = pattern.matcher(result.getHeader().getIndex_name());
                                        if(matcher.find()) {
                                            p = matcher.group();
                                            pattern = Pattern.compile("[0-9]{1,3}");
                                            matcher = pattern.matcher(p);
                                            if(matcher.find()) {
                                                p = matcher.group();
                                            }
                                        }
                                        //获取链接
                                        String finalP = p;
                                        PixivCat.getPixivCat("https://www.pixiv.net/artworks/" + result.getData().getPixiv_id(), new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                group.sendMessage(new Message().lineQ().text("唔，预览图链接获取失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                group.sendMessage(messageLineQ.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String string = response.body().string();
                                                PixivCat pixivCat = MyYuQ.toClass(string, PixivCat.class);

                                                if (pixivCat.isSuccess()){
                                                    try {
                                                        int imageP = Integer.parseInt(finalP);
                                                        String proxyUrl = "";
                                                        if (pixivCat.getOriginal_urls_proxy() != null){
                                                            if (imageP < pixivCat.getOriginal_urls_proxy().length){
                                                                proxyUrl = pixivCat.getOriginal_urls_proxy()[imageP];
                                                            }else {
                                                                group.sendMessage(new Message().lineQ().text("唔，找不到预览图，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                                group.sendMessage(messageLineQ.getMessage());
                                                                return;
                                                            }
                                                        }else if(pixivCat.getOriginal_url_proxy() != null){
                                                            proxyUrl = pixivCat.getOriginal_url_proxy();
                                                        }else {
                                                            group.sendMessage(new Message().lineQ().text("唔，找不到预览图，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                            group.sendMessage(messageLineQ.getMessage());
                                                            return;
                                                        }

                                                        //原图
                                                        messageLineQ.textLine("原图链接（也许是?）：");
                                                        messageLineQ.text(proxyUrl);

                                                        group.sendMessage(messageLineQ.getMessage());

                                                    }catch (Exception e){
                                                        group.sendMessage(new Message().lineQ().text("唔，预览图分P获取失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                                        group.sendMessage(messageLineQ.getMessage());
                                                    }
                                                }else {
                                                    messageLineQ.text("代理获取失败：" + pixivCat.getError());
                                                    group.sendMessage(messageLineQ.getMessage());
                                                }
                                            }
                                        });
                                    }
                                }
                            }else {
                                group.sendMessage(noImage);
                            }

                        }else {
                            group.sendMessage(messageLineQ.getMessage());
                        }

                    }
                }else {
                    group.sendMessage(new Message().lineQ().textLine("未找到图片相关结果:" + sauceNAO.getResults().size()).imageByUrl(imageUrl).getMessage());
                }

            }else {
                group.sendMessage(new Message().lineQ().textLine("搜图失败了:").text(sauceNAO.getHeader().getMessage()).getMessage());
            }
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            group.sendMessage(new Message().lineQ().textLine("错误：").text(e.getMessage()).getMessage());
        }
    }
}