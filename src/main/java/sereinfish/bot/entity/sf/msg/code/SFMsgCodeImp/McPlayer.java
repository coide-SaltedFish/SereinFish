package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import com.icecreamqaq.yuq.message.Image;
import sereinfish.bot.entity.mc.MojangAPI;
import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;

@SFMsgCodeInfo("McPlayer")
public class McPlayer implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return "[McPlayer：" + e.getMessage() + "]";
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        String id = parameter.getString(0).trim();
        String type = parameter.getString(1).trim();

        if (type.equals("id")){
            //得到账号信息
            MojangAPI.AccountInfo accountInfo = MojangAPI.getAccountInfo(id);
            if (accountInfo == null || accountInfo.getId() == null){
                throw new Exception("账号信息获取失败：null");
            }

            return accountInfo.getId();
        }

        if (type.equals("names")){
            //得到账号信息
            MojangAPI.AccountInfo accountInfo = MojangAPI.getAccountInfo(id);
            if (accountInfo == null || accountInfo.getId() == null){
                throw new Exception("账号信息获取失败：null");
            }
            //得到用过的名称
            MojangAPI.NameInfo[] nameInfos = MojangAPI.getNamesInfo(accountInfo.getId());
            String reStr = "曾用名列表：";

            int num = 1;
            for (MojangAPI.NameInfo nameInfo:nameInfos){

                if(nameInfo.getChangedToAt() != 0){
                    reStr += "\n" + num + "." + nameInfo.getName();
                    reStr += "\n修改时间：\n" + Time.dateToString(nameInfo.getChangedToAt(), Time.LOG_TIME_);
                    num ++;
                }else {
                    reStr += "\n" + nameInfo.getName();
                }
            }

            return reStr;
        }

        if (type.equals("head")){
            //得到账号信息
            MojangAPI.AccountInfo accountInfo = MojangAPI.getAccountInfo(id);

            if (accountInfo == null || accountInfo.getId() == null){
                throw new Exception("账号信息获取失败：null");
            }
            //得到皮肤信息

            Image image =  MyYuQ.uploadImage(codeContact.getSource(), NetHandle.getMcPlayerHeadImageFile(accountInfo.getId(), 128));
            return "<Rain:Image:" + image.getId() + ">";
        }

        if (type.equals("skin")){
            //得到账号信息
            MojangAPI.AccountInfo accountInfo = MojangAPI.getAccountInfo(id);
            if (accountInfo == null || accountInfo.getId() == null){
                throw new Exception("账号信息获取失败：null");
            }
            //得到皮肤信息

            Image image = MyYuQ.uploadImage(codeContact.getSource(), NetHandle.getMcPlayerSkinImageFile(accountInfo.getId(), 128));
            return "<Rain:Image:" + image.getId() + ">";
        }


        throw new Exception("未知类型：" + type);
    }
}
