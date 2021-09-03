package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.google.zxing.WriterException;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.pixiv.Pixiv;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.entity.pixiv.entity.Rank;
import sereinfish.bot.entity.pixivcat.PixivCat;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

@Menu(type = Menu.Type.GROUP, name = "Pixiv")
@GroupController
public class PixivController {

    private boolean isRun = false;

    @Before
    public void before(){
        if (isRun){
            throw new Message().lineQ().text("模块正在运行").getMessage().toThrowable();
        }
    }


    @Action("\\^获取[Pp]站日榜$\\")
    @Synonym({"获取Pixiv日榜", "\\^[Pp]站日榜$\\"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "获取Pixiv日榜前7", usage = "@Bot 获取Pixiv日榜", description = "获取Pixiv日榜前7")
    public Message getDayRank(Group group){
        isRun = true;
        group.sendMessage("开始获取~");
        try {
            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_DAY, 1, 7);
            analysisRank(rank, group, 7);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            isRun = false;
            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
        }
        throw new DoNone();
    }

    @Action("\\^获取[Pp]站周榜$\\")
    @Synonym({"获取Pixiv周榜", "\\^[Pp]站周榜$\\"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "获取Pixiv周榜前7", usage = "@Bot 获取Pixiv周榜", description = "获取Pixiv周榜前7")
    public Message getWeekRank(Group group){
        isRun = true;
        group.sendMessage("开始获取~");
        try {
            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_WEEK, 1, 7);
            analysisRank(rank, group, 7);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            isRun = false;
            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
        }
        throw new DoNone();
    }

