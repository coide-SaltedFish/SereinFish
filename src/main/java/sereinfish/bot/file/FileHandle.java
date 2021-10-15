package sereinfish.bot.file;

import sereinfish.bot.myYuq.MyYuQ;

import java.io.*;

public class FileHandle {
    public final static String encoding = "UTF-8";//文件编码

    public static File dataPath = new File("SereinFish/");//数据文件保存路径

    public static File dataBasePath = new File(dataPath,"data/database/");//本地数据库文件夹
    public static File dataBaseConfFile = new File(dataBasePath,"conf/databaseConfigs.json");//数据库配置列表文件
    public static File MessageDataBaseFile = new File(dataBasePath,"Msg.db");//消息数据库文件

    public static File configPath = new File(dataPath,"conf/");//配置文件夹路径
    public static File AuthorityConfigFile = new File(configPath,"authority.json");//权限配置文件

    public static File cachePath = new File(dataPath,"cache/");//缓存文件路径
    public static File groupHeadCachePath = new File(cachePath,"group/head/");//群头像缓存路径
    public static File memberHeadCachePath = new File(cachePath,"member/head/");//qq头像缓存路径
    public static File gameInfoCacheFile = new File(cachePath,"gameInfo.json");//玩家信息缓存
    public static File imageCachePath = new File(cachePath,"image/");//网络图片缓存
    public static File voiceCachePath = new File(cachePath,"voice/");//网络图片缓存
    public static File imageLoliconCachePath = new File(cachePath,"image/lolicon/");//Lolicon图片缓存
    public static File helpMenuImageCacheFile = new File(cachePath, "helpMenu/helpMenu_" + MyYuQ.version);//帮助菜单缓存文件

    public static File groupDataPath = new File(dataPath,"data/group/");//群数据文件路径

    public static File msgToImageTemplate = new File(configPath,"MessageTemplate.conf");//消息转图片模板
    public static File msgToImageTemplateTemp = new File(configPath,"MessageTemplateTemp");//消息转图片模板临时缓存

    public static String mcResDefaultFontFile = "mc/gui/font/Unifont-mono.ttf";//mc默认字体
    public static String mcResOptionsBackgroundFile = "mc/gui/texture/options_background.png";//土方块背景材质
    public static String mcResInitFile = "mc/gui/texture/init.png";//土方块背景材质
    public static String mcResServerStateFrameImageFile = "mc/gui/texture/window.png";//边框材质

    public static File mcResPath = new File(configPath, "mc/");//mc文件夹
    public static File rconConfFile = new File(mcResPath, "rcon/conf.json");//rcon配置文件

    public static String JetBrainsMonoFontFile = "fonts/JetBrainsMono.ttc";//mc默认字体

    public static File backupsPath = new File(dataPath, "backups/");//备份文件

    static {
        //初始化
        if (dataPath.exists())
            dataPath.mkdirs();
        if (dataBasePath.exists())
            dataBasePath.mkdirs();
        if (configPath.exists())
            configPath.mkdirs();
        if (cachePath.exists())
            cachePath.mkdirs();
        if (groupHeadCachePath.exists())
            groupHeadCachePath.mkdirs();
        if (memberHeadCachePath.exists())
            memberHeadCachePath.mkdirs();
        if (imageCachePath.exists())
            imageCachePath.mkdirs();
        if (imageLoliconCachePath.exists())
            imageLoliconCachePath.mkdirs();
        if (groupDataPath.exists())
            groupDataPath.mkdirs();
        if (mcResPath.exists())
            mcResPath.mkdirs();
        if (backupsPath.exists())
            backupsPath.mkdirs();
    }
    /**
     * 写入文件
     * @param file
     * @param str
     * @return
     */
    public static void write(File file,String str) throws IOException {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(new FileOutputStream(file),encoding);
        outputStreamWriter.write(str);
        outputStreamWriter.close();
    }

    /**
     * 文件读取
     * @param file
     * @return
     */
    public static String read(File file) throws IOException {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = bufferedReader.readLine();
        while (lineTxt != null) {
            stringBuilder.append(lineTxt);

            lineTxt = bufferedReader.readLine();
            if(lineTxt != null){
                stringBuilder.append("\n");
            }
        }
        read.close();
        return stringBuilder.toString();
    }

}
