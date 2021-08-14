package sereinfish.bot.entity.lolicon;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.message.Message;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.lolicon.sf.Request;
import sereinfish.bot.entity.lolicon.sf.Response;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LoliconManager {
    /**
     * 下载文件到本地
     *
     * @param urlString
     *          被下载的文件地址
     * @param file
     *          本地文件名
     * @throws Exception
     *           各种异常
     */
    public static void download(String urlString, File file) throws Exception {
        SfLog.getInstance().d(LoliconManager.class,"开始图片下载：" + urlString);
        byte[] data = OkHttpUtils.downloadBytes(urlString);
        new FileOutputStream(file).write(data);
    }

    /**
     * 从lolicon得到一个图片配置文件
     * @return
     */
    public synchronized static Lolicon getLolicon(Lolicon.Request request) throws IOException {
        String res = OkHttpUtils.getJson(request.getUrl()).toJSONString();
        Lolicon lolicon = MyYuQ.toClass(res,Lolicon.class);
        return lolicon;
    }

    /**
     * 从SFLolicon得到一个图片配置文件
     * @return
     */
    public synchronized static Response getSFLolicon(Request request, boolean isRe) throws IOException {
        String res = OkHttpUtils.getStr(request.getUrl());
        try{
            Response response = MyYuQ.toClass(res,Response.class);
            return response;
        }catch (Exception e){
            if (isRe){
                SfLog.getInstance().e(LoliconManager.class,"失败，重试：" + request.getUrl());
                return getSFLolicon(request, false);
            }else {
                SfLog.getInstance().e(LoliconManager.class,e);
                return null;
            }
        }
    }

//    /**
//     * 请求失败处理
//     * @param lolicon
//     * @param request
//     */
//    public static Message loliconErr(boolean isGroupMsg, GroupConf conf, Lolicon lolicon, Lolicon.Request request){
//        switch (lolicon.getCode()) {
//            case Lolicon.APIKEY_ERR:
//                return MyYuQ.getMif().text("错误>>APIKEY:" + lolicon.getMsg()).toMessage();
//            case Lolicon.ERR:
//                return MyYuQ.getMif().text("错误>>Lolicon:" + lolicon.getMsg()).toMessage();
//            case Lolicon.QUOTA_ERR:
//                if (isGroupMsg) {
//                    if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconLocalImage).getValue()){
//                        File file = null;
//                        if (request.getR18() == Lolicon.NO_R18){
//                            File files[] = new File(FileHandle.imageCachePath,"lolicon/").listFiles();
//                            file = files[MyYuQ.getRandom(0,files.length - 1)];
//                        }else if (request.getR18() == Lolicon.R18){
//                            File files[] = new File(FileHandle.imageCachePath,"lolicon/R18/").listFiles();
//                            file = files[MyYuQ.getRandom(0,files.length - 1)];
//                        }else if (request.getR18() == Lolicon.PLAIN_AND_R18){
//                            ArrayList<File> arr = new ArrayList<>();
//                            File files[] = FileHandle.imageCachePath.listFiles();
//                            for (File f:files){
//                                arr.add(f);
//                            }
//                            files = new File(FileHandle.imageCachePath,"R18/").listFiles();
//                            for (File f:files){
//                                arr.add(f);
//                            }
//                            file = arr.get(MyYuQ.getRandom(0,arr.size() - 1));
//                        }
//
//                        if (file != null && file.isFile()){
//                            SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
//                            if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconMD5Image).getValue()){
//                                try {
//                                    StringBuilder stringBuilderMd5 = new StringBuilder(DigestUtils.md5Hex(new FileInputStream(file)));
//                                    return Message.Companion.toMessageByRainCode("<Rain:Image:" + stringBuilderMd5.toString().toUpperCase() + ".jpg>");
//                                } catch (IOException e) {
//                                    SfLog.getInstance().e(LoliconManager.class,e);
//                                }
//                            }else {
//                                Message message = MyYuQ.getMif().imageByFile(file).toMessage();
//                                r18ReCell(message,new Lolicon.Setu(false));
//                                return message;
//                            }
//                        }
//                    } else {
//                        return MyYuQ.getMif().text("错误>>额度上限:" + lolicon.getMsg()).toMessage();
//                    }
//                }else {
//                    File file = null;
//                    if (request.getR18() == Lolicon.NO_R18){
//                        File files[] = FileHandle.imageCachePath.listFiles();
//                        file = files[MyYuQ.getRandom(0,files.length - 1)];
//                    }else if (request.getR18() == Lolicon.R18){
//                        File files[] = new File(FileHandle.imageCachePath,"R18/").listFiles();
//                        file = files[MyYuQ.getRandom(0,files.length - 1)];
//                    }else if (request.getR18() == Lolicon.PLAIN_AND_R18){
//                        ArrayList<File> arr = new ArrayList<>();
//                        File files[] = FileHandle.imageCachePath.listFiles();
//                        for (File f:files){
//                            arr.add(f);
//                        }
//                        files = new File(FileHandle.imageCachePath,"R18/").listFiles();
//                        for (File f:files){
//                            arr.add(f);
//                        }
//                        file = arr.get(MyYuQ.getRandom(0,arr.size() - 1));
//                    }
//                    if (file != null && file.isFile()){
//                        SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
//                        if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconMD5Image).getValue()){
//                            try {
//                                StringBuilder stringBuilderMd5 = new StringBuilder(DigestUtils.md5Hex(new FileInputStream(file)));
//                                return Message.Companion.toMessageByRainCode("<Rain:Image:" + stringBuilderMd5.toString().toUpperCase() + ".jpg>");
//                            } catch (IOException e) {
//                                SfLog.getInstance().e(LoliconManager.class,e);
//                            }
//                        }else {
//                            Message message = MyYuQ.getMif().imageByFile(file).toMessage();
//                            r18ReCell(message,new Lolicon.Setu(false));
//                            return message;
//                        }
//                    }
//                }
//                break;
//            default:
//                return MyYuQ.getMif().text("错误:" + lolicon.getMsg()).toMessage();
//        }
//        return null;
//    }

    /**
     * 得到请求信息
     * @return
     */
    public static Lolicon.Request getRequest(boolean isGroupMsg, GroupConf conf, String[] tags, int[] uids, int num){
        int r18 = Lolicon.NO_R18;
        if (isGroupMsg){
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SetuR18).getValue()){
                r18 = Lolicon.R18;
            }
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_PlainAndR18).getValue()){
                r18 = Lolicon.PLAIN_AND_R18;
            }
        }

        Lolicon.Request request = new Lolicon.Request(r18, num, uids, tags, null);

        return request;
    }
}
