package sereinfish.bot.controller.group.normal;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.database.service.GroupHistoryMsgService;
import sereinfish.bot.entity.image.wordCloud.MyWordCloud;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@GroupController
public class WordCloudController {

    @Inject
    GroupHistoryMsgService groupHistoryMsgService;

    //@Action("今日词云")
    public Message createGroupWordCloud(Group group, @PathVar(value = 1, type = PathVar.Type.Source) Image image) throws IOException {
        //默认背景图片
        BufferedImage bufferedImage = null;
        //如果有指定图片
        if (image != null){
            bufferedImage = ImageIO.read(new URL(image.getUrl()));
        }else {
            bufferedImage = ImageIO.read(getClass().getClassLoader().getResource("arknights/" + MyYuQ.getRandom(1, 5) + ".png"));
        }
        List<String> list =  groupHistoryMsgService.findLastTimeByGroupToRainCode(group.getId(), Time.getTodayStart());
        group.sendMessage("正在加载" + list.size() + "条消息...");

        File file = new File(FileHandle.imageCachePath,"wordCloud_temp_" + System.currentTimeMillis());

        //BufferedImage wordCloud = MyWordCloud.getWordCloud(list, bufferedImage);

        //ImageIO.write(wordCloud, "PNG", file);
        return new Message().lineQ().plus(MyYuQ.uploadImage(group, file)).getMessage();
    }
}
