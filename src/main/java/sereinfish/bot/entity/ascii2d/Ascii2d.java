package sereinfish.bot.entity.ascii2d;

import com.google.zxing.WriterException;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.entity.pixiv.Pixiv;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;
import sereinfish.bot.utils.QRCodeImage;
import sereinfish.bot.utils.UA;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ascii2d {
    private String api = "https://ascii2d.net/";

    private String tokenName = "";
    private String token = "";
    private String cookie = "";
    private String urlId = "";

    private String url;//要搜索的图像链接


    public Ascii2d(String url) throws IOException {
        this.url = url;
        getInfo();
        getSearchUrl();
    }

    /**
     * 解析颜色搜索结果页
     */
    public Ascii2dData getColorResponse() throws IOException {
        Headers headers = new Headers.Builder()
                .add("cookie", "_session_id=" + cookie)
                .add("User-Agent", UA.PC.getValue())
                .build();

        Document document = Jsoup.parse(OkHttpUtils.getStr("https://ascii2d.net/search/color/" + urlId, headers));

        return new Ascii2dData(document);
    }

    /**
     * 解析特征搜索结果页
     */
    public Ascii2dData getBovwResponse() throws IOException {
        Headers headers = new Headers.Builder()
                .add("cookie", "_session_id=" + cookie)
                .add("User-Agent", UA.PC.getValue())
                .build();

        Document document = Jsoup.parse(OkHttpUtils.getStr("https://ascii2d.net/search/bovw/" + urlId, headers));

        return new Ascii2dData(document);
    }

    private void getSearchUrl() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("utf8","%E2%9C%93");
        map.put(tokenName, token);
        map.put("uri", url);
        map.put("search", "");

        Headers headers = new Headers.Builder()
                .add("cookie", "_session_id=" + cookie)
                .add("User-Agent", UA.PC.getValue())
                .build();

        Response response = OkHttpUtils.post("https://ascii2d.net/search/uri", map, headers);
        String u = response.header("location");
        urlId = u.substring(u.lastIndexOf("/") + 1);
    }



    private void getInfo() throws IOException {
        Response response = OkHttpUtils.get(api);
        String str = response.header("set-cookie");
        cookie = str.substring(str.indexOf("=") + 1, str.indexOf(";"));

        Document document = Jsoup.parse(response.body().string());
        for (Element element:document.select("meta[name]")){
            if (element.attr("name").equals("csrf-param")){
                tokenName = element.attr("content");
            }

            if (element.attr("name").equals("csrf-token")){
                token = element.attr("content");
            }
        }
    }

    public class Ascii2dData{
        private Document document;

        public Ascii2dData(Document document) {
            this.document = document;
        }

        public Message getInfo(Contact contact) throws IOException {
            boolean isR18 = false;

            MessageLineQ messageLineQ = new Message().lineQ().textLine("Ascii2d 检索结果");

            if (document.select("div[class=row item-box]").size() < 2){
                return new Message().lineQ().text("未搜索到结果").getMessage();
            }

            for (Element element:document.select("div[class=row item-box]")){
                try{
                    Element linkElement = element.select("div[class=detail-box gray-link]").get(0);
                    //来源
                    String source = linkElement.select("small").get(0).text();
                    messageLineQ.textLine("来源：" + source);

                    if (source.equals("pixiv")){
                        Element element1 = element.select("div[class=detail-box gray-link]").get(0);
                        String link = element1.select("a[target=_blank]").get(0).attr("href");
                        long id = Long.valueOf(link.substring(link.lastIndexOf("/") + 1));
                        Illust illust = Pixiv.getIllust(id).getIllust();
                        illust.setProxy(ConfManager.getInstance().get(contact.getId()).getPixivProxy());

                        if (illust.isR18G()){
                            messageLineQ.textLine("注意：R18G警告！！！！");
                        }

                        getPixivInfo(contact, messageLineQ, illust);

                        if (illust.isR18() || illust.isR18G()){
                            isR18 = true;
                        }

                    }else {
                        //messageLineQ.textLine("来源链接：" + linkElement.select("a[target=_blank]").get(0).text());
                        //图片链接
                        messageLineQ.textLine("图片链接：" + linkElement.select("a[target=_blank]").get(0).attr("href"));

                        //预览图
                        messageLineQ.text("预览图：");
                        String link = "https://ascii2d.net" + element.select("img[loading=lazy]").get(0).attr("src");

                        File file = NetHandle.imageDownload(link, "Ascii2d_" + System.currentTimeMillis());
                        Image image = contact.uploadImage(file);
                        messageLineQ.plus(image);
                    }

                    //图片信息
                    if (element.select("small[class=text-muted]").size() > 0){
                        messageLineQ.text(element.select("small[class=text-muted]").get(0).text());
                    }

                    Message message = messageLineQ.getMessage();

                    if (isR18){
                        message.setRecallDelay((long) ConfManager.getInstance().get(contact.getId()).getSetuReCallTime() * 1000);
                    }
                    return message;
                }catch (IOException e){
                    messageLineQ.textLine("错误：");
                    Message message = messageLineQ.text(e.getMessage()).getMessage();

                    if (isR18){
                        message.setRecallDelay((long) ConfManager.getInstance().get(contact.getId()).getSetuReCallTime() * 1000);
                    }

                    return message;
                }catch (Exception e){
                    SfLog.getInstance().w(this.getClass(), "解析错误");
                    messageLineQ = new Message().lineQ().textLine("Ascii2d 检索结果");
                }
            }

            return new Message().lineQ().text("未搜索到结果").getMessage();
        }

        private void getPixivInfo(Contact contact, MessageLineQ messageLineQ, Illust illust) throws IOException {
            messageLineQ.textLine("作者：" + illust.getUser().getName());
            messageLineQ.textLine("标题：" + illust.getTitle());

            messageLineQ.textLine("描述：" + MyYuQ.textLengthLimit(illust.getCaption(), 30));

            messageLineQ.textLine("Tags：");
            for (int i = 0; i < (illust.getTags().length > 4?4:illust.getTags().length); i++){
                Illust.Tag tag = illust.getTags()[i];
                messageLineQ.textLine(tag.getName() + "[" + tag.getTranslated_name() + "]");
            }
            if (illust.getTags().length > 4){
                messageLineQ.textLine("...");
            }else {
                messageLineQ.textLine("");
            }

            //预览图
            messageLineQ.text("原图（也许是？");

            if (illust.isR18()){
                //生成二维码
                File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
                try {
                    BufferedImage image = QRCodeImage.backgroundMatrix(
                            QRCodeImage.generateQRCodeBitMatrix(illust.getProxyUrl(), 800, 800),
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
                messageLineQ.plus(contact.uploadImage(imageFile));
            }else {
                File file = NetHandle.imagePixivDownload(illust, 0);
                Image image = contact.uploadImage(file);
                messageLineQ.plus(image);
            }

            //图片链接
            messageLineQ.textLine("链接：\nhttps://www.pixiv.net/artworks/" + illust.getId());
        }
    }
}
