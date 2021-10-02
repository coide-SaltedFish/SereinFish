package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.SkipMe;
import com.icecreamqaq.yuq.message.Message;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.file.image.gif.AnimatedGifEncoder;
import sereinfish.bot.file.image.gif.GifDecoder;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Map;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "图片生成")
public class ImageController extends QQController {
    private int paIndex = 16;

    private int maxTime = 15000;

    @Before
    public void before(){
        //创建缓存路径
        if (!FileHandle.imageCachePath.exists() || FileHandle.imageCachePath.isFile()){
            FileHandle.imageCachePath.mkdirs();
        }
    }

    /**
     * 丢头像
     * @return
     */
    @Action("丢 {member}")
    @Synonym({"扔 {member}", "diu {member}"})
    @MenuItem(name = "丢某人头像", usage = "[丢、扔、diu] {member}", description = "把指定对象头像扔出去")
    public Message diuAt(Group group,Member sender ,String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }
        return getDiu(qq);
    }

    /**
     * 丢头像
     * @return
     */
    @Action("\\.?丢.?\\")
    @Synonym("\\.?扔.?\\")
    @MenuItem(name = "丢触发者头像", usage = ".?丢.?", description = "把触发者头像扔出去")
    public Message diu(Member sender){
        //触发概率
        if (MyYuQ.getRandom(0,100) > 2){
            throw new SkipMe();
        }

        return getDiu(sender.getId());
    }


    @Action("爬 {member}")
    @MenuItem(name = "爬", usage = "爬 {member}", description = "生成指定对象爬表情")
    public Message pa(Group group, String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }

        if (MyYuQ.getRandom(0,100) > 50){
            return getPa(qq);
        }else {
            return getSuperPa(qq);
        }
    }

    @Action("爬")
    @MenuItem(name = "爬", usage = "爬", description = "生成触发者爬表情")
    public Message pa_2(Member sender){
        //触发概率
        if (MyYuQ.getRandom(0,100) > 2){
            throw new SkipMe();
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
    @MenuItem(name = "恰", usage = "嚼 {member} | 恰 {member}", description = "生成恰指定对象表情")
    public Message jiao(Group group, String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }
        return getJiao(qq);
    }

    @Action("mua {member}")
    @Synonym({"mua {member}"})
    @MenuItem(name = "mua", usage = "mua {member}", description = "生成mua指定对象表情")
    public Message mua(Group group, String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }
        return getMuaGif(qq);
    }

    @Action("摸 {member}")
    @Synonym({"rua {member}"})
    @MenuItem(name = "rua", usage = "rua {member} | 摸 {member}", description = "生成rua指定对象表情")
    public Message mo(Group group, String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }
        return getRua(qq);
    }

    @Action("蹭 {member}")
    @MenuItem(name = "蹭", usage = "蹭 {member}", description = "生成蹭指定对象表情")
    public Message ceng(Group group, String member){
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
        }catch (Exception e){
            //是名称
            if (member.equals(group.getBot().getName()) || member.equals(group.getBot().getNameCard())){
                qq = group.getBot().getId();
            }else {
                for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
                    if (entry.getValue().getNameCard().equals(member) || entry.getValue().getName().equals(member)){
                        qq = entry.getKey();
                        break;
                    }
                }
            }
            if (qq == -1){
                throw new SkipMe();
            }
        }
        return getCengGif(group, qq);
    }


    @Action("摸")
    @Synonym({"rua"})
    @MenuItem(name = "rua", usage = "rua | 摸", description = "生成rua触发者表情")
    public Message mo_2(Member sender){
        return getRua(sender.getId());
    }

    @Action("\\[.!！]读懂世界$\\")
    @MenuItem(name = "读懂世界", usage = "[.!！]读懂世界", description = "生成今日热点新闻图片")
    public Message readTheWorld(Group group){
        try {
//            File file = new File(FileHandle.imageCachePath, "/" + new Date().getTime());
//            FileOutputStream outputStream = new FileOutputStream(file);
            InputStream inputStream = OkHttpUtils.getByteStream("https://api.03c3.cn/zb/index.php");

//            byte[] b = new byte[1024];
//            int length;
//            while((length = inputStream.read(b)) != -1){
//                outputStream.write(b,0,length);
//            }
//            System.out.println(inputStream.available());
//            System.out.println(length);
//            inputStream.close();
//            outputStream.close();

            return MyYuQ.getMif().imageByInputStream(inputStream).toMessage();
        } catch (Exception e) {
            SfLog.getInstance().e(this.getClass(),e);
            return MyYuQ.getMif().text("在读取世界数据时出现了一点错误").toMessage();
        }
    }

    /**
     * 得到一个丢消息
     * @param m
     * @return
     */
    private Message getDiu(long m){
        BufferedImage headImage;
        BufferedImage bgImage;

        int hdW = 250;

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
        graphics.rotate(Math.toRadians(-40),hdW / 2,hdW / 2);
        graphics.drawImage(headImage.getScaledInstance(hdW,hdW, java.awt.Image.SCALE_SMOOTH), 0, 0, hdW, hdW, null);
        graphics.dispose();

        //重合图片
        BufferedImage bufferedImage = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        graphics2D.drawImage(formatAvatarImage,134,220,hdW,210,null);//头画上
        graphics2D.drawImage(bgImage, 0, 0, null);//背景画上
        graphics2D.dispose();

        //先保存为图片
        File file = new File(FileHandle.imageCachePath,"diu_temp");
        try {
            ImageIO.write(bufferedImage, "PNG", file);
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
        graphics.drawImage(headImage.getScaledInstance(hdW,hdW, java.awt.Image.SCALE_SMOOTH), 0, 0, hdW, hdW, null);
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
        animatedGifEncoder.setRepeat(0);
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
        animatedGifEncoder.setRepeat(0);
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
        animatedGifEncoder.setRepeat(0);

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

    /**
     * 得到一个动态的mua
     * @param m
     * @return
     */
    public Message getMuaGif(long m){
        int bgWH = 240;//背景宽高
        int delay = 100;//每张图之间的延迟
        BufferedImage headImage = (BufferedImage) ImageHandle.getMemberHeadImage(m,80);//得到头像
        File imageFile = new File(FileHandle.imageCachePath,"mua_temp");//文件缓存路径

        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setSize(bgWH,bgWH);
        animatedGifEncoder.start(imageFile.getAbsolutePath());
        animatedGifEncoder.setDelay(delay);
        animatedGifEncoder.setRepeat(0);

        //x,y,w,h
        int imageHeadInfo[][] = {
                {46,117,62,64},//1
                {68,107,63,66},//2
                {76,107,58,69},//3
                {55,123,58,63},//4
                {66,123,56,68},//5
                {71,122,54,66},//6
                {24,146,57,56},//7
                {32,128,71,72},//8
                {73,110,55,72},//9
                {57,118,54,65},//10
                {76,114,60,69},//11
                {47,137,56,66},//12
                {22,149,68,65}//13
        };//头像位置信息

        for (int i = 0; i < 13; i++){
            BufferedImage bgImage;
            try {
                //得到背景
                bgImage = ImageIO.read(getClass().getClassLoader().getResource("image/mua/" + (i + 1) + ".png"));
                //空白底
                BufferedImage bufferedImage = new BufferedImage(bgWH,bgWH,BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0,0,bgWH,bgWH);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
                //先画头
                graphics2D.drawImage(headImage,imageHeadInfo[i][0],imageHeadInfo[i][1],imageHeadInfo[i][2],imageHeadInfo[i][3],null);
                //画背景
                graphics2D.drawImage(bgImage,0,0,bgWH,bgWH,null);
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

    /**
     * 得到一个蹭动图
     * @return
     */
    private Message getCengGif(Group group, long m){
        int bgW = 240;
        int bgH = 240;
        int delay = 50;//每张图之间的延迟

        BufferedImage headImage = ImageHandle.imageToBufferedImage(CacheManager.getMemberHeadImage(m));//得到头像
        File imageFile = new File(FileHandle.imageCachePath,"ceng_temp_" + new Date().getTime());//文件缓存路径

        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setSize(bgW,bgH);
        animatedGifEncoder.start(imageFile.getAbsolutePath());
        animatedGifEncoder.setDelay(delay);
        animatedGifEncoder.setRepeat(0);

        //x,y,w,h
        int imageHeadInfo[][] = {
                {37,85,72,85},//1
                {46,98,74,83},//2
                {65,97,74,82},//3
                {51,81,74,87},//4
                {57,109,71,82},//5
                {60,100,58,81},//6
        };//头像位置信息

        for (int i = 0; i < imageHeadInfo.length; i++){
            BufferedImage bgImage;
            try {
                //得到背景
                bgImage = ImageIO.read(getClass().getClassLoader().getResource("image/ceng/" + (i + 1) + ".png"));
                //空白底
                BufferedImage bufferedImage = new BufferedImage(bgW,bgH,BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0,0,bgW,bgH);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
                //先画头
                graphics2D.drawImage(headImage,imageHeadInfo[i][0],imageHeadInfo[i][1],imageHeadInfo[i][2],imageHeadInfo[i][3],null);
                //画背景
                graphics2D.drawImage(bgImage,0,0,bgW,bgH,null);
                graphics2D.dispose();
                animatedGifEncoder.addFrame(bufferedImage);
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),e);
                return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
            }
        }

        if(animatedGifEncoder.finish()){
            try {
                String md5 = MyYuQ.uploadImage(group, imageFile);
                return Message.Companion.toMessageByRainCode("<Rain:Image:" + md5 + ".gif>");
            } catch (IOException e) {
                return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
            }
        }
        return Message.Companion.toMessageByRainCode("在生成图片时出现了一点点错误");
    }
}