package sereinfish.bot.file;

import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.myYuq.MyYuQ;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

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
        return getHeadImage(CacheManager.getMemberHeadImage(MyYuQ.getYuQ().getBotId()),size,size);
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
        graphics.drawImage(image, border, border, width - border * 2, width - border * 2, null);
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
}
