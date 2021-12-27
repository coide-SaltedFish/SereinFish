package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.entity.aSoul.asf.tool.IngredientChecking;
import sereinfish.bot.entity.bili.entity.info.Data;
import sereinfish.bot.entity.bili.entity.info.follow.Follow;
import sereinfish.bot.entity.bili.entity.info.search.UserSearch;
import sereinfish.bot.entity.bili.entity.vtbs.VtbsInfo;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.NetworkLoader;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GroupController
public class VtbController {
    boolean isRun = false;

    @Action("更新vtb列表")
    @QMsg(mastAtBot = true, reply = true)
    public String listUpdate() throws Exception {
        VtbsInfo.INSTANCE.updateData();

        return "完成,Vtb列表长度：" + VtbsInfo.INSTANCE.getDatas().length;
    }

    @Action("查成分 {name}")
    @QMsg(mastAtBot = true, reply = true)
    public void ddCheck(String name, Group group, Message message) throws Exception{
        if (isRun){
            Message reMsg = new Message().lineQ().text("正在处理上一个事务，稍等捏").getMessage();
            reMsg.setReply(message.getSource());
            group.sendMessage(reMsg);
            return;
        }
        isRun = true;

        try {
            ddCheckF(name, group, message);
            isRun = false;
        }catch (Exception e){
            isRun = false;
            throw e;
        }
    }

    private void ddCheckF(String name, Group group, Message message) throws Exception {
        Message reMsg = new Message().lineQ().text("结果生成中，请稍后").getMessage();
        reMsg.setReply(message.getSource());
        group.sendMessage(reMsg);

        //得到用户信息
        long mid = -1;

        try {
            for(Data d:new UserSearch(name).getSearchRes().getData().getResult()){
                if (d.getUname().equals(name)){
                    mid = d.getMid();
                    break;
                }
            }
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), "用户信息获取失败", e);
        }

        //得到关注信息
        IngredientChecking ingredientChecking = new IngredientChecking(name);

        //生成mirai消息
        MessageChainBuilder messageChain = new MessageChainBuilder();

        messageChain.append(new PlainText("结果生成时间：" + Time.dateToString(new Date(), Time.LOG_TIME_)));

        messageChain.append(new PlainText("\n查询用户：" + name));

        //判断是否成功
        if (ingredientChecking.getData().getCode() != 0){
            messageChain.append("\n失败：");
            messageChain.append(ingredientChecking.getData().getMessage());
            if (ingredientChecking.getData().getCode() == 22115){
                messageChain.append(new PlainText("\n啥成分就不用说了捏"));
            }

            Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage(messageChain.asMessageChain());
            return;
        }

        if (ingredientChecking.getData().getData().getList().length == 0){
            messageChain.append(new PlainText("\n这个人没有关注任何收录的虚拟主播捏"));

            messageChain.append(new PlainText("\n数据来源：asoulfan.com"));

            Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage(messageChain.asMessageChain());
            return;
        }

        if (ingredientChecking.getData().getData().getList().length > 15){
            reMsg = new Message().lineQ().text("共 " + ingredientChecking.getData().getData().getList().length + " 位，加载时间可能会比较长，请耐心等待").getMessage();
            reMsg.setReply(message.getSource());
            group.sendMessage(reMsg);
        }

        //得到总关注数
        float con = 0;
        if (mid != -1){
            Follow follow = new Follow(mid);
            messageChain.append(new PlainText("\n总关注数：" + follow.getFollowInfo().getData().getFollowing()));

            //浓度
            con = (ingredientChecking.getData().getData().getList().length / (float) follow.getFollowInfo().getData().getFollowing()) * 100;

            messageChain.append(new PlainText(String.format("\n浓度：%.2f", con) + "%"));

            if (con == 100){
                messageChain.append(new PlainText("\n啊这，我不太好说"));
            }else if (con > 80){
                messageChain.append(new PlainText("\n浓度爆表"));
            }else if (con > 50){
                messageChain.append(new PlainText("\n很行"));
            }
        }

        messageChain.append(new PlainText("\n成分列表如下："));

        int num = 1;
        List<ForwardMessage.Node> nodeList = new ArrayList<>();
