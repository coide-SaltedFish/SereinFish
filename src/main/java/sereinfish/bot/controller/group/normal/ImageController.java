package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@GroupController
public class ImageController {
    private Member sender;
    private Group group;
    private GroupConf conf;

    @Before
    public void before(Member sender, Group group){
        this.sender = sender;
        this.group = group;

        conf = GroupConfManager.getInstance().get(group.getId());
        if (!conf.isEnable()){
            throw new DoNone();
        }

    }

    /**
     * 丢头像
     * @return
     */
    @Action("\\.*丢.*\\")
    public Message diu(){
        BufferedImage headImage;
        BufferedImage bgImage;

        //触发概率
        if (MyYuQ.getRandom(0,100) > 60){
            throw new DoNone();
        }

        int hdW = 146;

        try {
            headImage = (BufferedImage) ImageHandle.getMemberHeadImage(sender.getId(),hdW);
            bgImage = ImageIO.read(getClass().getClassLoader().getResource("image/diu.png"));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            throw new DoNone();
        }

        //处理头像
        BufferedImage formatAvatarImage = new BufferedImage(hdW, hdW, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = formatAvatarImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        //图片是一个圆型
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, hdW, hdW);
        //需要保留的区域
        graphics.setClip(shape);
        graphics.rotate(Math.toRadians(-50),hdW / 2,hdW / 2);
        graphics.drawImage(headImage.getScaledInstance(hdW,hdW,Image.SCALE_SMOOTH), 0, 0, hdW, hdW, null);
        graphics.dispose();

        //重合图片
        Graphics2D graphics2D = bgImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        graphics2D.drawImage(formatAvatarImage,110 - hdW / 2,275 - hdW / 2,hdW,hdW,null);//头画背景上
        graphics2D.dispose();

        //先保存为图片
        File file = new File(FileHandle.imageCachePath,"diu_" + new Date().getTime());
        try {
            ImageIO.write(bgImage, "PNG", file);
            return MyYuQ.getMif().imageByFile(file).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
        }
    }
}