    @Action("\\^获取[Pp]站月榜$\\")
    @Synonym({"获取Pixiv月榜", "\\^[Pp]站月榜$\\"})
    @QMsg(mastAtBot = true)
    @MenuItem(name = "获取Pixiv月榜前7", usage = "@Bot 获取Pixiv月榜", description = "获取Pixiv月榜前7")
    public Message getMonthRank(Group group){
        isRun = true;
        group.sendMessage("开始获取~");
        try {
            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_MONTH, 1, 7);
            analysisRank(rank, group, 7);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            isRun = false;
            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
        }
        throw new DoNone();
    }

    @Action("获取Pixiv {type} 榜")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "获取Pixiv指定榜前7", usage = "@Bot 获取Pixiv {type} 榜", description = "获取Pixiv指定榜前7")
    public Message getTypeRank(Group group, String type){
        isRun = true;
        group.sendMessage("开始获取~");
        try {
            Rank rank = Pixiv.getRank(type, 1, 7);
            analysisRank(rank, group, 7);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            isRun = false;
            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
        }
        throw new DoNone();
    }

    @Action("\\^[pP]站图片$\\ {uid}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "获取P站指定uid的图片", usage = "@Bot P站图片 {uid}", description = "获取P站指定uid的图片")
    public Message getPixivImageUid(Group group, Member sender, Message message, long uid){

        System.out.println(uid);
        group.sendMessage("开始获取~");
        //获取代理
        try {
            PixivEntity pixivEntity = Pixiv.getIllust(uid);
            if (pixivEntity.isError()){
                return new Message().lineQ().textLine("出现错误了：").text(pixivEntity.getError().getUser_message()).getMessage();
            }else {
                Illust illust = pixivEntity.getIllust();
                //获取代理
                PixivCat.getPixivCat("https://www.pixiv.net/artworks/" + uid, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message msg = new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
                        msg.setReply(message.getSource());
                        group.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String string = response.body().string();
                        PixivCat pixivCat = MyYuQ.toClass(string, PixivCat.class);

                        String proxyUrl;

                        if (pixivCat.getOriginal_urls_proxy() != null){
                            proxyUrl = pixivCat.getOriginal_urls_proxy()[illust.getRestrict()];
                        }else if(pixivCat.getOriginal_url_proxy() != null){
                            proxyUrl = pixivCat.getOriginal_url_proxy();
                        }else {
                            Message msg = new Message().lineQ().text("唔，找不到预览图").getMessage();
                            msg.setReply(message.getSource());
                            group.sendMessage(msg);
                            return;
                        }
                        MessageLineQ messageLineQ = new Message().lineQ();
                        messageLineQ.textLine("图片[" + uid + "]：");
                        //是否R18
                        if (Pixiv.isR18(illust.getTags())){
                            messageLineQ.textLine("要找的图片带有R18标签，这里" + MyYuQ.getBotName() + "就不进行展示了哦");
                            File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                            try {
                                BufferedImage image = QRCodeImage.backgroundMatrix(
                                        QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                        ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                        0.0f,
                                        Color.BLACK);
                                ImageIO.write(image, "png", imageFile);
                                messageLineQ.imageByFile(imageFile);
                            } catch (WriterException e) {
                                SfLog.getInstance().e(this.getClass(), e);
                                messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                            }catch (IOException e){
                                SfLog.getInstance().e(this.getClass(), e);
                                messageLineQ.text("唔，二维码图片生成失败了：IOException");
                            }
                        }else {
                            messageLineQ.imageByUrl(proxyUrl);
                        }
                        Message msg = messageLineQ.getMessage();
                        msg.setReply(message.getSource());
                        group.sendMessage(msg);
                        return;
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new DoNone();
    }


    /**
     * 排行榜解析
     * @param rank
     * @return
     */
    private void analysisRank(Rank rank, Group group, int size){
        MessageLineQ messageLineQ = new Message().lineQ();

        if (rank.getIllusts() == null || rank.getIllusts().length == 0){
            group.sendMessage("数据获取失败了");
            isRun = false;
            return;
        }
        int len = rank.getIllusts().length;
        if (size > len){
            size = len;
        }

        group.sendMessage("共 " + size + " 条数据，努力加载中...");
        final int[] index = {0};

        int finalSize = size;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < finalSize; i++){
                    Illust illust = rank.getIllusts()[i];
                    final boolean[] isEnd = {false};
                    messageLineQ.textLine("标题：" + illust.getTitle());//标题
                    messageLineQ.textLine("描述：").textLine(MyYuQ.delHTMLTag(illust.getCaption()));//描述
                    messageLineQ.textLine("图片：");
                    //获取链接
                    PixivCat.getPixivCat("https://www.pixiv.net/artworks/" + illust.getId(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            messageLineQ.textLine("代理数据获取失败了:" + e);
                            isEnd[0] = true;
                            index[0]++;
                            if (index[0] == finalSize){
                                group.sendMessage(messageLineQ.getMessage());
                                isRun = false;
                            }
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            index[0]++;

                            String string = response.body().string();
//                            System.out.println(string);
                            PixivCat pixivCat = MyYuQ.toClass(string, PixivCat.class);

                            if (pixivCat.isSuccess()){
                                String proxyUrl = "";
                                if (pixivCat.getOriginal_urls_proxy() != null){
                                    proxyUrl = pixivCat.getOriginal_urls_proxy()[illust.getRestrict()];
                                }else if(pixivCat.getOriginal_url_proxy() != null){
                                    proxyUrl = pixivCat.getOriginal_url_proxy();
                                }else {
                                    messageLineQ.textLine("唔，找不到预览图");
                                    if (index[0] == finalSize){
                                        isEnd[0] = true;
                                        group.sendMessage(messageLineQ.getMessage());
                                    }
                                    isRun = false;
                                    return;
                                }
                                //r18
                                if (Pixiv.isR18(illust.getTags())){
                                    File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                                    try {
                                        BufferedImage image = QRCodeImage.backgroundMatrix(
                                                QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                                ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                                0.0f,
                                                Color.BLACK);
                                        ImageIO.write(image, "png", imageFile);
                                        messageLineQ.imageByFile(imageFile);
                                    } catch (WriterException e) {
                                        SfLog.getInstance().e(this.getClass(), e);
                                        messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                                    }catch (IOException e){
                                        SfLog.getInstance().e(this.getClass(), e);
                                        messageLineQ.text("唔，二维码图片生成失败了：IOException");
                                    }
                                }else {
                                    messageLineQ.imageByUrl(proxyUrl);
                                }
                            }else {
                                messageLineQ.textLine("代理获取失败：" + pixivCat.getError());
                            }

                            isEnd[0] = true;
                            if (index[0] == finalSize){
                                group.sendMessage(messageLineQ.getMessage());
                                isRun = false;
                            }
                        }
                    });

                    while (!isEnd[0]){
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            SfLog.getInstance().e(this.getClass(), e);
                        }
                    }
                }
            }
        }).start();
    }
}
