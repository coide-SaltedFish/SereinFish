package sereinfish.bot.file.image;

import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;

import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sun.font.FontDesignMetrics;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;

/**
 * 一些图像处理方法
 */
public class ImageHandle {

    /**
     * 得到群头像
     * @param group
     * @param size
     * @return
     */
    public static Image getGroupHeadImage(long group,int size){
        return getHeadImageNoFrame(CacheManager.getGroupHeadImage(group),size,size);
    }

    /**
     * 得到qq头像
     * @param qq
     * @param size
     * @return
     */
    public static Image getMemberHeadImage(long qq,int size){
        return getHeadImageNoFrame(CacheManager.getMemberHeadImage(qq),size,size);
    }

    /**
     * 得到圆头像
     * @param image
     * @param width
     * @param high
     * @return
     */
    public static Image getHeadImageNoFrame(Image image, int width, int high){
        // 透明底的图片
        BufferedImage formatAvatarImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = formatAvatarImage.createGraphics();
        //把图片切成一个园
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
        int border = 0;
        //图片是一个圆型
        Ellipse2D.Double shape = new Ellipse2D.Double(border, border, width - border * 2, width - border * 2);
        //需要保留的区域
        graphics.setClip(shape);
        graphics.drawImage(image.getScaledInstance(width,high,Image.SCALE_SMOOTH), border, border, width - border * 2, width - border * 2, null);
        graphics.dispose();
        //在圆图外面再画一个圆
        //新创建一个graphics，这样画的圆不会有锯齿
        graphics = formatAvatarImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int border1 = 1;
        //画笔是4.5个像素，BasicStroke的使用可以查看下面的参考文档
        //使画笔时基本会像外延伸一定像素，具体可以自己使用的时候测试
        Stroke s = new BasicStroke(1F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(s);
        graphics.setColor(Color.WHITE);
        graphics.drawOval(border1, border1, width - border1 * 2, width - border1 * 2);
        graphics.dispose();

        return formatAvatarImage;
    }

    /**
     * Image 转 Buf
     * @param image
     * @return
     */
    public static BufferedImage imageToBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.TRANSLUCENT;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            SfLog.getInstance().e(ImageHandle.class,e);
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_4BYTE_ABGR;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    /**
     * 消息转图片
     * @param message
     * @return
     */
    public static BufferedImage messageToImage(Message message, GroupConf conf){
        Color bgColor = Color.decode("#EEEEEE");//背景颜色
        Color paintColor = Color.decode("#212121");//画笔颜色
        String mFont = conf.getLongMsgToImageFont();
        int fontSize = conf.getLongMsgToImageFontSize();//文本大小
        int margin = conf.getLongMsgToImageMargins();//生成图像边距
        int maxWidth = 1080;//最大图片宽度大小
        int maxLineWidth = maxWidth - (margin * 2);//最大行宽
        int lineMaxWidth = 0;//最大文字宽度
        int lineHeight = 0;//行高

        BufferedImage bufferedOldImage = null;
        //设置字体
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        //判断字体是否是文件
        Font font = new Font(fonts[0],Font.PLAIN,fontSize);
        if(Arrays.asList(fonts).contains(mFont)){
            font = new Font(mFont,Font.PLAIN,fontSize);
        }else {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(mFont));
                font = font.deriveFont(Font.PLAIN, fontSize * 1.0f);
            } catch (FontFormatException e) {
                SfLog.getInstance().e(ImageHandle.class, e);
            } catch (IOException e) {
                SfLog.getInstance().e(ImageHandle.class, e);
            }
        }

