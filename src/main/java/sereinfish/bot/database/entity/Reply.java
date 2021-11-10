package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import lombok.Data;
import sereinfish.bot.myYuq.MyYuQ;

import javax.persistence.*;

/**
 * 自动回复表格
 */
@Data
@Table(name = "sf_reply")
@Entity
@DB("user")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private long time = System.currentTimeMillis();

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private long qq = -1;

    @Column(nullable = false)
    private long source = -1;

    @Column(nullable = false, length = 3000)
    private String reKey;

    @Column(nullable = false, length = 3000)
    private String reply;

    public Reply() {
    }

    public Reply(long qq, long source, String reKey, String reply) {
        this.qq = qq;
        this.source = source;
        this.reKey = reKey;
        this.reply = reply;

        uuid = MyYuQ.stringToMD5(reKey + "$$" + reply);
    }
}
