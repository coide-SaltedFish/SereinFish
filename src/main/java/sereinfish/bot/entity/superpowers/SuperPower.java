package sereinfish.bot.entity.superpowers;

import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SuperPower {
    private ArrayList<Effect> effects = new ArrayList<>();
    private ArrayList<DeBuff> deBuffs = new ArrayList<>();
    private ArrayList<Long> blackList = new ArrayList<>();

    public boolean isBlackList(long qq){
        return blackList.contains(qq);
    }

    public String msg(Member member){
        if (effects.size() == 0 || deBuffs.size() == 0){
            return "还没有添加任何超能力或超能力代价哦";
        }

        String effect = effects.get(MyYuQ.getRandom(0, effects.size() - 1)).getEffect();
        String deBuff = deBuffs.get(MyYuQ.getRandom(0, deBuffs.size() - 1)).getDeBuff();

        return member.nameCardOrName()
                + "获得了" + effect + "的能力~"
                + "但是使用后的代价是" + deBuff;
    }

    public String msg(Contact contact){
        String effect = effects.get(MyYuQ.getRandom(0, effects.size() - 1)).getEffect();
        String deBuff = deBuffs.get(MyYuQ.getRandom(0, deBuffs.size() - 1)).getDeBuff();

        return contact.getName()
                + "获得了" + effect + "的能力~"
                + "但是使用后的代价是" + deBuff;
    }

    public String addEffect(Group group, Member sender, String effect){
        if (isExistEffect(effect)){
            return "已存在";
        }

        effects.add(new Effect(System.currentTimeMillis(), group.getId(), sender.getId(), effect));
        save();

        return "添加成功";
    }

    public String addDeBuff(Group group, Member sender, String deBuff){
        if (isExistDeBuff(deBuff)){
            return "已存在";
        }

        deBuffs.add(new DeBuff(System.currentTimeMillis(), group.getId(), sender.getId(), deBuff));
        save();

        return "添加成功";
    }

    private boolean isExistEffect(String effect){
        for (Effect effect1:effects){
            if (effect1.getEffect().equalsIgnoreCase(effect)){
                return true;
            }
        }
        return false;
    }

    private boolean isExistDeBuff(String deBuff){
        for (DeBuff d:deBuffs){
            if (d.getDeBuff().equalsIgnoreCase(deBuff)){
                return true;
            }
        }
        return false;
    }

    /**
     * 保存
     */
    public void save(){
        File confFile = new File(FileHandle.configPath, "superPower.json");
        try {
            FileHandle.write(confFile, MyYuQ.toJson(this, SuperPower.class));
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "超能力配置保存失败");
        }
    }

    /**
     * 得到配置
     * @param
     * @return
     */
    public static SuperPower read(){
        File confFile = new File(FileHandle.configPath, "superPower.json");
        if (confFile.exists() && confFile.isFile()){
            try {
                String fileStr = FileHandle.read(confFile);
                SuperPower superPower = MyYuQ.toClass(fileStr, SuperPower.class);
                return superPower;
            } catch (IOException e) {
                SfLog.getInstance().e(SuperPower.class, e);
                return null;
            }
        }
        SuperPower superPower = new SuperPower();
        superPower.save();
        return superPower;
    }

    @Data
    @AllArgsConstructor
    public class Effect{
        long time;
        long fromGroup;
        long fromQQ;
        String effect;
    }

    @Data
    @AllArgsConstructor
    public class DeBuff{
        long time;
        long fromGroup;
        long fromQQ;
        String deBuff;
    }
}
