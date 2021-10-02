package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.gif.GifDecoder;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.jar.JarFile;

@GroupController
@Menu(name = "骰子")
public class RandomController {

    @Action("\\[.。!！][Rr][Dd]$\\")
    @MenuItem(name = "随机一个数", usage = "[.。!！][Rr][Dd] {text/num} {num} (参数可选)", description = "指定或不指定（默认100）随机一个范围内数")
    public Message roll(Message message){
        int num = 100;

        String reText = "";

        String msgText = message.getCodeStr();
        String msgs[] = msgText.split(" ");

        if (msgs.length > 1){
            try {
                num = Integer.valueOf(msgs[1]);
                if (num <= 0){
                    num = 100;
                }
                if (msgs.length > 2){
                    String flag = msgs[2];
                    reText = "随机[" + flag + "]得到：" + MyYuQ.getRandom(0, num);
                }else {
                    reText = MyYuQ.getRandom(0, num) + "";
                }
            }catch (Exception e){
                String flag = msgs[1];
                reText = "随机[" + flag + "]得到：" + MyYuQ.getRandom(0, num);
            }
        }else {
            reText = MyYuQ.getRandom(0, num) + "";
        }

        Message reMsg = MyYuQ.getMif().text(reText).toMessage();
        reMsg.setReply(message.getSource());
        return reMsg;
    }

    @Action("\\[.。!！][Rr][Aa]$\\ {name} {var}")
    @MenuItem(name = "Ra", usage = "[.。!！]ra {name} {var}", description = "骰娘")
    public Message randomAppraisal(Member sender, String name, int var){
        if (var <= 0){
            return new Message().lineQ().at(sender).text("\n").text("设定值：" + var + " 不合理，请设置在0~100区间内").getMessage();
        }
        if (var > 80){
            var = 80;
        }

        int rdVar = MyYuQ.getRandom(1, 100);
        String result = "大失败";
        if (rdVar < 5){
            result = "大成功";
        }else if (rdVar < (float) var * 0.2){
            result = "极难成功";
        }else if (rdVar < (float) var * 0.50){
            result = "困难成功";
        }else if (rdVar > var) {
            result = "失败";
        }else if (rdVar > 95){
            result = "大失败";
        }else {
            result = "普通成功";
        }

        return new Message().lineQ().at(sender).text("\n").textLine("设置值：" + var).textLine("对[" + name + "]进行判定，判定值:" + rdVar).text(result).getMessage();
    }

    @Action("抽一位幸运群友")
    @Synonym({"抽个幸运群友", "抽一个幸运群友", "抽个幸运群员", "抽一个幸运群员", "抽一位幸运群员"})
    @MenuItem(name = "抽一位幸运群友", usage = "@Bot 抽一位幸运群友", description = "抽一位幸运群友")
    @QMsg(mastAtBot = true)
    public Message randomMember(Group group){
        ArrayList<Member> members = new ArrayList<>();
        for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
            members.add(entry.getValue());
        }
        Member member = members.get(MyYuQ.getRandom(0, members.size() - 1));
        String name = member.getNameCard();
        if (name == null || name.equals("")){
            name = member.getName();
        }
        return new Message().lineQ().textLine("抽中了:" + name + "[" + member.getId() + "]").imageByUrl(member.getAvatar()).getMessage();
    }

    @Action("抽一位幸运管理")
    @Synonym({"抽个幸运管理", "抽一个幸运管理","抽一位幸运管理员","抽个幸运管理员", "抽一个幸运管理员"})
    @MenuItem(name = "抽个幸运管理", usage = "@Bot 抽个幸运管理", description = "抽个幸运管理")
    @QMsg(mastAtBot = true)
    public Message randomAdminMember(Group group){
        ArrayList<Member> members = new ArrayList<>();
        for (Map.Entry<Long, Member> entry:group.getMembers().entrySet()){
            if (entry.getValue().isAdmin()){
                members.add(entry.getValue());
            }
        }
        Member member = members.get(MyYuQ.getRandom(0, members.size() - 1));
        return new Message().lineQ().textLine("抽中了:" + member.getNameCard() + "[" + member.getId() + "]").imageByUrl(member.getAvatar()).getMessage();
    }

    @Action("抽一位幸运群主")
    @Synonym({"抽个幸运群主", "抽一个幸运群主"})
    @MenuItem(name = "抽一位幸运群主", usage = "@Bot 抽一位幸运群主", description = "抽一位幸运群主")
    @QMsg(mastAtBot = true)
    public Message randomOwnerMember(Group group){
        Member member = group.getOwner();
        return new Message().lineQ().textLine("抽中了:" + member.getNameCard() + "[" + member.getId() + "]").imageByUrl(member.getAvatar()).getMessage();
    }


    @Action("抽个禁言")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "抽个禁言", usage = "@Bot 抽个禁言", description = "抽个禁言")
    public void randomBan(Group group, Member sender) throws IOException {
        int banTimes[] = {60, 300, 600, 1800, 3600, 7200, 86400, 259200, 2592000};
        int banTime = banTimes[MyYuQ.getRandom(0, banTimes.length - 1)];

        File imageFile = new File(FileHandle.imageCachePath, "/random_ban_" + new Date().getTime());
        URL url = getClass().getClassLoader().getResource("image/ban/" + banTime);
        if (url == null){
            SfLog.getInstance().e(this.getClass(), "资源文件丢失：image/ban/" + banTime);
            return;
        }

        BufferedImage bufferedImage = ImageIO.read(url);
        ImageIO.write(bufferedImage, "png", imageFile);

        MessageLineQ messageLineQ = new Message().lineQ();
        messageLineQ.at(sender);
        messageLineQ.textLine("");
        if (banTime > 60 * 60 * 24){
            messageLineQ.textLine("恭喜中大奖！！！");
        }else {
            messageLineQ.textLine("恭喜中奖！！！");
        }
        messageLineQ.imageByFile(imageFile);

        group.sendMessage(messageLineQ.getMessage());

        if ((group.getBot().isAdmin() || group.getBot().isOwner())
                && (!sender.isOwner() && !sender.isOwner())){
            sender.ban(banTime);
            SfLog.getInstance().w(this.getClass(), "禁言：" + sender);

            //设置定时任务，45秒后取消
            MyYuQ.getJobManager().registerTimer(new Runnable() {
                @Override
                public void run() {
                    sender.unBan();//取消禁言
                    SfLog.getInstance().w(this.getClass(), "取消禁言：" + sender);
                }
            }, 45 * 1000);
        }else {
            group.sendMessage(new Message().lineQ().at(sender).textLine("").textLine("已经给你塞上口球了").text("时间没到之前不能说话哦"));
        }
    }

    @Catch(error = IOException.class)
    public String iOException(IOException e){
        return "发生错误：" + e.getMessage();
    }

}
