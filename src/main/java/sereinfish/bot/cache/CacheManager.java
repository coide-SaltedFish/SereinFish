package sereinfish.bot.cache;

import com.google.gson.reflect.TypeToken;
import sereinfish.bot.entity.lolicon.Lolicon;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.entity.mc.GamerInfo;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * 缓存管理
 */
public class CacheManager {
    private static CacheManager cacheManager;
    private static Map<String,GamerInfo> gamerInfoMap;

    private CacheManager(){

    }

    /**
     * 初始化缓存管理
     * @return
     */
    public static CacheManager init(){
        cacheManager = new CacheManager();

        return cacheManager;
    }

    public static CacheManager getInstance(){
        if (cacheManager == null){
            throw new NullPointerException("缓存管理未初始化");
        }
        return cacheManager;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
    }

    /**这是关于图像文件缓存的**/

    /**
     * 得到群头像
     * @param group
     * @return
     */
    public static Image getGroupHeadImage(long group){
        File file = new File(FileHandle.groupHeadCachePath,group + "");
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        //检测缓存文件里有没有
        if (existGroupHead(group)){
            //从缓存文件获取
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                SfLog.getInstance().e(CacheManager.class,"群头像缓存获取失败：" + group);
            }
        }
        //从网络获取
        try {
            Image image = NetHandle.getImage(new URL("https://p.qlogo.cn/gh/" + group + "/" + group + "/640"));
            ImageIO.write((BufferedImage) image,"PNG",file);//写入文件
            return image;
        } catch (IOException e) {
            SfLog.getInstance().e(CacheManager.class,"群头像下载失败：" + group);
        }

        //获取失败，返回一个错误图片
        BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
        bufferedImage.createGraphics().drawString("错误",0,0);
        return bufferedImage;
    }

    /**
     * 得到qq头像
     * @param qq
     * @return
     */
    public static Image getMemberHeadImage(long qq){
        File file = new File(FileHandle.memberHeadCachePath,qq + "");
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        if (existMemberHead(qq)){
            //从缓存文件获取
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                SfLog.getInstance().e(CacheManager.class,"头像缓存获取失败：" + qq);
            }
        }
        //从网络获取
        try {
            Image image = NetHandle.getImage(new URL("http://q1.qlogo.cn/g?b=qq&nk=" + qq + "&s=640"));
            ImageIO.write((BufferedImage) image,"PNG",file);//写入文件
            return image;
        } catch (IOException e) {
            SfLog.getInstance().e(CacheManager.class,"头像下载失败：" + qq);
        }

        //获取失败，返回一个错误图片
        BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
        bufferedImage.createGraphics().drawString("错误",0,0);
        return bufferedImage;
    }

    /**Lolicon**/

    /**
     * 得到一个Lolicon图片
     * @param id
     * @param setu
     * @return
     */
    public static File getLoliconImage(int id, Lolicon.Setu setu) throws FileNotFoundException {
        File file = setu.isR18() ? new File(FileHandle.imageCachePath,"R18/lolicon_" + id) : new File(FileHandle.imageCachePath,"lolicon_" + id);
        SfLog.getInstance().d(CacheManager.class,"图片获取：" + file);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (file.exists() && file.isFile()){
            //从缓存获取
            return file;
        }else {
            //从网络获取
            try {
                LoliconManager.download(setu.getUrl(),file);
                return file;
            } catch (IOException e) {
                SfLog.getInstance().e(CacheManager.class,"图片下载失败：" + setu.getUrl(),e);
            } catch (Exception e) {
                SfLog.getInstance().e(CacheManager.class,"图片下载失败：" + setu.getUrl(),e);
            }
        }
        //获取失败，返回一个错误图片
        throw new FileNotFoundException("文件未找到：" + file);
    }

    /**关于游戏**/

    /**
     * 得到玩家信息
     * @param uuid
     * @return
     */
    public static GamerInfo getGameInfoToUUID(String uuid){
        if (uuid == null){
            return null;
        }
        uuid = uuid.trim().replace("-","");

        if (gamerInfoMap.containsKey(uuid)){
            return gamerInfoMap.get(uuid);
        }

        GamerInfo gamerInfo = null;
        try {
            gamerInfo = NetHandle.getGameInfo(uuid);
        } catch (IOException e) {
            SfLog.getInstance().e(CacheManager.class,"玩家信息获取失败",e);
        }
        return gamerInfo;
    }

    /**
     * 添加玩家信息
     * @param gamerInfo
     */
    public static void addGameInfo(String uuid,GamerInfo gamerInfo){
        gamerInfoMap.put(uuid,gamerInfo);
        try {
            setCacheGameInfo(MyYuQ.toJson(gamerInfoMap,new TypeToken<Map<String,GamerInfo>>(){}.getType()));
        } catch (IOException e) {
            SfLog.getInstance().e(CacheManager.class,"玩家信息保存失败",e);
        }
    }

    /**私有**/

    /**
     * 检测群头像是否存在
     * @param group
     * @return
     */
    private static boolean existGroupHead(long group){
        File file = new File(FileHandle.groupHeadCachePath,group + "");
        return file.exists() && file.isFile();
    }

    /**
     * 检测qq头像是否存在
     * @param qq
     * @return
     */
    private static boolean existMemberHead(long qq){
        File file = new File(FileHandle.memberHeadCachePath,qq + "");
        return file.exists() && file.isFile();
    }

    /**
     * 读取缓存的玩家信息
     */
    private static String getCacheGameInfo(){
        try {
            return FileHandle.read(FileHandle.gameInfoCacheFile);
        } catch (IOException e) {
            SfLog.getInstance().e(CacheManager.class,"读取失败：" + FileHandle.gameInfoCacheFile,e);
            return "";
        }
    }

    /**
     * 写入缓存的玩家信息
     */
    private static void setCacheGameInfo(String s) throws IOException {
        FileHandle.write(FileHandle.gameInfoCacheFile,s);
    }
}
