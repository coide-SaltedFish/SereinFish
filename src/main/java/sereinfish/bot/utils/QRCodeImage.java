package sereinfish.bot.utils;

import com.google.zxing.*;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码图像
 */
public class QRCodeImage {
    private static int Qr_Version = 15;

//    /**
//     * 生成带logo的二维码
//     * @return
//     */
//    public static BitMatrix QRCodeLogo(BitMatrix bitMatrix, BufferedImage logo){
//
//    }
//
//    /**
//     * 生成带背景二维码
//     * @param bitMatrix
//     * @param bgImage
//     * @param alpha
//     * @return
//     */
//    public static BitMatrix QRCodeBackgroundImage(BitMatrix bitMatrix, BufferedImage bgImage, float alpha){
//
//    }

    /**
     * 二维码图像生成
     * @return
     */
    public static BufferedImage generateQRCodeImage(String text, int width, int height, Color onColor) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        hints.put(EncodeHintType.QR_VERSION, Qr_Version);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(onColor.hashCode(), 0xFFFFFFFF);

        return MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
    }

    /**
     * 二维码生成
     * @param text
     * @param width
     * @param height
     * @return
     */
    public static BitMatrix generateQRCodeBitMatrix(String text, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.QR_VERSION, Qr_Version);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        return bitMatrix;
    }

    /**
     * 二维码添加logo
     * @param matrixImage 源二维码图片
     * @param logoImage logo图片
     * @return 返回带有logo的二维码图片
     * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
     */
    public static BufferedImage logoMatrix(BufferedImage matrixImage, BufferedImage logoImage){
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        //开始绘制图片
        g2.drawImage(logoImage,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);//绘制
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5,20,20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush() ;
        return matrixImage ;
    }

    /**
     * 添加背景
     * @param bitMatrix
     * @param bgImage
     * @param alpha
     * @param onColor
     * @return
     */
    public static BufferedImage backgroundMatrix(BitMatrix bitMatrix, BufferedImage bgImage, float alpha, Color onColor){
        int bitW = bitMatrix.getWidth();
        int bitH = bitMatrix.getHeight();
        int whiteSideWidth = 0;

        int qrWidth = (Qr_Version - 1) * 4 + 21;

        //得到边界大小
        for (int y = 0; y < bitH; y++){
            for (int x = 0; x < bitW; x++){
                if (bitMatrix.get(x, y)){
                    whiteSideWidth = x;
                    break;
                }
            }
        }

        int rowSize = (bitW - whiteSideWidth * 2) / qrWidth;

        //二值化
        boolean bit[][] = new boolean[qrWidth + 1][qrWidth + 1];

        for(int y = whiteSideWidth, bitY = 0; y < bitH - whiteSideWidth && bitY < qrWidth + 1; y += rowSize, bitY++){
            for (int x = whiteSideWidth, bitX = 0; x < bitW - whiteSideWidth && bitX < qrWidth + 1; x += rowSize, bitX++){
                bit[bitY][bitX] = bitMatrix.get(x, y);
            }
        }

        //生成底图
        BufferedImage bufferedImage = new BufferedImage(bitW, bitH, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿

        //绘制背景
        graphics2D.drawImage(bgImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        //绘制半透明图层
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//半透明颜色覆盖
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));

        int rowSizeBg = 2;//绘制时的像素点大小

        //绘制二维码像素点
        for (int y = 0; y < bit.length; y++){
            for (int x = 0; x < bit[y].length; x++){

                int imageX = (int) (whiteSideWidth + (x * rowSize) + (rowSize - rowSizeBg) / 2.0);
                int imageY = (int) (whiteSideWidth + (y * rowSize) + (rowSize - rowSizeBg) / 2.0);

                if (bit[y][x]){
                    //设置颜色
                    graphics2D.setPaint(onColor);
                    //判断是否是标记框
                    if (isFlagRect(bit[y].length, bit.length, x, y)){
                        imageX = whiteSideWidth + (x * rowSize);
                        imageY = whiteSideWidth + (y * rowSize);
                        graphics2D.fillRect(imageX, imageY, rowSize, rowSize);
                    }else {
                        graphics2D.fillRect(imageX, imageY, rowSizeBg, rowSizeBg);
                    }
                }else {
                    //设置颜色
                    graphics2D.setPaint(Color.WHITE);
                    graphics2D.fillRect(imageX, imageY, rowSizeBg, rowSizeBg);
                }
            }
        }
        graphics2D.dispose();
        return bufferedImage;
    }

    /**
     * 判断是否定位点
     * @param width
     * @param height
     * @param x
     * @param y
     * @return
     */
    private static boolean isFlagRect(int width, int height, int x, int y){
        int qrWidth = (Qr_Version - 1) * 4 + 21;
        int flagWidth = 8;

        if (x < flagWidth && y < flagWidth){
            return true;
        }else if ((x > width - flagWidth - 1 && y < flagWidth)){
            return true;
        }else if (x < flagWidth && y > height - flagWidth - 1){
            return true;
        }else if (x == flagWidth - 2 || y == flagWidth - 2){
            return true;
        }
        //校正图像
        ArrayList<Integer[]> checkImages = new ArrayList<>();
        int apNum = (qrWidth - 6) / 18  + 1;
        for (int i = 0; i < apNum; i++){//y
            for (int j = 0; j < apNum; j++){//x
                if (j == i && j == 0){
                    continue;
                }
                if (i == 0 && j == apNum - 1){
                    continue;
                }
                if (i == apNum - 1 && j == 0){
                    continue;
                }
                int yCorrection = (i - 1) * 2;
                int xCorrection = (j - 1) * 2;

                if (yCorrection < 0){
                    yCorrection = 0;
                }
                if (xCorrection < 0){
                    xCorrection = 0;
                }

                checkImages.add(new Integer[]{3 + (i * 20) + yCorrection, 3 + (j * 20) + xCorrection});
            }
        }

        int apW = 6;
        for (Integer[] checkImage:checkImages){
            if (y > checkImage[0] && y < checkImage[0] + apW && x > checkImage[1] && x < checkImage[1] + apW){
                return true;
            }
        }

        return false;
    }

    /**
     * 扫码
     * @param image
     * @return
     * @throws NotFoundException
     */
    public static Result getQrResult(BufferedImage image) throws NotFoundException {
        Result result = null;
        BinaryBitmap bitmap = new BinaryBitmap(
                new HybridBinarizer(new BufferedImageLuminanceSource(image)));

        HashMap hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        result = new MultiFormatReader().decode(bitmap, hints);

        return result;
    }

}
