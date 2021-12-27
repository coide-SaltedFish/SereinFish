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
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.pixiv.Pixiv;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.entity.pixiv.entity.PixivEntity;
import sereinfish.bot.entity.pixiv.entity.Rank;
import sereinfish.bot.entity.pixivcat.PixivCat;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Menu(type = Menu.Type.GROUP, name = "Pixiv")
@GroupController
public class PixivController {

    private boolean isRun = false;

    @Before
    public void before(GroupConf groupConf){
        if (!groupConf.isPixivEnable()){
            throw new DoNone();
        }

        if (isRun){
            throw new Message().lineQ().text("模块正在运行").getMessage().toThrowable();
        }
    }


//    @Action("\\^获取[Pp]站日榜$\\")
//    @Synonym({"获取Pixiv日榜", "\\^[Pp]站日榜$\\"})
//    @QMsg(mastAtBot = true)
//    @MenuItem(name = "获取Pixiv日榜前7", usage = "@Bot 获取Pixiv日榜", description = "获取Pixiv日榜前7")
//    public Message getDayRank(Group group, GroupConf groupConf){
//        isRun = true;
//        group.sendMessage("开始获取~");
//        try {
//            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_DAY, 1, groupConf.getPixivRankGetMaxNum());
//            analysisRank(rank, group, groupConf);
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(), e);
//            isRun = false;
//            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
//        }
//        throw new DoNone();
//    }
//
//    @Action("\\^获取[Pp]站周榜$\\")
//    @Synonym({"获取Pixiv周榜", "\\^[Pp]站周榜$\\"})
//    @QMsg(mastAtBot = true)
//    @MenuItem(name = "获取Pixiv周榜前7", usage = "@Bot 获取Pixiv周榜", description = "获取Pixiv周榜前7")
//    public Message getWeekRank(Group group, GroupConf groupConf){
//        isRun = true;
//        group.sendMessage("开始获取~");
//        try {
//            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_WEEK, 1, groupConf.getPixivRankGetMaxNum());
//            analysisRank(rank, group, groupConf);
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(), e);
//            isRun = false;
//            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
//        }
//        throw new DoNone();
//    }
//
//    @Action("\\^获取[Pp]站月榜$\\")
//    @Synonym({"获取Pixiv月榜", "\\^[Pp]站月榜$\\"})
//    @QMsg(mastAtBot = true)
//    @MenuItem(name = "获取Pixiv月榜前7", usage = "@Bot 获取Pixiv月榜", description = "获取Pixiv月榜前7")
//    public Message getMonthRank(Group group, GroupConf groupConf){
//        isRun = true;
//        group.sendMessage("开始获取~");
//        try {
//            Rank rank = Pixiv.getRank(Pixiv.RANK_MODE_MONTH, 1, groupConf.getPixivRankGetMaxNum());
//            analysisRank(rank, group, groupConf);
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(), e);
//            isRun = false;
//            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
//        }
//        throw new DoNone();
//    }

