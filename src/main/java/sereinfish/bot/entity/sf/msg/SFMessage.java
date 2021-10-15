package sereinfish.bot.entity.sf.msg;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.entity.ClassManager;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.mlog.SfLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFMessage {
    private Map<String, SFMsgCode> classMap = new HashMap<>();//标签类列表

    private static SFMessage sfMessage;
    private SFMessage(){
        for (Class c: ClassManager.getInstance().getClassList(SFMsgCodeInfo.class)){
            SFMsgCodeInfo sfMsgCodeInfo = (SFMsgCodeInfo) c.getAnnotation(SFMsgCodeInfo.class);
            if (ClassManager.hasInterfaces(c, SFMsgCode.class)){
                try {
                    SFMsgCode sfMsgCode = (SFMsgCode) c.newInstance();
                    if (classMap.containsKey(sfMsgCodeInfo.value().toLowerCase())){
                        SfLog.getInstance().w(this.getClass(), "SFCode冲突警告：" + sfMsgCodeInfo.value() + "\n" + c.getTypeName());
                    }

                    classMap.put(sfMsgCodeInfo.value().toLowerCase(), sfMsgCode);
                }catch (IllegalAccessException e){
                    SfLog.getInstance().e(this.getClass(),"类型实例化失败：" + sfMsgCodeInfo.value(), e);
                }catch (InstantiationException e){
                    SfLog.getInstance().e(this.getClass(),"类型实例化失败：" + sfMsgCodeInfo.value(), e);
                }
            }
        }
    }

    public static SFMessage getInstance(){
        if (sfMessage == null){
            sfMessage = new SFMessage();
        }
        return sfMessage;
    }

    /**
     * sf码转消息
     * @return
     */
    public ArrayList<Message> sfCodeToMessage(SFMsgCodeContact sfMsgCodeContact, String code){
        ArrayList<Message> messages = new ArrayList<>();
        for (String str:sfCodeToRainCode(sfMsgCodeContact, code)){
            System.out.println(str);
            if (str.startsWith("<SF:Reply>") && sfMsgCodeContact.getBotActionContext() != null){
                str = str.substring("<SF:Reply>".length());
                Message message = sfMsgCodeContact.getReMessage().plus(Message.Companion.toMessageByRainCode(str));
                message.setReply(sfMsgCodeContact.getBotActionContext().getMessage().getSource());
                messages.add(message);
            }else {
                messages.add(sfMsgCodeContact.getReMessage().plus(Message.Companion.toMessageByRainCode(str)));
            }

        }
        return messages;
    }

    /**
     * sf码转Rain码
     * @return
     */
    public String[] sfCodeToRainCode(SFMsgCodeContact codeContact, String code){
        StringBuilder stringBuilder = new StringBuilder(code);

        Pattern pattern=Pattern.compile("<SF:.+?>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        ArrayList<Replace> replaces = new ArrayList<>();

        while (matcher.find()){
            String gr = matcher.group();
            //去除头尾
            String c = gr.substring(1, gr.length() - 1);

            String[] p = c.split(":", 3);
            if (p.length >= 2){
                String type = p[1].toLowerCase();
                String para = "";
                if (p.length > 2){
                    para = p[2];
                }

                if (!classMap.containsKey(type)){
                    continue;
                }

                //匹配函数
                codeContact.setParameter(para);

                try {
                    String reCode = classMap.get(type).code(codeContact);
                    if (reCode != null){
                        replaces.add(new Replace(matcher.start(), matcher.end(), reCode));
                    }
                }catch (DoNone doNone){
                    //doNone
                }catch (Exception e){
                    try {
                        String reCode = classMap.get(type).error(e);
                        if (reCode != null){
                            replaces.add(new Replace(matcher.start(), matcher.end(), reCode));
                        }
                    }catch (DoNone doNone){
                        //doNone
                    }
                }
            }
        }

        //从后向前替换
        for (int i = replaces.size() - 1; i >= 0; i--){
            Replace replace = replaces.get(i);
            stringBuilder.replace(replace.start, replace.end, replace.reCode);
        }

        return stringBuilder.toString().split("<SF:Split>");
    }

    @Getter
    @AllArgsConstructor
    class Replace{
        int start;
        int end;
        String reCode;
    }
}
