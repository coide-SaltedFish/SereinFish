package sereinfish.bot.file;

import sereinfish.bot.entity.mc.GamerInfo;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
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
    public synchronized static Image getImage(URL url) throws IOException {
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