//    @Action("获取Pixiv {type} 榜")
//    @QMsg(mastAtBot = true)
//    @MenuItem(name = "获取Pixiv指定榜前7", usage = "@Bot 获取Pixiv {type} 榜", description = "获取Pixiv指定榜前7")
//    public Message getTypeRank(Group group, GroupConf groupConf, String type){
//        isRun = true;
//        group.sendMessage("开始获取~");
//        try {
//            Rank rank = Pixiv.getRank(type, 1, groupConf.getPixivRankGetMaxNum());
//            analysisRank(rank, group, groupConf);
//        } catch (IOException e) {
//            SfLog.getInstance().e(this.getClass(), e);
//            isRun = false;
//            return new Message().lineQ().textLine("出现了错误：").text(e.getMessage()).getMessage();
//        }
//        throw new DoNone();
//    }

    @Action("\\^[pP]站图片$\\ {uid}")
    @QMsg(mastAtBot = true, reply = true)
    @MenuItem(name = "获取P站指定uid的图片", usage = "@Bot P站图片 {uid}", description = "获取P站指定uid的图片")
    public void getPixivImageUid(GroupConf groupConf, Group group, Member sender, Message message, long uid) throws IOException {
        int showNum = groupConf.getPixivGetMaxNum();
        MessageLineQ messageLineQ = new Message().lineQ();
        group.sendMessage("开始获取~");

        String pageStr = "";
        if (message.getPath().size() >= 3){
            pageStr = message.getPath().get(2).toPath();
        }

        boolean enablePage = false;
        int page = 1;

        if (pageStr != null && !pageStr.equals("")){
            enablePage = true;
        }

        if (enablePage){
            try {
                page = Integer.decode(pageStr);
            }catch (NumberFormatException e){
                SfLog.getInstance().e(this.getClass(), e);
                group.sendMessage(new Message().lineQ().text(pageStr + "?不认识").getMessage());
                return;
            }

            if (page < 1){
                page = 1;
                messageLineQ.textLine("错误已自动纠正：[" + pageStr + "]->[" + page + "]");
            }
        }

        //获取代理
        PixivEntity pixivEntity = Pixiv.getIllust(uid);
        SfLog.getInstance().d(this.getClass(), "图片信息获取完成");
        if (pixivEntity.isError()){
            group.sendMessage(new Message().lineQ().textLine("出现错误了：").text(pixivEntity.getError().getUser_message()).getMessage());
            return;
        }else {
            Illust illust = pixivEntity.getIllust();
            illust.setProxy(groupConf.getPixivProxy());

            messageLineQ.textLine("图片[" + uid + "]：");
            if (page > illust.getPageMax()){
                page = illust.getPageMax();
                messageLineQ.textLine("错误已自动纠正：[" + pageStr + "]->[" + page + "]");
            }

            //是否R18
            if (illust.isR18()){
                messageLineQ.textLine("要找的图片带有R18标签，这里" + MyYuQ.getBotName() + "就不进行展示了哦");
                if (enablePage){
                    messageLineQ.textLine("共" + illust.getPageMax() + "张，以下是第" + page + "张");
                    String proxyUrl = illust.getProxyUrl(page - 1);
                    File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                    try {
                        SfLog.getInstance().d(this.getClass(), "加载(" + page + "/" + illust.getPageMax() + ")：" + proxyUrl);
                        BufferedImage image = QRCodeImage.backgroundMatrix(
                                QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                0.0f,
                                Color.BLACK);
                        ImageIO.write(image, "png", imageFile);
                        try{
                            Image image1 = MyYuQ.uploadImage(group, imageFile);;
                            messageLineQ.plus(image1);
                        }catch (Exception e){
                            SfLog.getInstance().e(this.getClass(), e);
                            messageLineQ.textLine("错误：" + e.getMessage());
                        }
                    } catch (WriterException e) {
                        SfLog.getInstance().e(this.getClass(), e);
                        messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                    }catch (IOException e){
                        SfLog.getInstance().e(this.getClass(), e);
                        messageLineQ.text("唔，二维码图片生成失败了：IOException");
                    }
                }else {
                    //页数
                    int num = illust.getPageMax();
                    if (num > showNum){
                        num = showNum;
                        messageLineQ.textLine("共" + illust.getPageMax() + "张，以下是前" + num + "张");
                    }else {
                        messageLineQ.textLine("共" + illust.getPageMax() + "张");
                    }
                    for(int i = 0; i < num; i++){
                        String proxyUrl = illust.getProxyUrl(i);
                        File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                        try {
                            SfLog.getInstance().d(this.getClass(), "加载(" + (i + 1) + "/" + num + ")：" + proxyUrl);
                            BufferedImage image = QRCodeImage.backgroundMatrix(
                                    QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                    ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                    0.0f,
                                    Color.BLACK);
                            ImageIO.write(image, "png", imageFile);
                            try{
                                Image image1 = MyYuQ.uploadImage(group, imageFile);
                                messageLineQ.plus(image1);
                            }catch (Exception e){
                                SfLog.getInstance().e(this.getClass(), e);
                                messageLineQ.textLine("错误：" + e.getMessage());
                            }
                        } catch (WriterException e) {
                            SfLog.getInstance().e(this.getClass(), e);
                            messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                        }catch (IOException e){
                            SfLog.getInstance().e(this.getClass(), e);
                            messageLineQ.text("唔，二维码图片生成失败了：IOException");
                        }
                    }
                }
                Message message1 = messageLineQ.getMessage();
                message1.setRecallDelay((long) groupConf.getSetuReCallTime() * 1000);
                group.sendMessage(message1);
                return;
            }else {
                if (enablePage){
                    messageLineQ.textLine("共" + illust.getPageMax() + "张，以下是第" + page + "张");
                    String proxyUrl = illust.getProxyUrl(page);
                    SfLog.getInstance().d(this.getClass(), "加载(" + pageStr + "/" + illust.getPageMax() + ")：" + proxyUrl);
                    NetHandle.imagePixivDownload(group, illust, page - 1, new NetworkLoader.NetworkLoaderListener() {
                        @Override
                        public void start(long len) {

                        }

                        @Override
                        public void success(File file) {
                            Image image1 = MyYuQ.uploadImage(group, file);
                            messageLineQ.plus(image1);

                            group.sendMessage(messageLineQ.getMessage());
                        }

                        @Override
                        public void fail(Exception e) {
                            SfLog.getInstance().e(this.getClass(), e);
                            File imageFile = new File(FileHandle.imageCachePath, "qr_" + illust.getId());
                            try {
                                BufferedImage image = QRCodeImage.backgroundMatrix(
                                        QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                        ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                        0.0f,
                                        Color.BLACK);
                                ImageIO.write(image, "png", imageFile);
                                try{
                                    Image image1 = MyYuQ.uploadImage(group, imageFile);
                                    messageLineQ.plus(image1);
                                }catch (Exception e1){
                                    SfLog.getInstance().e(this.getClass(), e1);
                                    messageLineQ.textLine("错误：" + e.getMessage());
                                }
                            } catch (WriterException | IOException writerException) {
                                SfLog.getInstance().e(this.getClass(), e);
                                messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                            }

                            group.sendMessage(messageLineQ.getMessage());
                        }

                        @Override
                        public void progress(long pro, long len, long speed) {

                        }
                    });
                }else {
                    //页数
                    int num = illust.getPageMax();
                    if (num > showNum){
                        num = showNum;
                        messageLineQ.textLine("共" + illust.getPageMax() + "张，以下是前" + num + "张");
                    }else {
                        messageLineQ.textLine("共" + illust.getPageMax() + "张");
                    }
                    for(int i = 0; i < num; i++) {
                        String proxyUrl = illust.getProxyUrl(i);
                        SfLog.getInstance().d(this.getClass(), "加载(" + (i + 1) + "/" + num + ")：" + proxyUrl);

                        NetHandle.imagePixivDownload(group, illust, i, new NetworkLoader.NetworkLoaderListener() {
                            @Override
                            public void start(long len) {

                            }

                            @Override
                            public void success(File file) {
                                Image image1 = MyYuQ.uploadImage(group, file);
                                messageLineQ.plus(image1);

                                group.sendMessage(messageLineQ.getMessage());
                            }

                            @Override
                            public void fail(Exception e) {
                                SfLog.getInstance().e(this.getClass(), e);
                                File imageFile = new File(FileHandle.imageCachePath, "qr_" + illust.getId());
                                try {
                                    BufferedImage image = QRCodeImage.backgroundMatrix(
                                            QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
                                            ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                                            0.0f,
                                            Color.BLACK);
                                    ImageIO.write(image, "png", imageFile);
                                    try{
                                        Image image1 = MyYuQ.uploadImage(group, imageFile);
                                        messageLineQ.plus(image1);
                                    }catch (Exception e1){
                                        SfLog.getInstance().e(this.getClass(), e1);
                                        messageLineQ.textLine("错误：" + e.getMessage());
                                    }
                                } catch (WriterException | IOException writerException) {
                                    SfLog.getInstance().e(this.getClass(), e);
                                    messageLineQ.text("唔，二维码图片生成失败了：WriterException");
                                }

                                group.sendMessage(messageLineQ.getMessage());
                            }

                            @Override
                            public void progress(long pro, long len, long speed) {

                            }
                        });
                    }
                }
            }
        }
    }


