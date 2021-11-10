package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.google.zxing.WriterException;
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
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.entity.GroupHistoryMsg;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.pixiv.Pixiv;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.entity.sauceNAO.SauceNao;
import sereinfish.bot.entity.sauceNAO.SauceNaoAPI;
import sereinfish.bot.entity.sauceNAO.sauce.Result;
import sereinfish.bot.entity.sauceNAO.sauce.SauceNAO;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.performance.MyPerformance;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "SauceNao搜图")
public class SauceNaoController extends QQController {
    @Inject
    private GroupHistoryMsgService groupHistoryMsgService;

    private int maxTime = 25000;

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

    @Action("\\^[!！.。]搜图$\\")
    @MenuItem(name = "搜图", usage = "[!！.。]搜图", description = "使用SauceNao在设置的库内搜索图片")
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
            GroupHistoryMsg groupHistoryMsg = groupHistoryMsgService.findByGroupAndMid(group.getId(), message.getReply().getId());
            if (groupHistoryMsg == null){
                group.sendMessage("找不到该消息，可能是消息未被记录:" + message.getReply().getId());
                return;
            }
            Message replayMsg = groupHistoryMsg.getMessage();
            for (MessageItem messageItem:replayMsg.getBody()){
                if (messageItem instanceof Image){
                    Image image = (Image) messageItem;
                    sNSearch(sender, message, groupConf, group, "http://gchat.qpic.cn/gchatpic_new/0/-0-"
                            + image.getId().substring(0, image.getId().lastIndexOf(".")).toUpperCase() + "/0");
                    break;
                }
            }
        }
    }

    @Action("搜图")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "搜图", usage = "@Bot 搜图", description = "使用SauceNao在设置的库内搜索图片")
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
    @MenuItem(name = "搜图", usage = "@Bot 搜头像 {member}", description = "使用SauceNao在设置的库内搜索指定对象头像")
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
            SfLog.getInstance().d(this.getClass(), "成功获取到数据");
            //判断是否成功
            if (sauceNAO.getHeader().getStatus() == SauceNAO.SUCCESS){
                SfLog.getInstance().d(this.getClass(), "成功获取到图片");
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
                        //是pixiv图片再进行
                        if (result.getHeader().getIndex_id() == 5){
                            messageLineQ.textLine("Pixiv ID：" + result.getData().getPixiv_id());
                            messageLineQ.textLine("作者名称：" + result.getData().getMember_name());

                            PixivEntity pixivEntity = Pixiv.getIllust(result.getData().getPixiv_id());//图片信息
                            if (pixivEntity.isError()){
                                messageLineQ.text("图片信息获取失败：" + pixivEntity.getError().getUser_message());
                                group.sendMessage(messageLineQ.getMessage());
                                return;
                            }

                            Illust illust = pixivEntity.getIllust();
                            illust.setProxy(groupConf.getPixivProxy());
                            int page = result.getHeader().getPage() + 1;

                            //标题
                            messageLineQ.textLine("标题：");
                            messageLineQ.textLine(MyYuQ.textLengthLimit(illust.getTitle(), 30));

                            //描述
                            messageLineQ.textLine("描述：");
                            messageLineQ.textLine(MyYuQ.textLengthLimit(MyYuQ.delHTMLTag(illust.getTitle()), 30));

                            //tag列表
                            messageLineQ.textLine("Tag：");
                            for (int i = 0; i < (illust.getTags().length > 4?4:illust.getTags().length); i++){
                                Illust.Tag tag = illust.getTags()[i];
                                messageLineQ.textLine(tag.getName() + "[" + tag.getTranslated_name() + "]");
                            }
                            if (illust.getTags().length > 4){
                                messageLineQ.textLine("...");
                            }else {
                                messageLineQ.textLine("");
                            }

                            if (pixivEntity.isError()){
                                messageLineQ.text("图片加载错误：" + pixivEntity.getError().getUser_message());
                                group.sendMessage(messageLineQ.getMessage());
                            }else {

                                if (page > illust.getPageMax()){
                                    messageLineQ.textLine("图片页数错误，已修改为显示第一张：" + page + "->1");
                                    page = 1;
                                }

                                if (illust.isR18()){
                                    messageLineQ.textLine("预览图片包含R18标签，这里" + MyYuQ.getBotName() + "就不进行展示了哦");
                                }

                                messageLineQ.textLine(String.format("共%d张，当前显示第%d张", pixivEntity.getIllust().getPageMax(), page));

                                String proxyUrl = pixivEntity.getIllust().getProxyUrl(page - 1);

                                if (pixivEntity.getIllust().isR18()) {
                                    //消息发送
                                    try {
                                        //原图
                                        messageLineQ.textLine("原图链接（也许是?）：");
                                        //生成二维码
                                        File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                                        try {
                                            BufferedImage image = QRCodeImage.backgroundMatrix(
                                                    QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                                    ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                                    0.3f,
                                                    Color.BLACK);
                                            ImageIO.write(image, "png", imageFile);

                                        } catch (WriterException e) {
                                            SfLog.getInstance().e(this.getClass(), e);
                                            messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                                        } catch (IOException e) {
                                            SfLog.getInstance().e(this.getClass(), e);
                                            messageLineQ.text("唔，二维码图片生成失败了：IOException");
                                        }
                                        messageLineQ.plus(group.uploadImage(imageFile));
                                        Message message1 = messageLineQ.getMessage();
                                        message1.setRecallDelay((long) groupConf.getSetuReCallTime() * 1000);
                                        group.sendMessage(message1);
                                    } catch (IllegalStateException e) {
                                        SfLog.getInstance().e(this.getClass(), e);
                                        messageLineQ.text("图片上传失败");
                                        group.sendMessage(new Message().lineQ().text("唔，预览图上传失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                        group.sendMessage(messageLineQ.getMessage());
                                    }
                                }else {
                                    //原图
                                    messageLineQ.textLine("原图（也许是?）：");
                                    try {
                                        File file = NetHandle.imagePixivDownload(illust, page - 1);
                                        BufferedImage bufferedImage = ImageIO.read(file);
                                        messageLineQ.textLine("图片信息："
                                                + bufferedImage.getWidth() + "x" + bufferedImage.getHeight()
                                                + "    "
                                                + MyPerformance.unitConversion(file.length()));
                                        messageLineQ.plus(group.uploadImage(file));
                                    } catch (IllegalStateException e) {
                                        SfLog.getInstance().e(this.getClass(), e);
                                        group.sendMessage(new Message().lineQ().text("唔，预览图上传失败了，但" + MyYuQ.getBotName() + "还是帮你找到了下面的图片信息").getMessage());
                                        messageLineQ.text("图片上传失败");
                                    } catch (IOException e){
                                        messageLineQ.text("图片下载失败：" + e.getMessage());
                                    }
                                    group.sendMessage(messageLineQ.getMessage());
                                }
                            }
                        }else if (result.getHeader().getIndex_id() == 9){//danbooru
                            messageLineQ.textLine("Danbooru ID：" + result.getData().getDanbooru_id());
                            messageLineQ.textLine("Gelbooru ID：" + result.getData().getGelbooru_id());
                            messageLineQ.textLine("创作者：" + result.getData().getCreator());
                            messageLineQ.textLine("来源：" + result.getData().getSource());
                            messageLineQ.text("外部链接：");
                            if (result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else if(result.getHeader().getIndex_id() == 38 || result.getHeader().getIndex_id() == 18){//e-hentai
                            ArrayList<String> creators = (ArrayList<String>) result.getData().getCreator();
                            messageLineQ.text("创作者：");
                            for (String s:creators){
                                messageLineQ.textLine(s);
                            }
                            messageLineQ.textLine("英文名：" + result.getData().getEng_name());
                            messageLineQ.textLine("日文名：" + result.getData().getJp_name());

                            messageLineQ.textLine("来源：" + result.getData().getSource());
                            //不是p站图片
                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else if (result.getHeader().getIndex_id() == 34){//deviantArt
                            messageLineQ.textLine("DeviantArt ID：" + result.getData().getDa_id());
                            messageLineQ.textLine("标题：" + result.getData().getTitle());
                            messageLineQ.textLine("作者：" + result.getData().getAuthor_name());
                            messageLineQ.textLine("Author_url：" + result.getData().getAuthor_url());
                            //不是p站图片
                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else if (result.getHeader().getIndex_id() == 37){//MangaDex
                            messageLineQ.textLine("MangaDex ID：" + result.getData().getMd_id());
                            messageLineQ.textLine("Mu ID：" + result.getData().getMu_id());
                            messageLineQ.textLine("Mal ID：" + result.getData().getMal_id());

                            messageLineQ.textLine("作者：" + result.getData().getAuthor());
                            messageLineQ.textLine("Artist：" + result.getData().getArtist());

                            messageLineQ.textLine("Part：" + result.getData().getPart());
                            messageLineQ.textLine("来源：" + result.getData().getSource());

                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else if(result.getHeader().getIndex_id() == 40){//FurAffinity
                            messageLineQ.textLine("FurAffinity ID：" + result.getData().getFa_id());
                            messageLineQ.textLine("标题：" + result.getData().getTitle());
                            messageLineQ.textLine("作者：" + result.getData().getAuthor_name());
                            messageLineQ.textLine("Author_url：" + result.getData().getAuthor_url());

                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else if(result.getHeader().getIndex_id() == 12){// Yande.re
                            messageLineQ.textLine("Yandere ID：" + result.getData().getYandere_id());
                            messageLineQ.textLine("作者：" + result.getData().getCreator());
                            messageLineQ.textLine("来源：" + result.getData().getSource());

                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
                            group.sendMessage(messageLineQ.getMessage());
                        }else {
                            //不是p站图片
                            messageLineQ.text("链接：");
                            if (result.getData().getExt_urls() != null && result.getData().getExt_urls().length > 0){
                                for (String url:result.getData().getExt_urls()){
                                    messageLineQ.textLine(url);
                                }
                            }else {
                                messageLineQ.text("无链接");
                            }
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
