package sereinfish.bot.entity.sf.msg;

import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
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

                    String[] names = sfMsgCodeInfo.value();
                    for (String name:names){
                        name = name.toLowerCase();

                        if (classMap.containsKey(name)){
                            SfLog.getInstance().w(this.getClass(), "SFCode冲突警告：" + sfMsgCodeInfo.value() + "\n" + c.getTypeName());
                        }

                        classMap.put(name, sfMsgCode);
                    }
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
    public ArrayList<SFMessageEntity> sfCodeToMessage(SFMsgCodeContact sfMsgCodeContact, String code){
        ArrayList<SFMessageEntity> messages = new ArrayList<>();
        for (Msg msg:sfCodeToRainCode(sfMsgCodeContact, code)){
            if (msg.rainCode.startsWith("<SF:Reply>") && sfMsgCodeContact.getBotActionContext() != null){
                String str = msg.rainCode.substring("<SF:Reply>".length());
                Message message = sfMsgCodeContact.getReMessage().plus(Message.Companion.toMessageByRainCode(str));
                message.setReply(sfMsgCodeContact.getBotActionContext().getMessage().getSource());

                SFMessageEntity messageEntity = new SFMessageEntity(message);
                messageEntity.setWaitTime(msg.getTime());
                messages.add(messageEntity);
            }else {
                SFMessageEntity messageEntity = new SFMessageEntity(sfMsgCodeContact.getReMessage().plus(Message.Companion.toMessageByRainCode(msg.rainCode)));
                messageEntity.setWaitTime(msg.getTime());
                messages.add(messageEntity);
            }

        }
        return messages;
    }

    /**
     * sf码转Rain码
     * @return
     */
    public ArrayList<Msg> sfCodeToRainCode(SFMsgCodeContact codeContact, String code){
        code = codeHandle(codeContact, code);//处理标签
        //处理分割符
        ArrayList<Msg> msgList = new ArrayList<>();
        for (String str:code.split("<SF:Split>")){
            if (str.equals("")){
                continue;
            }

            Pattern pattern = Pattern.compile("<SF:Split:.+?>",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);

            int start = 0;
            boolean isFind = false;
            while (matcher.find()){
                isFind = true;
                String gr = matcher.group();
                //去除头尾
                String c = gr.substring(1, gr.length() - 1);
                String[] p = c.split(":", 3);
                if (p.length > 2){
                    String paraStr = p[2];
                    long time = 0;
                    try {
                        time = Long.decode(paraStr);
                    }catch (Exception e){
                        //
                    }
                    String sfCode = str.substring(start, matcher.start());
                    start = matcher.end();
                    String rainCode = codeHandle(codeContact, sfCode);
                    if (!rainCode.equals("")){
                        msgList.add(new Msg(rainCode, time));
                    }
                }
            }
            if (isFind){
                String rainCode = str.substring(start);
                if (!rainCode.equals("")){
                    msgList.add(new Msg(rainCode, 0));
                }
            }else {
                msgList.add(new Msg(str, 0));
            }
        }

        return msgList;
    }

    /**
     * 标签处理
     * @param code
     * @return
     */
    public String codeHandle(SFMsgCodeContact codeContact, String code){
        StringBuilder stringBuilder = new StringBuilder(code);

        Pattern pattern = Pattern.compile("<SF:[^<>]+?>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        //如果已经没有匹配
        boolean isFind = false;

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
                        isFind = true;
                    }
                }catch (DoNone doNone){
                    //doNone
                }catch (Exception e){
                    try {
                        String reCode = classMap.get(type).error(e);
                        if (reCode != null){
                            replaces.add(new Replace(matcher.start(), matcher.end(), reCode));
                            isFind = true;
                        }
                    }catch (DoNone doNone){
                        //doNone
                    }
                }
            }
        }

        if (!isFind){
            return code;
        }

        //从后向前替换
        for (int i = replaces.size() - 1; i >= 0; i--){
            Replace replace = replaces.get(i);
            stringBuilder.replace(replace.start, replace.end, replace.reCode);
        }
        return codeHandle(codeContact, stringBuilder.toString());
    }

    @Getter
    @AllArgsConstructor
    class Replace{
        int start;
        int end;
        String reCode;
    }

    @Data
    @AllArgsConstructor
    class Msg{
        String rainCode;
        long time;
    }

    @Data
    public static class SFMessageEntity{
        Message message;
        long waitTime = 0;
        long reCallTime = 0;

        public SFMessageEntity(Message message) {
            this.message = message;
        }
    }
}
