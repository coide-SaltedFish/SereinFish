package sereinfish.bot.entity.sf.msg.code;

public interface SFMsgCode {
    String error(Exception e);

    String code(SFMsgCodeContact codeContact) throws Exception;
}