        //解析消息数据
        for (MessageItem item:message.getBody()){


            if (item instanceof Text){
                Text text = (Text) item;
                String str = text.getText();
                //计算位置并开始绘制图像
                int lineWidth = 0;//当前行宽度
                int line = 0;//当前行数
                int lineStartIndex = 0;//当前行开始处标记
                int textLineHeight =  FontDesignMetrics.getMetrics(font).getHeight();//文本行高

                FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
                for (int i = 0; i < str.length(); i++) {
                    lineWidth += metrics.charWidth(str.charAt(i));
                    if (lineWidth >= maxLineWidth || i == str.length() - 1 || str.charAt(i) == '\n'){
                        if (lineWidth > lineMaxWidth){
                            lineMaxWidth = lineWidth;
                        }
                        //获取行文本
                        if (i == str.length() - 1){
                            i++;
                        }
                        String strLine = str.substring(lineStartIndex, i);
                        lineHeight += textLineHeight;
                        //画板拓展
                        BufferedImage bufferedImage = new BufferedImage(lineMaxWidth, lineHeight, BufferedImage.TYPE_4BYTE_ABGR);//本行画板
                        //绘制文本
                        Graphics2D graphics2D = bufferedImage.createGraphics();
                        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);;//抗锯齿
                        graphics2D.setFont(font);
                        if (bufferedOldImage != null){
                            graphics2D.drawImage(bufferedOldImage, 0, 0, null);//将旧数据绘制上去
                        }
                        graphics2D.setPaint(paintColor);
                        graphics2D.drawString(strLine, 0, lineHeight - textLineHeight + metrics.getAscent());
                        graphics2D.dispose();

                        bufferedOldImage = bufferedImage;
                        //标记更新
                        lineWidth = 0;
                        lineStartIndex = i;
                        line++;
                    }
                }

            }else if (item instanceof com.icecreamqaq.yuq.message.Image){
                com.icecreamqaq.yuq.message.Image image = (com.icecreamqaq.yuq.message.Image) item;
                BufferedImage msgImage = null;
                try {
                    URL url = null;
                    if (image.getUrl().equals("") || image.getUrl() == null){
                        url = new URL("http://gchat.qpic.cn/gchatpic_new/0/-0-" + image.getId() + "/0");
                    }else {
                        url = new URL(image.getUrl());
                    }
                    msgImage = (BufferedImage) NetHandle.getImage(url);
                } catch (IOException e) {
                    //获取失败，返回一个错误图片
                    msgImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
                    msgImage.createGraphics().drawString("图片加载失败",0,0);
                }

                if (msgImage.getWidth() > lineMaxWidth){
                    if (msgImage.getWidth() > maxWidth){
                        lineMaxWidth = maxWidth;
                        //等比缩小图片
                        int mwh = (int) (maxWidth * 1.0) / msgImage.getWidth() * msgImage.getHeight();
                        BufferedImage msgImageTmp = new BufferedImage(maxWidth, mwh, BufferedImage.TYPE_4BYTE_ABGR);
                        Graphics2D graphics2D = msgImageTmp.createGraphics();
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
                        graphics2D.drawImage(msgImage.getScaledInstance(maxWidth,mwh,Image.SCALE_SMOOTH), 0, 0, maxWidth, mwh, null);
                        graphics2D.dispose();

                        msgImage = msgImageTmp;
                    }else {
                        lineMaxWidth = msgImage.getWidth();
                    }
                }
                lineHeight += msgImage.getHeight();
                BufferedImage bufferedImage = new BufferedImage(lineMaxWidth, lineHeight, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
                if (bufferedOldImage != null){
                    graphics2D.drawImage(bufferedOldImage, 0, 0, null);//将旧数据绘制上去
                }
                graphics2D.drawImage(msgImage.getScaledInstance(msgImage.getWidth(),msgImage.getHeight(),Image.SCALE_SMOOTH), 0, lineHeight - msgImage.getHeight(), null);
                graphics2D.dispose();

                bufferedOldImage = bufferedImage;
            }else {

            }
        }
        //绘制完毕，加上背景
        BufferedImage bufferedImage = new BufferedImage(lineMaxWidth + (margin * 2), lineHeight + (margin * 2), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        graphics2D.setBackground(bgColor);
        graphics2D.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());//通过使用当前绘图表面的背景色进行填充来清除指定的矩形
        graphics2D.drawImage(bufferedOldImage, margin, margin, null);
        //加个水印
        font = font.deriveFont(22f);
        String watermark = conf.getMsgToImageWatermark();
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        int wlen = 0;
        for (int i = 0; i < watermark.length(); i++){
            wlen += metrics.charWidth(watermark.charAt(i));
        }
        if(wlen < lineMaxWidth / 2){
            graphics2D.setFont(font);
            graphics2D.setPaint(paintColor);
            graphics2D.drawString(watermark, lineMaxWidth + (margin * 2) - wlen - 28, lineHeight + (margin * 2) - metrics.getAscent() - 8);
        }