//    /**
//     * 排行榜解析
//     * @param rank
//     * @return
//     */
//    private void analysisRank(Rank rank, Group group, GroupConf groupConf){
//        MessageLineQ messageLineQ = new Message().lineQ();
//
//        if (rank.getIllusts() == null || rank.getIllusts().length == 0){
//            group.sendMessage("数据获取失败了");
//            isRun = false;
//            return;
//        }
//        int size = groupConf.getPixivRankGetMaxNum();
//
//        int len = rank.getIllusts().length;
//        if (size > len){
//            size = len;
//        }
//
//        group.sendMessage("共 " + size + " 条数据，努力加载中...");
//
//        int finalSize = size;
//        MyYuQ.getJobManager().registerTimer(new Runnable() {
//            @Override
//            public void run() {
//                boolean isR18 = false;
//                for (int i = 0; i < finalSize; i++){
//
//                    Illust illust = rank.getIllusts()[i];
//                    illust.setProxy(groupConf.getPixivProxy());
//
//                    messageLineQ.textLine((i + 1) + ".");
//                    messageLineQ.textLine("标题：" + illust.getTitle());//标题
//                    messageLineQ.textLine("Pid：" + illust.getId());
//                    String caption = MyYuQ.delHTMLTag(illust.getCaption());
//                    if (caption.length() > 20){
//                        caption = caption.substring(0, 20) + "...";
//                    }
//                    messageLineQ.textLine("描述：").textLine(caption);//描述
//                    messageLineQ.textLine("图片：");
//                    //获取链接
//                    String proxyUrl = illust.getProxyUrl();
//
//                    //r18
//                    if (illust.isR18()){
//                        isR18 = true;
//                        File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
//                        try {
//                            BufferedImage image = QRCodeImage.backgroundMatrix(
//                                    QRCodeImage.generateQRCodeBitMatrix(proxyUrl, 800, 800),
//                                    ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
//                                    0.0f,
//                                    Color.BLACK);
//                            ImageIO.write(image, "png", imageFile);
////                            messageLineQ.imageByFile(imageFile);
//                            Image im = group.uploadImage(imageFile);
//                            messageLineQ.plus(im);
//                        } catch (WriterException e) {
//                            SfLog.getInstance().e(this.getClass(), e);
//                            messageLineQ.text("唔，二维码图片生成失败了：WriterException");
//                        }catch (IOException e){
//                            SfLog.getInstance().e(this.getClass(), e);
//                            messageLineQ.text("唔，二维码图片生成失败了：IOException");
//                        }
//                    }else {
//                        try {
//                           Image image = group.uploadImage(NetHandle.imagePixivDownload(illust, 0));
//                            messageLineQ.plus(image);
//                        } catch (IOException e) {
//                            messageLineQ.textLine("图片加载失败：" + e.getMessage());
//                        }
//                    }
//                }
//                Message message = messageLineQ.getMessage();
//                if (isR18){
//                    message.setRecallDelay((long) groupConf.getSetuReCallTime() * 1000);
//                }
//                isRun = false;
//                group.sendMessage(message);
//            }
//        }, 0);
//    }
}
