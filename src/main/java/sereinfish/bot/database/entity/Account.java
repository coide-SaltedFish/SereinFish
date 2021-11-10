package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Table(name = "xiaomi_motion_account")
@Entity
@DB
public class Account {

    public class Type{
        public static final int XIAOMI_MOTION = 0;//小米运动
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    int type;

    @Column(nullable = false)
    long time = System.currentTimeMillis();

    @Column(nullable = false)
    long qq;

    @Column(nullable = false, length = 2000)
    String account;

    @Column(nullable = false, length = 2000)
    String password;

    @Column(nullable = false, length = 2000)
    String token;

    public Account() {
    }

    public Account(int type, long qq, String account, String password, String token) {
        this.type = type;
        this.qq = qq;
        this.account = account;
        this.password = password;
        this.token = token;
    }

    @Override
    public String toString(){
        return "[类型：" + type +
                ",时间：" + time +
                ",QQ：" + qq +
                ",账号：" + account +
                ",密码：" + password + "]";
    }
}
