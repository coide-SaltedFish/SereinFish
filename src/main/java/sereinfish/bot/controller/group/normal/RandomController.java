package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.myYuq.MyYuQ;

import java.util.ArrayList;
import java.util.Map;

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
    @Synonym({"抽个幸运群友", "抽一个幸运群友"})
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


}
