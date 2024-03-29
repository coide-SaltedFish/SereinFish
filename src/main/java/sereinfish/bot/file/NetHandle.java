package sereinfish.bot.file;

import com.IceCreamQAQ.Yu.util.IO;
import com.icecreamqaq.yuq.entity.Contact;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import sereinfish.bot.entity.mc.GamerInfo;
import sereinfish.bot.entity.netease.music.msg.PlayeInfoMessage;
import sereinfish.bot.entity.netease.music.song.Song;
import sereinfish.bot.entity.pixiv.entity.Illust;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;
import sun.nio.ch.Net;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public static void getImage(Contact contact, String md5, NetworkLoader.NetworkLoaderListener listener) throws IOException {
        File file = new File(FileHandle.imageCachePath,md5);
        String url = MyYuQ.getImageUrlStr(md5);

        NetworkLoader.INSTANCE.addTask(new NetworkLoader.Task(contact, url, file, listener));
    }


    /**
     * 从网络得到图像
     * 此处会对线程进行锁定以防止重复下载
     * @param md5
     * @return
     */
    public synchronized static BufferedImage getImage(String md5) throws IOException {
        //先读缓存
        File file = new File(FileHandle.imageCachePath,md5);
        if (file.exists() && file.isFile()){
            SfLog.getInstance().d(NetHandle.class, "返回缓存图片：" + md5);
            return ImageIO.read(file);
        }
        URL url = MyYuQ.getImageUrl(md5);
        BufferedImage bufferedImage = ImageIO.read(url);
        try {
            ImageIO.write(bufferedImage, "PNG", file);
        }catch (IOException e){
            if(file.delete()){
                SfLog.getInstance().d(NetHandle.class, "缓存写入失败，已删除：" + md5);
            }else {
                SfLog.getInstance().d(NetHandle.class, "缓存写入失败且删除失败：" + md5);
            }
        }
        return bufferedImage;
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
        conn.setConnectTimeout(5 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        String res = readInputStream(inputStream);
        GamerInfo gamerInfo = MyYuQ.toClass(res,GamerInfo.class);

        inputStream.close();
        conn.disconnect();
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
        if (gamerInfo != null && gamerInfo.getProperties() != null){
            for (GamerInfo.Properties properties : gamerInfo.getProperties()){
                if (properties.getName().equals("textures")){
                    textures = properties.getValue();
                }
            }
        }else {
            throw new FileNotFoundException("玩家信息或皮肤未找到");
        }
        return ImageIO.read(new URL(GamerInfo.getValue(textures).getTextures().getSKIN().getUrl()));
    }

    public static File getMcPlayerSkinImageFile(String uuid, int w) throws IOException {
        File file = new File(FileHandle.imageCachePath, "temp_" + System.currentTimeMillis());

        ImageIO.write(getMcPlayerSkin(uuid), "PNG", file);

        return file;
    }

    public static File getMcPlayerHeadImageFile(String uuid, int w) throws IOException {
        File file = new File(FileHandle.imageCachePath, "temp_" + System.currentTimeMillis());

        ImageIO.write(getMcPlayerHeadImage(uuid, w), "PNG", file);

        return file;
    }

    /**
     * 通过uuid获得玩家头像
     * @param uuid
     * @return
     */
    public synchronized static BufferedImage getMcPlayerHeadImage(String uuid, int w) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(w, w, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(new Color(255, 255, 255, 62));
        graphics2D.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.dispose();

        BufferedImage skinImage = getMcPlayerSkin(uuid);

        if (skinImage == null){
            return bufferedImage;
        }
        //裁剪出头像
        BufferedImage head = ImageHandle.crop(skinImage, 8, 8, 16, 16);
        BufferedImage face = ImageHandle.crop(skinImage, 40, 8, 48, 16);

        graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(head, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.drawImage(face, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.dispose();

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

    public static void imagePixivDownload(Contact contact, Illust illust, int page, NetworkLoader.NetworkLoaderListener listener) throws IOException {
        File imageFile = new File(FileHandle.imageCachePath,illust.getId() + "_p" + page);
        String url = illust.getProxyUrl(page);
        NetworkLoader.INSTANCE.addTask(new NetworkLoader.Task(contact, url, imageFile, listener));
    }

//    public static File imagePixivDownload(Illust illust, int page) throws IOException {
//        File imageFile = new File(FileHandle.imageCachePath,illust.getId() + "_p" + page);
//        if (!imageFile.getParentFile().exists()){
//            imageFile.getParentFile().mkdirs();
//        }
//        //判断缓存是否存在
//        if (imageFile.exists() && imageFile.isFile()){
//            SfLog.getInstance().d(NetHandle.class, "返回缓存图片:" + illust.getId());
//            return imageFile;
//        }
//        String url = illust.getProxyUrl(page);
//        SfLog.getInstance().d(NetHandle.class, "开始从网络获取图片文件:" + illust.getId() + ":" + url);
//
//        FileOutputStream downloadFile = null;
//        InputStream inputStream = null;
//        try {
//            inputStream = OkHttpUtils.getByteStream(url);
//            int index;
//            byte[] bytes = new byte[1024];
//            downloadFile = new FileOutputStream(imageFile);
//            while ((index = inputStream.read(bytes)) != -1) {
//                downloadFile.write(bytes, 0, index);
//                downloadFile.flush();
//            }
//        } catch (IOException e){
//            if (downloadFile != null){
//                downloadFile.close();
//            }
//            downloadFile = null;
//            if(imageFile.delete()){
//                SfLog.getInstance().d(NetHandle.class, "下载出错，文件已删除");
//            }else {
//                SfLog.getInstance().d(NetHandle.class, "下载出错，且文件删除失败");
//            }
//
//            throw e;
//        }
//
//        finally {
//            if (downloadFile != null){
//                downloadFile.close();
//            }
//            if (inputStream != null){
//                inputStream.close();
//            }
//        }
//
//        SfLog.getInstance().d(NetHandle.class, "文件写入缓存完成:" + illust.getId() + ":" + url);
//
//        return imageFile;
//    }

    public static void imageDownload(Contact contact, String url, String name, NetworkLoader.NetworkLoaderListener listener) throws IOException {
        File imageFile = new File(FileHandle.imageCachePath,name);
        NetworkLoader.INSTANCE.addTask(new NetworkLoader.Task(contact, url, imageFile, listener));
    }

    public static File imageDownload(String url, String name) throws IOException {
        File imageFile = new File(FileHandle.imageCachePath,name);
        if (!imageFile.getParentFile().exists()){
            imageFile.getParentFile().mkdirs();
        }
        //判断缓存是否存在
        if (imageFile.exists() && imageFile.isFile()){
            SfLog.getInstance().d(NetHandle.class, "返回缓存图片:" + imageFile);
            return imageFile;
        }
        SfLog.getInstance().d(NetHandle.class, "开始从网络获取图片文件:" + url);

        FileOutputStream downloadFile = null;
        InputStream inputStream = null;
        try {
            inputStream = OkHttpUtils.getByteStream(url);
            int index;
            byte[] bytes = new byte[1024];
            downloadFile = new FileOutputStream(imageFile);
            while ((index = inputStream.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
        } catch (IOException e){
            downloadFile.close();
            downloadFile = null;
            if(imageFile.delete()){
                SfLog.getInstance().d(NetHandle.class, "下载出错，文件已删除");
            }else {
                SfLog.getInstance().d(NetHandle.class, "下载出错，且文件删除失败");
            }

            throw e;
        }

        finally {
            if (downloadFile != null){
                downloadFile.close();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        SfLog.getInstance().d(NetHandle.class, "文件写入缓存完成:" + url);

        return imageFile;
    }

    public static void neteaseCloudMusicDownload(Contact contact, Song song, NetworkLoader.NetworkLoaderListener listener) throws IOException {
        File file = new File(FileHandle.neteaseCloudMusicCachePath, song.getId() + "");
        PlayeInfoMessage playeInfoMessage = song.getPlayerInfo();
        if (playeInfoMessage.getCode() != 200 || playeInfoMessage.getData() == null || playeInfoMessage.getData().length == 0){
            throw new IOException("歌曲信息获取失败");
        }
        PlayeInfoMessage.Data data = playeInfoMessage.getData()[0];

        NetworkLoader.INSTANCE.addTask(new NetworkLoader.Task(contact, NetworkLoader.WEIGHT_HIGH, data.getUrl(), file, data.getMd5(), listener));
    }

//    /**
//     * 网易云音乐下载
//     * @param song
//     * @return
//     */
//    public static File neteaseCloudMusicDownload(Song song) throws IOException {
//        File file = new File(FileHandle.neteaseCloudMusicCachePath, song.getId() + "");
//        File tem_file = new File(FileHandle.neteaseCloudMusicCachePath, "tem_" + song.getId() + "");
//
//        //检查缓存
//        if (file.exists() && file.isFile()){
//            SfLog.getInstance().d(NetHandle.class, "返回缓存音乐:" + file);
//            return file;
//        }
//
//        PlayeInfoMessage playeInfoMessage = song.getPlayerInfo();
//        if (playeInfoMessage.getCode() != 200 || playeInfoMessage.getData() == null || playeInfoMessage.getData().length == 0){
//            throw new IOException("歌曲信息获取失败");
//        }
//
//        PlayeInfoMessage.Data data = playeInfoMessage.getData()[0];
//
//        SfLog.getInstance().d(NetHandle.class, "开始从网络获取音乐文件:" + data.getUrl());
//
//        FileOutputStream downloadFile = null;
//        InputStream inputStream = null;
//        try {
//            inputStream = OkHttpUtils.getByteStream(data.getUrl());
//            int index;
//            byte[] bytes = new byte[10240];
//            downloadFile = new FileOutputStream(tem_file);
//            while ((index = inputStream.read(bytes)) != -1) {
//                downloadFile.write(bytes, 0, index);
//                downloadFile.flush();
//            }
//        } catch (IOException e){
//            if (downloadFile != null){
//                downloadFile.close();
//            }
//            if (inputStream != null){
//                inputStream.close();
//            }
//
//            if(tem_file.delete()){
//                SfLog.getInstance().d(NetHandle.class, "下载出错，文件已删除");
//            }else {
//                SfLog.getInstance().d(NetHandle.class, "下载出错，且文件删除失败");
//            }
//            throw e;
//        }
//
//        downloadFile.close();
//        inputStream.close();
//
//        //检查md5
//        FileInputStream inputStream1 = new FileInputStream(tem_file);
//        String md5 = DigestUtils.md5Hex(inputStream1);
//        inputStream1.close();
//        if(!md5.equalsIgnoreCase(data.getMd5())){
//            if(tem_file.delete()){
//                SfLog.getInstance().d(NetHandle.class, "下载出错，文件已删除");
//            }else {
//                SfLog.getInstance().d(NetHandle.class, "下载出错，且文件删除失败");
//            }
//            throw new IOException("文件下载出错：MD5验证失败");
//        }
//
//        //临时文件转移
//        FileUtils.copyFile(tem_file, file);
//        SfLog.getInstance().d(NetHandle.class, "文件写入缓存完成");
//        if(tem_file.delete()){
//            SfLog.getInstance().d(NetHandle.class, "下载成功，且临时文件已删除");
//        }else {
//            SfLog.getInstance().w(NetHandle.class, "下载成功，但临时文件删除失败");
//        }
//
//        return file;
//    }
}
