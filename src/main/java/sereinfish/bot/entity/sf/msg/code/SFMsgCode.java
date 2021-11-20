package sereinfish.bot.entity.sf.msg.code;

import sereinfish.bot.entity.sf.msg.code.entity.Parameter;

public interface SFMsgCode {
    String error(Exception e);

    String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception;
}
