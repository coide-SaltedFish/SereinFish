package sereinfish.bot.controller;

import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Global;
import com.IceCreamQAQ.Yu.controller.ActionContext;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import sereinfish.bot.data.conf.ConfManager;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@GroupController
@PrivateController
public class GlobalController {

    @Before(weight = -1000)
    @Global
    public void before(ActionContext actionContext, Group group){
        if (group != null){
            actionContext.set("group", group);
            GroupConf groupConf = ConfManager.getInstance().get(group.getId());
            actionContext.set("groupConf", groupConf);
        }
    }

    @Catch(error = Exception.class)
    @Global
    public void exception(Exception exception, BotActionContext actionContext){
        //拦截全局异常
        MessageLineQ messageLineQ = new Message().lineQ();
        try {
            File file = new File(FileHandle.imageCachePath, "xibao_" + System.currentTimeMillis());
            BufferedImage bufferedImage = ImageHandle.getXiBao(exception.getClass().getCanonicalName() + ":" + exception.getMessage(),
                    new Font("黑体", Font.BOLD, 168));
            ImageIO.write(bufferedImage, "jpg", file);
            Image image = MyYuQ.uploadImage(actionContext.getSource(), file);
            messageLineQ.plus(image).getMessage();
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), e);
            messageLineQ.text("错误：" + e.getMessage()).getMessage();
        }
        //异常信息打印
        SfLog.getInstance().e(this.getClass(), exception);

        throw messageLineQ.getMessage().toThrowable();
    }
}
