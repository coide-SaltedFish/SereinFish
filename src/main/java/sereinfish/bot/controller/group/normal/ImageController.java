package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.image.gif.AnimatedGifEncoder;
import sereinfish.bot.file.image.gif.GifDecoder;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@GroupController
public class ImageController {
    private Member sender;
    private Group group;
    private GroupConf conf;

    private int paIndex = 16;

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
    @Action("丢 {member}")
    public Message diuAt(long member){
        if (!group.getMembers().containsKey(member) && member != MyYuQ.getYuQ().getBotId()){
            throw new SkipMe();
        }
        return getDiu(member);
    }

    /**
     * 丢头像
     * @return
     */
    @Action("\\.?丢.?\\")
    public Message diu(){
        final Member sender = this.sender;

        //触发概率
        if (MyYuQ.getRandom(0,100) > 60){
            throw new DoNone();
        }

        return getDiu(sender.getId());
    }


    @Action("爬 {member}")
    public Message pa(long member){
        if (!group.getMembers().containsKey(member) && member != MyYuQ.getYuQ().getBotId()){
            throw new SkipMe();
        }

        if (MyYuQ.getRandom(0,100) > 50){
            return getPa(member);
        }else {
            return getSuperPa(member);
        }
    }

    @Action("爬")
    public Message pa_2(){
        //触发概率
        if (MyYuQ.getRandom(0,100) > 60){
            throw new DoNone();
        }
        final long member = sender.getId();

        if (MyYuQ.getRandom(0,100) > 50){
            return getPa(member);
        }else {
            return getSuperPa(member);
        }
    }


    @Action("嚼 {member}")
    @Synonym({"恰 {member}"})
    public Message jiao(long member){
        if (!group.getMembers().containsKey(member) && member != MyYuQ.getYuQ().getBotId()){
            throw new SkipMe();
        }
        return getJiao(member);
    }

    @Action("摸 {member}")
    @Synonym({"rua {member}"})
    public Message mo(long member){
        if (!group.getMembers().containsKey(member) && member != MyYuQ.getYuQ().getBotId()){
            throw new SkipMe();
        }
        return getRua(member);
    }

    @Action("摸")
    @Synonym({"rua"})
    public Message mo_2(){
        final Member sender = this.sender;//线程安全
        return getRua(sender.getId());
    }

    /**
     * 得到一个丢消息
     * @param m
     * @return
     */
    private Message getDiu(long m){
        BufferedImage headImage;
        BufferedImage bgImage;

        int hdW = 146;

        try {
            headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,hdW);
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
        File file = new File(FileHandle.imageCachePath,"diu_temp");
        try {
            ImageIO.write(bgImage, "PNG", file);
            return MyYuQ.getMif().imageByFile(file).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
        }
    }

    /**
     * 得到一个爬消息
     * @param m
     * @return
     */
    private Message getPa(long m){
        BufferedImage headImage;
        BufferedImage bgImage;
        int hdW = 65;

        try {
            headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,hdW);
            bgImage = ImageIO.read(getClass().getClassLoader().getResource("image/pa/" + MyYuQ.getRandom(1,16) + ".jpg"));
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
        graphics.drawImage(headImage.getScaledInstance(hdW,hdW,Image.SCALE_SMOOTH), 0, 0, hdW, hdW, null);
        graphics.dispose();

        //重合图片
        Graphics2D graphics2D = bgImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        graphics2D.drawImage(formatAvatarImage,0,bgImage.getHeight() - hdW,hdW,hdW,null);//头画背景上
        graphics2D.dispose();

        //先保存为图片
        File file = new File(FileHandle.imageCachePath,"pa_temp");
        try {
            ImageIO.write(bgImage, "PNG", file);
            return MyYuQ.getMif().imageByFile(file).toMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
        }
    }

    /**
     * 得到一个jiao消息
     * @return
     */
    private Message getJiao(long m){
        File imageFile = new File(FileHandle.imageCachePath,"jiao_temp");
        BufferedImage jiao_top = null;
        try {
            jiao_top = ImageIO.read(getClass().getClassLoader().getResource("image/jiao/jiao_top"));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(),e);
            return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
        }

