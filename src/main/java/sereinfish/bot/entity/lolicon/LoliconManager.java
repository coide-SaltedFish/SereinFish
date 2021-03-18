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
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        OutputStream os = new FileOutputStream(file);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    /**
     * 从lolicon得到一个图片配置文件
     * @return
     */
    public synchronized static Lolicon getLolicon(Lolicon.Request request) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(5*1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        String res = readInputStream(inputStream);
        Lolicon lolicon = MyYuQ.toClass(res,Lolicon.class);
        return lolicon;
    }

    /**
     * 从SFLolicon得到一个图片配置文件
     * @return
     */
    public synchronized static Response getSFLolicon(Request request, boolean isRe) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(5*1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        String res = readInputStream(inputStream);
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

    /**
     * 请求失败处理
     * @param lolicon
     * @param request
     */
    public static Message loliconErr(boolean isGroupMsg, GroupConf conf, Lolicon lolicon, Lolicon.Request request){
        switch (lolicon.getCode()) {
            case Lolicon.APIKEY_ERR:
                return MyYuQ.getMif().text("错误>>APIKEY:" + lolicon.getMsg()).toMessage();
            case Lolicon.ERR:
                return MyYuQ.getMif().text("错误>>Lolicon:" + lolicon.getMsg()).toMessage();
            case Lolicon.QUOTA_ERR:
                if (isGroupMsg) {
                    if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconLocalImage).getValue()){
                        File file = null;
                        if (request.getR18() == Lolicon.NO_R18){
                            File files[] = new File(FileHandle.imageCachePath,"lolicon/").listFiles();
                            file = files[MyYuQ.getRandom(0,files.length - 1)];
                        }else if (request.getR18() == Lolicon.R18){
                            File files[] = new File(FileHandle.imageCachePath,"lolicon/R18/").listFiles();
                            file = files[MyYuQ.getRandom(0,files.length - 1)];
                        }else if (request.getR18() == Lolicon.PLAIN_AND_R18){
                            ArrayList<File> arr = new ArrayList<>();
                            File files[] = FileHandle.imageCachePath.listFiles();
                            for (File f:files){
                                arr.add(f);
                            }
                            files = new File(FileHandle.imageCachePath,"R18/").listFiles();
                            for (File f:files){
                                arr.add(f);
                            }
                            file = arr.get(MyYuQ.getRandom(0,arr.size() - 1));
                        }

                        if (file != null && file.isFile()){
                            SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
                            if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconMD5Image).getValue()){
                                try {
                                    StringBuilder stringBuilderMd5 = new StringBuilder(DigestUtils.md5Hex(new FileInputStream(file)));
                                    stringBuilderMd5.insert(20,"-");
                                    stringBuilderMd5.insert(16,"-");
                                    stringBuilderMd5.insert(12,"-");
                                    stringBuilderMd5.insert(8,"-");
                                    return Message.Companion.toMessageByRainCode("<Rain:Image:{" + stringBuilderMd5.toString() + "}.mirai>");
                                } catch (IOException e) {
                                    SfLog.getInstance().e(LoliconManager.class,e);
                                }
                            }else {
                                Message message = MyYuQ.getMif().imageByFile(file).toMessage();
                                r18ReCell(message,new Lolicon.Setu(false));
                                return message;
                            }
                        }
                    } else {
                        return MyYuQ.getMif().text("错误>>额度上限:" + lolicon.getMsg()).toMessage();
                    }
                }else {
                    File file = null;
                    if (request.getR18() == Lolicon.NO_R18){
                        File files[] = FileHandle.imageCachePath.listFiles();
                        file = files[MyYuQ.getRandom(0,files.length - 1)];
                    }else if (request.getR18() == Lolicon.R18){
                        File files[] = new File(FileHandle.imageCachePath,"R18/").listFiles();
                        file = files[MyYuQ.getRandom(0,files.length - 1)];
                    }else if (request.getR18() == Lolicon.PLAIN_AND_R18){
                        ArrayList<File> arr = new ArrayList<>();
                        File files[] = FileHandle.imageCachePath.listFiles();
                        for (File f:files){
                            arr.add(f);
                        }
                        files = new File(FileHandle.imageCachePath,"R18/").listFiles();
                        for (File f:files){
                            arr.add(f);
                        }
                        file = arr.get(MyYuQ.getRandom(0,arr.size() - 1));
                    }
                    if (file != null && file.isFile()){
                        SfLog.getInstance().d(LoliconManager.class,"返回：" + file);
                        if ((Boolean) conf.getControl(GroupControlId.CheckBox_LoliconMD5Image).getValue()){
                            try {
                                StringBuilder stringBuilderMd5 = new StringBuilder(DigestUtils.md5Hex(new FileInputStream(file)));
                                stringBuilderMd5.insert(20,"-");
                                stringBuilderMd5.insert(16,"-");
                                stringBuilderMd5.insert(12,"-");
                                stringBuilderMd5.insert(8,"-");
                                return Message.Companion.toMessageByRainCode("<Rain:Image:{" + stringBuilderMd5.toString() + "}.mirai>");
                            } catch (IOException e) {
                                SfLog.getInstance().e(LoliconManager.class,e);
                            }
                        }else {
                            Message message = MyYuQ.getMif().imageByFile(file).toMessage();
                            r18ReCell(message,new Lolicon.Setu(false));
                            return message;
                        }
                    }
                }
                break;
            default:
                return MyYuQ.getMif().text("错误:" + lolicon.getMsg()).toMessage();
        }
        return null;
    }

    /**
     * R18延时撤回
     * @param message
     * @param setu
     */
    private static void r18ReCell(Message message, Lolicon.Setu setu){
        //如果是R18，延时撤回
        int time = 25000;//25s
        if (setu.isR18()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(time);
                    message.recall();
                }

                public void sleep(int ms) {
                    try {
                        Thread.sleep(ms);
                    } catch (InterruptedException e) {
                        message.recall();
                        SfLog.getInstance().e(this.getClass(), e);
                    }
                }
            }).start();
        }
    }

    /**
     * 得到请求信息
     * @return
     */
    public static Lolicon.Request getRequest(boolean isGroupMsg, GroupConf conf, String apiKey, String keyWord, int num){
        int r18 = Lolicon.NO_R18;
        if (isGroupMsg){
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_SetuR18).getValue()){
                r18 = Lolicon.R18;
            }
            if ((Boolean) conf.getControl(GroupControlId.CheckBox_PlainAndR18).getValue()){
                r18 = Lolicon.PLAIN_AND_R18;
            }
        }

        Lolicon.Request request = new Lolicon.Request(apiKey,r18,keyWord,num,null,true);

        return request;
    }
}
