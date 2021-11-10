package sereinfish.bot.controller.group.admin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PathVar;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.database.service.BlackListService;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.database.entity.BlackList;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.jsonEx.JsonMsg;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 黑名单相关控制器
 */
@GroupController
@Menu(name = "黑名单", permissions = Permissions.GROUP_ADMIN)
public class BlackListController {

    @Inject
    private BlackListService blackListService;

    private int page_num = 5;
    /**
     * 权限检查
     */
    @Before
    public void before(Group group, GroupConf groupConf, Member sender, Message message){
        if (!Permissions.getInstance().authorityCheck(group, sender, Permissions.GROUP_ADMIN)) { //权限检查
            Message msg = MyYuQ.getMif().text("你没有权限使用这个命令喵").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
        if (!groupConf.isBlackListGroupEnable()){//判断黑名单是否启用
            Message msg = MyYuQ.getMif().text("功能未启用").toMessage();
            msg.setReply(message.getSource());
            throw msg.toThrowable();
        }
    }

    /**
     * 添加黑名单
     * @param qq        qq
     * @param remake    备注
     */
    @Action("\\^[.!！]黑名单添加$\\ {qq}")
    @Synonym({"\\^[.!！]加黑$\\ {qq}"})
    @MenuItem(name = "黑名单添加", usage = "[.!！]黑名单添加 {qq} {remake}", description = "添加到本群黑名单", permission = Permissions.GROUP_ADMIN)
    public Message add(Group group, long qq,@PathVar(2) String remake){
        //判断是否已存在
        if (blackListService.exist(qq)){
            return MyYuQ.getMif().text("[" + qq + "]已存在于本群黑名单").toMessage();
        }else {
            blackListService.save(new BlackList(qq, group.getId(), remake));
            return MyYuQ.getMif().text("[" + qq + "]已添加到本群黑名单").toMessage();
        }
    }

    /**
     * 删除黑名单
     * @param qq        qq
     */
    @Action("\\^[.!！]黑名单删除$\\ {qq}")
    @Synonym({"\\^[.!！]删黑$\\ {qq}"})
    @MenuItem(name = "黑名单删除", usage = "[.!！]黑名单删除 {qq}", description = "移出本群黑名单", permission = Permissions.GROUP_ADMIN)
    public Message delete(Group group, long qq){
        //判断是否已存在
        if (!blackListService.exist(qq)){
            return MyYuQ.getMif().text("[" + qq + "]不存在于本群黑名单").toMessage();
        }else {
            blackListService.deleteByGroupAndQq(group.getId(),qq);
            return MyYuQ.getMif().text("[" + qq + "]已从本群黑名单移除").toMessage();
        }
    }

    /**
     * 查询本群黑名单
     */
    @Action("\\^[.!！]本群黑名单$\\")
    @Synonym("\\^[.!！]黑名单$\\")
    public Message query(Group group,@PathVar(1) String pageStr){
        int page = 1;
        try {
            page = Integer.decode(pageStr);
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(), "页数输入错误：" + pageStr);
        }

        if (page < 1){
            return MyYuQ.getMif().text("页面错误：" + page).toMessage();
        }
        List<BlackList> lists = blackListService.findByGroup(group.getId());
        int page_max = lists.size() / page_num + (lists.size() % page_num == 0 ? 0:1);
        if (page > page_max){
            return MyYuQ.getMif().text("页面错误,最大页数[" + page_max + "]：" + page).toMessage();
        }
        ArrayList<String> msgList = new ArrayList<>();
        for (int i = (page - 1) * page_num; i < lists.size() && i < (page - 1) * page_num + page_num; i++){
            BlackList blackList = lists.get(i);
            msgList.add(Time.dateToString(new Date(blackList.getTime()),Time.DATE_FORMAT) + ":[" + blackList.getQq() + "]" + blackList.getRemarks());
        }
        return MyYuQ.getMif().jsonEx(JsonMsg.getMenuCardNoAction("[黑名单列表(" + page + "/" + page_max + ")]",
                "本群黑名单列表(" + page + "/" + page_max + ")","http://q1.qlogo.cn/g?b=qq&nk=" + MyYuQ.getYuQ().getBotId() + "&s=640",
                msgList.toArray(new String[0]))).toMessage();
    }
}