        GifDecoder decoder = new GifDecoder();
        int status = 0;
        //w:60 h:67
        status = decoder.read(getClass().getResourceAsStream("/image/jiao/jiao.gif"));
        if (status != GifDecoder.STATUS_OK) {
            return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误:" + status);
        }
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        //保存的目标图片
        animatedGifEncoder.start(imageFile.getAbsolutePath());
        animatedGifEncoder.setRepeat(decoder.getLoopCount());
        animatedGifEncoder.setDelay(decoder.getDelay(0));
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            BufferedImage image = decoder.getFrame(i);
            //加入头像  直径：38
            int f = 38;
            //得到头
            BufferedImage headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,f);
            //编辑头，加缺口
            Graphics2D headGraphics2D = headImage.createGraphics();
            headGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
            headGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
            headGraphics2D.fillOval(21, 0, 8, 10);//画椭圆
            headGraphics2D.dispose();
            //把头放上去
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
            graphics.drawImage(headImage,0,image.getHeight() - headImage.getHeight() + 6,f,f,null);//头画背景上
            //手
            graphics.drawImage(jiao_top,0,0,image.getWidth(),image.getHeight(),null);//把手画上
            graphics.dispose();

            animatedGifEncoder.addFrame(image);
        }
        if(animatedGifEncoder.finish()){
            return MyYuQ.getMif().imageByFile(new File(FileHandle.imageCachePath,"jiao_temp")).toMessage();
        }

        return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
    }

    /**
     * 得到一个rua消息
     * @param m
     * @return
     */
    private Message getRua(long m){
        File imageFile = new File(FileHandle.imageCachePath,"mo_temp");
        int hw = 80;//头像宽度
        //得到头像
        BufferedImage headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,hw);
        //得到mo的gif
        GifDecoder decoder = new GifDecoder();
        int status = 0;
        //w:60 h:67
        status = decoder.read(getClass().getResourceAsStream("/image/mo/mo.gif"));
        if (status != GifDecoder.STATUS_OK) {
            return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误:" + status);
        }
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        //保存的目标图片
        animatedGifEncoder.start(imageFile.getAbsolutePath());
        animatedGifEncoder.setRepeat(decoder.getLoopCount());
        animatedGifEncoder.setDelay(decoder.getDelay(0));

        for (int i = 0; i < decoder.getFrameCount(); i++) {
            BufferedImage image = decoder.getFrame(i);
            //清除背景
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setColor(Color.getColor("#fffbf3"));
            graphics2D.fillRect(0,0,image.getWidth(),image.getHeight());
            //开始生成
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
            try {
                int mfd = 15;//最大幅度
                int fd = 0;//幅度
                //得到手
                BufferedImage shou = ImageIO.read(getClass().getClassLoader().getResource("image/mo/" + (i + 1) + ".png"));
                int y = (image.getHeight() - hw - 10);
                //拉伸头
                switch (i + 1){
                    case 1:
                        fd = 0;
                        graphics2D.drawImage(headImage,20 - (fd / 2), y + fd,hw + fd, hw - fd,null);
                        break;
                    case 2:
                        fd = mfd / 2;
                        graphics2D.drawImage(headImage,20 - (fd / 2), y + fd,hw + fd, hw - fd,null);
                        break;
                    case 3:
                        fd = mfd;
                        graphics2D.drawImage(headImage,20 - (fd / 2),y + fd,hw + fd, hw - fd,null);
                        break;
                    case 4:
                        fd = mfd / 2 + 1;
                        graphics2D.drawImage(headImage,20 - (fd / 2),y + fd,hw + fd, hw - fd,null);
                        break;
                    case 5:
                        fd = 1;
                        graphics2D.drawImage(headImage,20,y,hw,hw,null);
                        break;
                }
                //放手
                graphics2D.drawImage(shou,0,0,shou.getWidth(),shou.getHeight(),null);
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),e);
                return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
            }
            graphics2D.dispose();

            animatedGifEncoder.addFrame(image);
        }
        if(animatedGifEncoder.finish()){
            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        }
        return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
    }

    /**
     * 得到一个动态的爬
     * @param m
     * @return
     */
    private Message getSuperPa(long m){
        int w = 360;
        int h = 360;
        int delay = 50;
        int hdW = 65;

        File imageFile = new File(FileHandle.imageCachePath,"sPa_temp");

        BufferedImage headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,hdW);;

        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setSize(w,h);
        animatedGifEncoder.start(imageFile.getAbsolutePath());
        animatedGifEncoder.setDelay(delay);
        animatedGifEncoder.setRepeat(paIndex);

        for (int i = 0; i < paIndex; i++){
            BufferedImage bgImage;
            try {
                bgImage = ImageIO.read(getClass().getClassLoader().getResource("image/pa/" + (i + 1) + ".jpg"));
                BufferedImage bufferedImage = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
                graphics2D.drawImage(bgImage,0,0,w,h,null);
                graphics2D.drawImage(headImage,0,h - hdW,hdW,hdW,null);//头画背景上
                graphics2D.dispose();
                animatedGifEncoder.addFrame(bufferedImage);
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),e);
                return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
            }
        }

        if(animatedGifEncoder.finish()){
            return MyYuQ.getMif().imageByFile(imageFile).toMessage();
        }
        return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
    }
}
