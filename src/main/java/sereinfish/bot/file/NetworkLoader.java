package sereinfish.bot.file;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import lombok.Data;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class NetworkLoader {

    //优先级
    public static final int WEIGHT_UPDATE = -1;//上载

    public static final int WEIGHT_HIGH = 0;
    public static final int WEIGHT_MIDDLE = 1;
    public static final int WEIGHT_LOW = 2;

    private boolean isWait = false;//暂停活动

    private boolean isRun = false;//是否正在进行任务
    private Thread thread;//线程

    private ArrayList<Task> taskList = new ArrayList<>();//任务列表

    //加载队列

    //单例
    public static NetworkLoader INSTANCE;

    private NetworkLoader(){

    }

    public static void init(){
        INSTANCE = new NetworkLoader();
    }

    /**
     * 任务添加
     * @param task
     */
    public void addTask(Task task) throws IOException {
        //缓存检查
        if (task.getToFile().exists() && task.getToFile().isFile()){
            //md5
            if (task.getMd5() != null && !task.getMd5().equals("")){
                FileInputStream inputStream1 = new FileInputStream(task.getToFile());
                String md5 = DigestUtils.md5Hex(inputStream1);
                inputStream1.close();
                if(md5.equalsIgnoreCase(task.getMd5())){
                    try {
                        task.getListener().success(task.toFile);
                    }catch (Exception exception){
                        MessageLineQ messageLineQ = new Message().lineQ();
                        try {
                            File file = new File(FileHandle.imageCachePath, "xibao_" + System.currentTimeMillis());
                            BufferedImage bufferedImage = ImageHandle.getXiBao(exception.getClass().getCanonicalName() + ":" + exception.getMessage(),
                                    new Font("黑体", Font.BOLD, 168));
                            ImageIO.write(bufferedImage, "jpg", file);
                            Image image = MyYuQ.uploadImage(task.getContact(), file);
                            messageLineQ.plus(image).getMessage();
                        } catch (IOException e1) {
                            SfLog.getInstance().e(this.getClass(), e1);
                            messageLineQ.text("错误：" + e1.getMessage()).getMessage();
                        }
                        //异常信息打印
                        SfLog.getInstance().e(this.getClass(), exception);

                        task.getContact().sendMessage(messageLineQ);
                    }
                    return;
                }
            }else {
                try {
                    task.getListener().success(task.toFile);
                }catch (Exception exception){
                    MessageLineQ messageLineQ = new Message().lineQ();
                    try {
                        File file = new File(FileHandle.imageCachePath, "xibao_" + System.currentTimeMillis());
                        BufferedImage bufferedImage = ImageHandle.getXiBao(exception.getClass().getCanonicalName() + ":" + exception.getMessage(),
                                new Font("黑体", Font.BOLD, 168));
                        ImageIO.write(bufferedImage, "jpg", file);
                        Image image = MyYuQ.uploadImage(task.getContact(), file);
                        messageLineQ.plus(image).getMessage();
                    } catch (IOException e1) {
                        SfLog.getInstance().e(this.getClass(), e1);
                        messageLineQ.text("错误：" + e1.getMessage()).getMessage();
                    }
                    //异常信息打印
                    SfLog.getInstance().e(this.getClass(), exception);

                    task.getContact().sendMessage(messageLineQ);
                }
                return;
            }
        }
        //任务添加
        taskList.add(task);

        SfLog.getInstance().d(this.getClass(), "任务已添加，当前任务数：" + taskList.size());

        wakeUp();
    }

    /**
     * 唤醒任务处理
     */
    private void wakeUp(){
        if (!isRun){
            isRun = true;
            taskListHandle();
        }
    }

    /**
     * 任务列表处理
     */
    private void taskListHandle(){
        isRun = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Task task;
                    while ((task = getHighPriorityTask()) != null){
                        taskList.remove(task);
                        taskHandle(task);
                        SfLog.getInstance().d(this.getClass(), "任务已完成，当前剩余任务数：" + taskList.size());
                    }
                }catch (Exception e){
                    SfLog.getInstance().e(this.getClass(), e);
                }
                isRun = false;
            }
        });
        thread.start();
    }

    /**
     * 得到高优先级任务
     * @return
     */
    private Task getHighPriorityTask(){
        Task task = null;
        for (int i = 0; i < taskList.size(); i++){
            Task t = taskList.get(i);
            if (task == null){
                task = t;
            }else {
                if (t.getWeight() < task.weight){
                    task = t;
                }
            }
        }
        return task;
    }

    /**
     * 任务处理
     */
    private void taskHandle(Task task){
        try {
            long total = 0;//文件大小
            long speed = 0;//速度

            File temFile = new File(FileHandle.cachePath, System.currentTimeMillis() + "");//临时文件

            //文件路径检查
            if (!task.getToFile().getParentFile().exists()){
                task.getToFile().getParentFile().mkdirs();
            }
            //处理url
            SfLog.getInstance().d(NetHandle.class, "开始从网络获取图片文件:" + task.getUrl());

            Response response = OkHttpUtils.get(task.getUrl());
            total = response.body().contentLength();

            SfLog.getInstance().d(this.getClass(), "文件大小：" + total);
            task.getListener().start(total);

            InputStream inputStream = response.body().byteStream();

            //文件下载
            FileOutputStream fileOutputStream = null;
            try{
                fileOutputStream = new FileOutputStream(temFile);

                byte[] bytes = new byte[10240];//缓冲区
                int index = inputStream.read(bytes);
                long afterSpeedTime = System.currentTimeMillis();
                long afterPro = 0;
                boolean afterWait = isWait;

                while (index != -1) {
                    //速度计算
                    if (System.currentTimeMillis() - afterSpeedTime >= 1000){
                        afterSpeedTime = System.currentTimeMillis();
                        speed = afterPro - index;
                        afterPro = index;
                    }

                    if (afterWait != isWait){
                        afterWait = isWait;
                        if (isWait){
                            SfLog.getInstance().w(this.getClass(), "下载已暂停");
                        }else {
                            SfLog.getInstance().w(this.getClass(), "下载暂停已取消");
                        }
                    }

                    if (!isWait){
                        task.getListener().progress(index, total, speed);
                        fileOutputStream.write(bytes, 0, index);
                        fileOutputStream.flush();
                        index = inputStream.read(bytes);
                    }else {
                        Thread.sleep(100);
                    }
                }

                //关闭连接
                inputStream.close();
                fileOutputStream.close();
            }catch (Exception e){
                SfLog.getInstance().e(this.getClass(), e);
                //关闭连接
                inputStream.close();
                if (fileOutputStream != null){
                    fileOutputStream.close();
                }
                //删除临时文件
                if (!temFile.delete()){
                    SfLog.getInstance().w(this.getClass(), "临时文件删除失败：" + temFile);
                }
                task.getListener().fail(e);
                return;
            }
            //md5验证
            if (task.getMd5() != null && !task.getMd5().equals("")){
                FileInputStream inputStream1 = new FileInputStream(temFile);
                String md5 = DigestUtils.md5Hex(inputStream1);
                inputStream1.close();
                if(!md5.equalsIgnoreCase(task.getMd5())){
                    if(temFile.delete()){
                        SfLog.getInstance().d(NetHandle.class, "下载出错，文件已删除");
                    }else {
                        SfLog.getInstance().d(NetHandle.class, "下载出错，且文件删除失败");
                    }
                    throw new IOException("文件下载出错：MD5验证失败");
                }
            }

            //临时文件转移
            FileUtils.copyFile(temFile, task.toFile);
            SfLog.getInstance().d(NetHandle.class, "文件写入缓存完成");

            try {
                Files.delete(temFile.toPath());
            }catch (IOException e){
                SfLog.getInstance().e(this.getClass(), "临时文件删除失败：" + temFile, e);
            }

//            if(temFile.delete()){
//                SfLog.getInstance().d(NetHandle.class, "下载成功，且临时文件已删除");
//            }else {
//                SfLog.getInstance().w(NetHandle.class, "下载成功，但临时文件删除失败");
//            }
            task.getListener().success(task.toFile);
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), e);
            try {
                task.getListener().fail(e);
            }catch (Exception exception){
                MessageLineQ messageLineQ = new Message().lineQ();
                try {
                    File file = new File(FileHandle.imageCachePath, "xibao_" + System.currentTimeMillis());
                    BufferedImage bufferedImage = ImageHandle.getXiBao(exception.getClass().getCanonicalName() + ":" + exception.getMessage(),
                            new Font("黑体", Font.BOLD, 168));
                    ImageIO.write(bufferedImage, "jpg", file);
                    Image image = MyYuQ.uploadImage(task.getContact(), file);
                    messageLineQ.plus(image).getMessage();
                } catch (IOException e1) {
                    SfLog.getInstance().e(this.getClass(), e1);
                    messageLineQ.text("错误：" + e1.getMessage()).getMessage();
                }
                //异常信息打印
                SfLog.getInstance().e(this.getClass(), exception);

                task.getContact().sendMessage(messageLineQ);
            }
        }
    }

    public int size(){
        return taskList.size();
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isWait() {
        return isWait;
    }

    public void setWait(boolean wait) {
        isWait = wait;
    }

    /**
     * 任务对象
     */
    @Data
    public static class Task{
        private Contact contact;
        private int weight = WEIGHT_MIDDLE;//优先级
        private NetworkLoaderListener listener;//监听器
        private String url;//url
        private File toFile;//保存位置
        private String md5 = "";//如果不为空就会进行md5检查

        public Task(Contact contact, String url, File toFile, NetworkLoaderListener listener) {
            this.contact = contact;
            this.listener = listener;
            this.url = url;
            this.toFile = toFile;
        }

        public Task(Contact contact, int weight, String url, File toFile, String md5, NetworkLoaderListener listener) {
            this.contact = contact;
            this.weight = weight;
            this.listener = listener;
            this.url = url;
            this.toFile = toFile;
            this.md5 = md5;
        }
    }

    /**
     * 监听器
     */
    public interface NetworkLoaderListener{
        void start(long len);//开始
        void success(File file) throws Exception;//完成
        void fail(Exception e)  throws Exception;//失败
        void progress(long pro, long len, long speed);//进度
    }
}
