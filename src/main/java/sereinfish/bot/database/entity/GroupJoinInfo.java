package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

/**
 * 加群的记录
 */
@Data
@Table(name = "group_join_info")
@Entity
@DB
public class GroupJoinInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    long time = System.currentTimeMillis();

    @Column(nullable = false)
    long source = 0;

    @Column(nullable = false)
    long qq = 0;

    @Column(nullable = false)
    String joinMessage = "";

    @Column(nullable = false)
    boolean isInvitation = false;

    @Column(nullable = false)
    boolean isAccept = false;

    public GroupJoinInfo() {
    }

    public GroupJoinInfo(long source, long qq, String joinMessage) {
        this.source = source;
        this.qq = qq;
        this.joinMessage = joinMessage;
    }
}
