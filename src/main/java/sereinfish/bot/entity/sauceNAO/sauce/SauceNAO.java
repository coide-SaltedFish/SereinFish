package sereinfish.bot.entity.sauceNAO.sauce;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class SauceNAO {
    Header header;
    ArrayList<Result> results;

    @Override
    public String toString() {
        if (header.getStatus() < 0){
            return "失败：" + header.getStatus() +
                    "\n" + header.getMessage();
        }

        if (header.getStatus() > 0){
            return "服务器错误：" + header.getStatus() +
                    "\n" + header.getMessage();
        }

        if (results == null){
            return "结果为空";
        }

        String s = "结果如下：";
        for (Result result:results){
            s += "\n" + result.toString();
        }
        return s;
    }
}
