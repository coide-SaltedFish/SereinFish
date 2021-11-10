package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import lombok.Data;

import javax.persistence.*;

/**
 * 黑名单
 */
@Data
@Table(name = "sf_blacklist")
@Entity
@DB("user")
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private long time = System.currentTimeMillis();

    @Column(nullable = false)
    private long qq;

    @Column(nullable = false)
    private long source;

    @Column(nullable = false, length = 3000)
    private String remarks = "无";//备注

    public BlackList() {
    }

    public BlackList(long qq, long source) {
        this.qq = qq;
        this.source = source;
    }

    public BlackList(long qq, long source, String remarks) {
        this.qq = qq;
        this.source = source;
        this.remarks = remarks;
    }
}
