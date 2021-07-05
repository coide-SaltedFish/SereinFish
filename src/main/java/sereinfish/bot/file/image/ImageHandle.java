package sereinfish.bot.file.image;

import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;
import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import gui.ava.html.renderer.ImageRenderer;
import gui.ava.html.renderer.ImageRendererImpl;
import org.xhtmlrenderer.swing.Java2DRenderer;

import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sun.font.FontDesignMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
        return getHeadImage(CacheManager.getGroupHeadImage(group),size,size);
    }

    /**
     * 得到qq头像
     * @param qq
     * @param size
     * @return
     */
    public static Image getMemberHeadImage(long qq,int size){
        return getHeadImage(CacheManager.getMemberHeadImage(qq),size,size);
    }

    /**
     * 得到qq头像
     * @param qq
     * @param size
     * @return
     */
    public static Image getMemberHeadImageNoFrame(long qq,int size){
        return getHeadImageNoFrame(CacheManager.getMemberHeadImage(qq),size,size);
    }

    /**
     * 得到圆头像
     * @param image
     * @param width
     * @param high
     * @return
     */
    public static Image getHeadImage(Image image, int width, int high){
        // 透明底的图片
        BufferedImage formatAvatarImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = formatAvatarImage.createGraphics();
        //把图片切成一个园
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
        int border = 1;
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
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            SfLog.getInstance().e(ImageHandle.class,e);
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
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
        String mFont = (String) conf.getControl(GroupControlId.ComBox_FontSelect).getValue();
        int fontSize = (int) (double) conf.getControl(GroupControlId.Edit_IntNum_FontSize).getValue();//文本大小
        int margin = (int) ((double) conf.getControl(GroupControlId.Edit_IntNum_Margins).getValue());//生成图像边距
        int maxWidth = 1080;//最大图片宽度大小
        int maxLineWidth = maxWidth - (margin * 2);//最大行宽
        int lineMaxWidth = 0;//最大文字宽度
        int lineHeight = 0;//行高

        BufferedImage bufferedOldImage = null;

        //解析消息数据
        for (MessageItem item:message.getBody()){
            if (item instanceof Text){
                Font font = new Font(mFont,Font.PLAIN,fontSize);
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
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
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
        Font font = new Font(mFont,Font.PLAIN,22);
        String watermark = (String) conf.getControl(GroupControlId.Edit_Small_Plain_MsgToImageWatermark).getValue();
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
     * 消息转图片
     * 已弃用
     * @return
     */
    public static BufferedImage messageToHtmlImage(Message message){
        return null;

//        //判断模板是否存在
//        if (!FileHandle.msgToImageTemplate.exists() || !FileHandle.msgToImageTemplate.isFile()){
//            try {
//                FileHandle.msgToImageTemplate.createNewFile();
//                FileHandle.write(FileHandle.msgToImageTemplate,"<!DOCTYPE html>\n" +
//                        "<html>\n" +
//                        "<head> \n" +
//                        "<meta charset=\"utf-8\"> \n" +
//                        "<title>SereinFish</title> \n" +
//                        "</head>\n" +
//                        "<body>\n" +
//                        "#Msg#\n" +
//                        "</body>\n" +
//                        "</html>");
//            } catch (IOException e) {
//                SfLog.getInstance().e(ImageHandle.class,e);
//                //获取失败，返回一个错误图片
//                BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
//                bufferedImage.createGraphics().drawString("在生成消息图片时发生了错误：" + e.getMessage(),0,0);
//                return bufferedImage;
//            }
//        }
//        //html文件头
//        String messageTemplate = "";
//        try {
//            messageTemplate = FileHandle.read(FileHandle.msgToImageTemplate);
//        } catch (IOException e) {
//            SfLog.getInstance().e(ImageHandle.class,e);
//            //获取失败，返回一个错误图片
//            BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
//            bufferedImage.createGraphics().drawString("在生成消息图片时发生了错误：" + e.getMessage(),0,0);
//            return bufferedImage;
//        }
//        String[] messageTemplates = messageTemplate.split("#Msg#");
//        if (messageTemplates.length != 2){
//            //获取失败，返回一个错误图片
//            BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
//            bufferedImage.createGraphics().drawString("消息模板错误",0,0);
//            return bufferedImage;
//        }
//        String msg = messageTemplates[0];
//        for(MessageItem item:message.getBody()){
//            if (item instanceof Text){
//                String text = ((Text) item).getText();
//                text = text.replace("\n", "<br>");
//
//                msg += "<p>" + text + "</p>";
//            }else if (item instanceof com.icecreamqaq.yuq.message.Image){
//                msg += "<img src=\"" + ((com.icecreamqaq.yuq.message.Image) item).getUrl() + " alt=\"图像加载失败\">";
//            }else {
//                msg += "<p>" + item.toPath() + "</p>";
//            }
//        }
//        msg += messageTemplates[1];
//
//        HtmlParser htmlParser = new HtmlParserImpl();
//        htmlParser.loadHtml(msg);
//
//        ImageRenderer imageRenderer = new ImageRendererImpl(htmlParser);
//        imageRenderer.setWidth(1080);
//        return imageRenderer.getBufferedImage();
    }
}
