package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Path;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.QMsg;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.entity.arknights.penguinStatistics.PenguinStatistics;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.utils.OkHttpUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@GroupController
@Menu(type = Menu.Type.GROUP, name = "企鹅物流")
public class ArkNightController {

    @Action("方舟掉落 {name}")
    @Synonym("企鹅物流 {name}")
    @QMsg(mastAtBot = true)
    @MenuItem(name = "企鹅物流物品掉落查询", usage = "@Bot 方舟掉落 {name}", description = "在企鹅物流查询物品掉落数据")
    public Message penguinStatisticsQuery(Group group, String name){
        group.sendMessage(MyYuQ.getMif().text(getWaitMsg()).toMessage());
        try {
            PenguinStatistics penguinStatistics = PenguinStatistics.getInstance();
            String itemId = penguinStatistics.getItemId(name);
            if (itemId.equals("")){
                return MyYuQ.getMif().text("查询不到物品[" + name + "]的信息呢").toMessage();
            }else {
                PenguinStatistics.PenguinWidgetData penguinWidgetData = penguinStatistics.getPenguinWidgetData(itemId);
                if (penguinWidgetData == null){
                    SfLog.getInstance().e(this.getClass(), "企鹅物流查询到的数据为null:" + name);
                    return MyYuQ.getMif().text("摸鱼中，一会再试试看喵").toMessage();
                }
                if (penguinWidgetData.getError() != null){
                    return new Message().lineQ().textLine("出现错误了:" + name + ">>").text(penguinWidgetData.getError().getDetails()).getMessage();
                }
                //先保存为图片
                File file = new File(FileHandle.imageCachePath,"penguinStatisticsQuery_temp");
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                try {
                    ImageIO.write(penguinStatistics.getDataImage(penguinWidgetData), "PNG", file);
                    Image image = group.uploadImage(file);
                    return new Message().lineQ().plus(image).getMessage();
                } catch (Exception e) {
                    SfLog.getInstance().e(this.getClass(),e);
                    return MyYuQ.getMif().text("图片发送失败：" + e.getMessage()).toMessage();
                }
            }
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            return MyYuQ.getMif().text("摸鱼中，一会再试试看喵").toMessage();
        }
    }

    private String getWaitMsg(){
        String character[] = {"能天使", "阿能", "德克萨斯", "可颂", "小莫", "莫斯提马", "空"};
        String things[] = {"摸鱼中", "大喊\"德克萨斯做得到吗！\"", "阿巴阿巴", "加急送来", "发呆", "睡大觉", "祈祷中..", " ciallo⭐", "暗中观察"};

        return "[" + character[MyYuQ.getRandom(0, character.length - 1)] + "正在" + things[MyYuQ.getRandom(0, things.length - 1)] + "]";
     }
}
