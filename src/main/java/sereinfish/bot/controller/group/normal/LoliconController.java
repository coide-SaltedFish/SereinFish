package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.cache.CacheManager;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlId;
import sereinfish.bot.entity.lolicon.Lolicon;
import sereinfish.bot.entity.lolicon.LoliconManager;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@GroupController
@PrivateController
public class LoliconController {
    private boolean isGroupMsg = false;//是否是群聊消息
    private Group group;
    private Contact qq;
    private GroupConf conf;
    private Message message;

    private String apiKey;

    @Before
    public void before(Group group, Contact qq, Message message){
        this.group = group;
        this.qq = qq;
        this.message = message;
        if (group == null){
            isGroupMsg = false;
        }else {
            isGroupMsg = true;
        }
        if (qq == null){

        }
        if (isGroupMsg){
            conf = GroupConfManager.getInstance().get(group.getId());
            apiKey = (String) conf.getControl(GroupControlId.Edit_SetuKey).getValue();
            //检查群功能是否启用
            if (!(Boolean) conf.getControl(GroupControlId.CheckBox_SetuEnable).getValue()){
                throw new DoNone();
            }
            if (apiKey.trim().equals("")){
                this.apiKey = null;
            }
        }else {
            apiKey = null;
        }
    }

    @Action("\\img_\\{B407F708-A2C6-A506-3420-98DF7CAC4A57\\}\\")
    @Synonym({"\\[!！.]setu\\", "\\[!！.]色图\\"})
    public void getST(){
        Lolicon.Request request = getRequest(null, 1);
        try {
            SfLog.getInstance().d(this.getClass(),"Lolicon 获取中");
            Lolicon lolicon = LoliconManager.getLolicon(request);
            if (lolicon.getCode() == Lolicon.SUCCESS){
                if (lolicon.getQuota() == 0){
                    sendMessage(Message.Companion.toMessageByRainCode("<Rain:Image:{2B15CC31-8393-68DA-A35C-8F314661FF13}.jpg>"));
                    return;
                }else {
                    for (Lolicon.Setu setu:lolicon.getData()){
                        File file = CacheManager.getLoliconImage(setu.getPid(),setu);
                        SfLog.getInstance().d(this.getClass(),"返回：" + file);
                        sendMessage(MyYuQ.getMif().imageByFile(file).toMessage(), setu);
                    }
                }
            }else {
                loliconErr(lolicon,request);
            }
        } catch (IOException e) {
            sendMessage("错误:" + e.getMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    @Action("\\img_\\{7CF98559-280F-D216-C5C4-8AA3D22A8815\\}\\")
    @QMsg(mastAtBot = true)
    public void getST_2(){
        Lolicon.Request request = getRequest(null, 1);
        try {
            SfLog.getInstance().d(this.getClass(),"Lolicon 获取中");
            Lolicon lolicon = LoliconManager.getLolicon(request);
            if (lolicon.getCode() == Lolicon.SUCCESS){
                if (lolicon.getQuota() == 0){
                    sendMessage(Message.Companion.toMessageByRainCode("<Rain:Image:{2B15CC31-8393-68DA-A35C-8F314661FF13}.jpg>"));
                    return;
                }else {
                    for (Lolicon.Setu setu:lolicon.getData()){
                        File file = CacheManager.getLoliconImage(setu.getPid(),setu);
                        SfLog.getInstance().d(this.getClass(),"返回：" + file);
                        sendMessage(MyYuQ.getMif().imageByFile(file).toMessage(), setu);
                    }
                }
            }else {
                loliconErr(lolicon,request);
            }
        } catch (IOException e) {
            sendMessage("错误:" + e.getMessage());
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    @Action("\\[!！.]LoliconKey\\ {key}")
    public void apiKey(String key){
        conf.getControl(GroupControlId.Edit_SetuKey).setValue(key);
        if(GroupConfManager.getInstance().put(conf)){
            sendMessage("设置成功");
        }else {
            sendMessage("设置成功");
        }
    }

    @Action("\\img_\\{8F2283DA-2199-823A-C507-B452F386D654\\}\\")
    @QMsg(mastAtBot = true)
    public void enableR18(Member sender){
        if (isGroupMsg){
            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(true);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                sendMessage(Message.Companion.toMessageByRainCode("<Rain:Image:{241A6BB9-5CC0-2A98-CCE6-48BEE17148D0}.jpg>"));
            }
        }
    }

    @Action("\\img_\\{982776A6-AA3D-D49A-9DE4-57F8EABE4EB0\\}\\")
    @QMsg(mastAtBot = true)
    public void enableNoR18(Member sender){
        if (isGroupMsg){
            //权限判断
            if (AuthorityManagement.getInstance().authorityCheck(sender,AuthorityManagement.GROUP_ADMIN)){
                conf.getControl(GroupControlId.CheckBox_SetuR18).setValue(false);
                conf.getControl(GroupControlId.CheckBox_PlainAndR18).setValue(false);
                GroupConfManager.getInstance().put(conf);
                sendMessage(Message.Companion.toMessageByRainCode("<Rain:Image:{2B15CC31-8393-68DA-A35C-8F314661FF13}.jpg>"));
            }
        }
    }

    /**
     * 请求失败处理
     * @param lolicon
     * @param request
     */
    private void loliconErr(Lolicon lolicon, Lolicon.Request request){
        switch (lolicon.getCode()) {
            case Lolicon.APIKEY_ERR:
                sendMessage("错误>>APIKEY:" + lolicon.getMsg());
                break;
            case Lolicon.ERR:
                sendMessage("错误>>Lolicon:" + lolicon.getMsg());
                break;
            case Lolicon.QUOTA_ERR:
                if (isGroupMsg) {
                    if ((Boolean) conf.getControl(GroupControlId.CheckBox_LocalImage).getValue()){
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

                        if (file != null){
                            SfLog.getInstance().d(this.getClass(),"返回：" + file);
                            sendMessage(MyYuQ.getMif().imageByFile(file).toMessage(), new Lolicon.Setu(false));
                        }
                    } else {
                        sendMessage("错误>>额度上限:" + lolicon.getMsg());
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
                        SfLog.getInstance().d(this.getClass(),"返回：" + file);
                        sendMessage(MyYuQ.getMif().imageByFile(file).toMessage(), new Lolicon.Setu(false));
                    }
                }
                break;
            default:
                sendMessage("错误:" + lolicon.getMsg());
        }
    }

    /**
     * 得到请求信息
     * @return
     */
    private Lolicon.Request getRequest(String keyWord, int num){
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

    /**
     * 发送消息
     * @param message
     */
    private void sendMessage(Message message, Lolicon.Setu setu){
        if (isGroupMsg){
            MyYuQ.sendGroupMessage(group,message);
        }else {
            MyYuQ.sendMessage(qq,message);
        }
        //如果是R18，延时撤回
        int time = 25000;//25s
        if (setu.isR18()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(time);
                    message.recall();
                }

                public void sleep(int ms){
                    try {
                        Thread.sleep(ms);
                    } catch (InterruptedException e) {
                        message.recall();
                        SfLog.getInstance().e(this.getClass(),e);
                    }
                }
            }).start();
        }
    }

    /**
     * 发送消息
     * @param msg
     */
    private void sendMessage(String msg){
        if (isGroupMsg){
            MyYuQ.sendGroupMessage(group,MyYuQ.getMif().text(msg).toMessage());
        }else {
            MyYuQ.sendMessage(qq,MyYuQ.getMif().text(msg).toMessage());
        }
    }

    /**
     * 发送消息
     * @param message
     */
    private void sendMessage(Message message){
        if (isGroupMsg){
            MyYuQ.sendGroupMessage(group,message);
        }else {
            MyYuQ.sendMessage(qq,message);
        }
    }
}
