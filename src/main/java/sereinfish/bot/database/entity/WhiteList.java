package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 白名单
 */
@Data
@Table(name = "redbot_minecraft_players")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DB("user")
public class WhiteList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private long group;//qq号

    @Column
    private long qq;//qq号

    @Column
    private long joinTimestamp;

    @Column
    private long leaveTimestamp;

    @Column(length = 40)
    private String uuid1;//大号uuid

    @Column
    private Timestamp uuid1AddedTime;//大号添加时间

    @Column
    private String uuid2;//小号uuid

    @Column(length = 40)
    private Timestamp uuid2AddedTime;//小号添加时间

    @Column
    private boolean blocked;

    @Column(length = 1000)
    private String blockReason;
}