//        ForwardMessageBuilder builder = new ForwardMessageBuilder(Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()));
        for (Data data:ingredientChecking.getData().getData().getList()){
            messageChain.append("\n" + num + ".");
            //头像
            try {
                NetworkLoader.INSTANCE.setWait(true);
                File file = NetHandle.imageDownload(data.getFace(), "bili_/" + data.getFace().substring(data.getFace().lastIndexOf("/") + 1));
                NetworkLoader.INSTANCE.setWait(false);
                //头像加工
                String md5 = data.getFace().substring(data.getFace().lastIndexOf("/") + 1).substring(0, data.getFace().substring(data.getFace().lastIndexOf("/") + 1).lastIndexOf("."));
                File imageHeadCircular = new File(FileHandle.imageCachePath, "bill_circular_" + md5);

                if (!imageHeadCircular.exists()){
                    BufferedImage bufferedImage = ImageIO.read(file);
                    bufferedImage = ImageHandle.imageToBufferedImage(ImageHandle.getHeadImageNoFrame(bufferedImage, 640, 640));
                    ImageIO.write(bufferedImage, "PNG", imageHeadCircular);
                    SfLog.getInstance().d(this.getClass(), "图片生成完成：" + imageHeadCircular);
                }else {
                    SfLog.getInstance().d(this.getClass(), "使用缓存图片：" + imageHeadCircular);
                }

                //计算文件md5
                //String fileMd5 = DigestUtils.md5Hex(new FileInputStream(imageHeadCircular)).toUpperCase();

                ExternalResource res = ExternalResource.create(imageHeadCircular);
                net.mamoe.mirai.message.data.Image image = Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).uploadImage(res);
                res.close(); // 记得关闭资源
                messageChain.append(image);

//                if (!MyYuQ.imageEnableTX(fileMd5)){
////                    Image image = group.uploadImage(headImageFile);
//                    if (!headImageFile.delete()){
//                        SfLog.getInstance().w(this.getClass(), "文件删除失败：" + headImageFile);
//                    }
////                    messageChain.append(net.mamoe.mirai.message.data.Image.fromId(MyYuQ.getMiraiImageId(image.getId())));
//                }else {
////                    messageChain.append(net.mamoe.mirai.message.data.Image.fromId(MyYuQ.getMiraiImageId(fileMd5, "jpg")));
//                }

            } catch (IOException e) {
                messageChain.append(new PlainText("\n失败：头像加载错误"));
            }
            messageChain.append(new PlainText("\n" + data.getUname()));//名称
            messageChain.append(new PlainText("\n主页链接：https://space.bilibili.com/" + data.getMid()));

            if (num != 0 && num % 10 == 0){
                //消息整合
//                MessageReceipt<net.mamoe.mirai.contact.Group> messageReceipt = Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage(messageChain.asMessageChain());
//                if (ingredientChecking.getData().getData().getList().length > 3){
//                    try {
//                        messageReceipt.recall();//撤回
//                    }catch (Exception e){
//                        SfLog.getInstance().e(this.getClass(), e);
//                    }
//                }
                nodeList.add(new ForwardMessage.Node(MyYuQ.getYuQ().getBotId(), Time.getSecondTimestamp(), MyYuQ.getBotName(), messageChain.asMessageChain()));
//                builder.add(MyYuQ.getYuQ().getBotId(), MyYuQ.getBotName(), messageChain.asMessageChain());
                messageChain = new MessageChainBuilder();
            }

            num ++;
        }
        messageChain.append(new PlainText("\n数据来源：asoulfan.com"));

        //消息整合
//        MessageReceipt<net.mamoe.mirai.contact.Group> messageReceipt = Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage(messageChain.asMessageChain());
//        if (ingredientChecking.getData().getData().getList().length > 3){
//            try {
//                messageReceipt.recall();//撤回
//            }catch (Exception e){
//                SfLog.getInstance().e(this.getClass(), e);
//            }
//        }
        nodeList.add(new ForwardMessage.Node(MyYuQ.getYuQ().getBotId(), Time.getSecondTimestamp(), MyYuQ.getBotName(), messageChain.asMessageChain()));
//        builder.add(MyYuQ.getYuQ().getBotId(), MyYuQ.getBotName(), messageChain.asMessageChain());
        //合并转发
        if (ingredientChecking.getData().getData().getList().length > 3){
            List<String> preList = new ArrayList<>();
            preList.add(name + " 的成分查询：");
            preList.add(String.format("浓度：%.2f", con) + "%");
            preList.add("共 " + (num - 1) + " 位");

            ForwardMessage forwardMessage = new ForwardMessage(preList, "转发的聊天记录", "[聊天记录]", "", "SereinFish Bot", nodeList);

            SfLog.getInstance().d(this.getClass(), "合并转发");

            Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage("查询结果如下");
            Bot.getInstance(MyYuQ.getYuQ().getBotId()).getGroup(group.getId()).sendMessage(forwardMessage);
        }
    }
}