        graphics2D.dispose();
        bufferedOldImage = bufferedImage;
        return bufferedOldImage;
    }

    /**
     * base64转图片
     * @param base64
     * @return
     */
    public static BufferedImage base64ToImage(String base64) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        if (base64.startsWith("data:image/png;base64,")){
            base64 = base64.substring("data:image/png;base64,".length());
        }
        byte[] bytes = decoder.decodeBuffer(base64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (image == null){
            throw new FileNotFoundException("图片为null");
        }
        ImageIO.write(image, "png", byteArrayOutputStream);

        return image;
    }

    /**
     * 裁剪图片
     * @param image
     * @param x
     * @param y
     * @param endX
     * @param endY
     * @return
     */
    public static BufferedImage crop(BufferedImage image, int x, int y, int endX, int endY){
        if (x > image.getWidth()){
            x = image.getWidth();
        }
        if (y > image.getHeight()){
            y = image.getHeight();
        }
        if (endX > image.getWidth()){
            endX = image.getWidth();
        }
        if (endY > image.getHeight()){
            endY = image.getHeight();
        }

        if (x <= -1) {
            x = 0;
        }
        if (y <= -1) {
            y = 0;
        }
        if (endX <= -1) {
            endX = image.getWidth() - 1;
        }
        if (endY <= -1) {
            endY = image.getHeight() - 1;
        }

        int width = Math.abs(endX - x);
        int height = Math.abs(endY - y);

        BufferedImage bufferedImage = new BufferedImage(width, height, image.getType());
        for (int iy = y; iy < endY; iy++) {
            for (int ix = x; ix < endX; ix++) {
                int rgb = image.getRGB(ix, iy);
                bufferedImage.setRGB(ix - x, iy - y, rgb);
            }
        }

        return bufferedImage;
    }

    /**
     * 图片填充拉伸
     * @param image
     * @param w
     * @param h
     * @return
     */
    public static BufferedImage picturesStretch9(BufferedImage image, int w, int h){
        if (w < image.getWidth()){
            w = image.getWidth();
        }
        if (h < image.getHeight()){
            h = image.getHeight();
        }

        BufferedImage bufferedImage = new BufferedImage(w, h, image.getType());

        int halfX = image.getWidth() / 2;
        int halfY = image.getHeight() / 2;

        //横向拉伸
        int resX = 0;//原图坐标
        int resY = 0;//
        for (int y = 0; y < h; y++){
            resX = 0;
            //Y轴填充区域
            if (y >= halfY && y <= h - halfY){
                for (int x = 0; x < w; x++){
                    //X轴填充区域
                    if (x >= halfX && x <= w - halfX){
                        int rgb = image.getRGB(halfX, halfY);
                        bufferedImage.setRGB(x, y, rgb);
                    }else {
                        int rgb = image.getRGB(resX, halfY);
                        bufferedImage.setRGB(x, y, rgb);
                        resX++;
                    }
                }
            }else {
                for (int x = 0; x < w; x++){
                    //X轴填充区域
                    if (x >= halfX && x <= w - halfX){
                        int rgb = image.getRGB(halfX, resY);
                        bufferedImage.setRGB(x, y, rgb);
                    }else {
                        int rgb = image.getRGB(resX, resY);
                        bufferedImage.setRGB(x, y, rgb);
                        resX++;
                    }
                }
                resY++;
            }
        }

        return bufferedImage;
    }
}
