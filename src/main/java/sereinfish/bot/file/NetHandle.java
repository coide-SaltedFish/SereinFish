package sereinfish.bot.file;

import sereinfish.bot.entity.mc.GamerInfo;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络资源处理
 */
public class NetHandle {

    /**
     * 从网络得到图像
     * 此处会对线程进行锁定以防止重复下载
     * @param url
     * @return
     */
    public synchronized static BufferedImage getImage(URL url) throws IOException {
        return ImageIO.read(url);
    }

    /**
     * 通过uuid得到玩家信息
     * @param uuid
     * @return
     */
    public synchronized static GamerInfo getGameInfo(String uuid) throws IOException {
        String strUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(5*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        String res = readInputStream(inputStream);
        GamerInfo gamerInfo = MyYuQ.toClass(res,GamerInfo.class);
        return gamerInfo;
    }

    /**
     * 通过uuid得到玩家皮肤
     * @param uuid
     * @return
     */
    public synchronized static BufferedImage getMcPlayerSkin(String uuid) throws IOException {
        GamerInfo gamerInfo = getGameInfo(uuid);
        String textures = "";
        for (GamerInfo.Properties properties : gamerInfo.getProperties()){
            if (properties.getName().equals("textures")){
                textures = properties.getValue().substring(0, properties.getValue().length());
            }
        }
        return getImage(new URL(GamerInfo.getValue(textures).getTextures().getSKIN().getUrl()));
    }

    /**
     * 通过uuid获得玩家头像
     * @param uuid
     * @return
     */
    public synchronized static BufferedImage getMcPlayerHeadImage(String uuid, int w) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(w, w, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.gray);
        graphics2D.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.dispose();

        BufferedImage skinImage = getMcPlayerSkin(uuid);
        //裁剪出头像
        BufferedImage head = ImageHandle.crop(skinImage, 8, 8, 16, 16);
        BufferedImage face = ImageHandle.crop(skinImage, 40, 8, 48, 16);

        graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(head, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.drawImage(face, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);

        return bufferedImage;
    }

    /**
     * 从输入流中获取字符串
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return new String(bos.toByteArray(),"utf-8");
    }
}
