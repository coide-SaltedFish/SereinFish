package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.database.table.GroupHistoryMsg;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.msg.GroupHistoryMsgDBManager;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.utils.QRCodeImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "二维码", permissions = Permissions.NORMAL)
public class QrController {

    @Action("二维码 {text}")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "二维码生成", usage = "@bot 二维码 {text} {img(可选)}", description = "让猫砂帮你生成一张还行的二维码")
    public Message qr(Group group, Message message, String text){
        group.sendMessage("正在处理~");

        Image img = null;
        for (MessageItem messageItem:message.getBody()){
            if (messageItem instanceof Image){
                img = (Image) messageItem;
                break;
            }
        }

        MessageLineQ messageLineQ = new Message().lineQ();
        File imageFile = new File(FileHandle.imageCachePath, "/QR_" + new Date().getTime());
        BufferedImage bufferedImage;
        if (img == null){
            try {
                BufferedImage qrImage = QRCodeImage.backgroundMatrix(
                        QRCodeImage.generateQRCodeBitMatrix(text, 800, 800),
                        ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png")),
                        0f,
                        new Color(20, 78, 88));
                ImageIO.write(qrImage, "png", imageFile);
                messageLineQ.imageByFile(imageFile);
            } catch (WriterException e) {
                messageLineQ.text("唔，二维码图片生成失败了：WriterException");
            } catch (IOException e) {
                messageLineQ.text("唔，二维码图片生成失败了：IOException");
            }
        }else {
            try {
                bufferedImage = ImageIO.read(new URL(img.getUrl()));
                BufferedImage qrImage = QRCodeImage.backgroundMatrix(
                        QRCodeImage.generateQRCodeBitMatrix(text, 800, 800),
                        bufferedImage,
                        0.3f,
                        Color.BLACK);
                ImageIO.write(qrImage, "png", imageFile);
                messageLineQ.imageByFile(imageFile);
            } catch (IOException e) {
                messageLineQ.text("图片读取出错了");
            } catch (WriterException e) {
                messageLineQ.text("唔，二维码图片生成失败了：WriterException");
            }
        }

        Message msg = messageLineQ.getMessage();
        msg.setReply(message.getSource());
        return msg;
    }

    @Action("扫码")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "二维码扫描", usage = "@bot 扫码 {qr Image}", description = "让猫砂帮你解析二维码")
    public Message scanQr(Group group, Message message){
        group.sendMessage("开始解析~");
        String imageUrl = null;
        //是回复
        if (message.getReply() != null){
            //检查回复的图片
            GroupHistoryMsg groupHistoryMsg = null;
            try {
                groupHistoryMsg = GroupHistoryMsgDBManager.getInstance().query(group.getId(), message.getReply().getId());
                if (groupHistoryMsg == null){
                    return new Message().lineQ().text("找不到该消息，可能是消息未被记录:" + message.getReply().getId()).getMessage();
                }
                Message replayMsg = groupHistoryMsg.getMessage();
                for (MessageItem messageItem:replayMsg.getBody()){
                    if (messageItem instanceof Image){
                        Image image = (Image) messageItem;
                        imageUrl = "http://gchat.qpic.cn/gchatpic_new/0/-0-" + image.getId().substring(0, image.getId().lastIndexOf(".")) + "/0";
                        break;
                    }
                }
            } catch (SQLException e) {
                SfLog.getInstance().e(this.getClass(),e);
                return new Message().lineQ().text("发生错误了：" + e.getMessage()).getMessage();
            }
        }else {
            for (MessageItem messageItem:message.getBody()){
                if (messageItem instanceof Image){
                    Image image = (Image) messageItem;
                    imageUrl = image.getUrl();
                }
            }
        }

        //开始扫描
        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            Message msg = new Message().lineQ().text(QRCodeImage.getQrResult(image).getText()).getMessage();
            msg.setReply(message.getSource());
            return msg;
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            Message msg = new Message().lineQ().text("图片读取出错了").getMessage();
            msg.setReply(message.getSource());
            return msg;
        } catch (NotFoundException e) {
            SfLog.getInstance().e(this.getClass(), e);
            Message msg =  new Message().lineQ().text("未在图片中检出二维码").getMessage();
            msg.setReply(message.getSource());
            return msg;
        }
    }
}
