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
@Table(name = "whitelist")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DB("user")
public class WhiteList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private long qq;//qq号

    @Column
    private String main_uuid;//大号uuid

    @Column
    private Timestamp main_add_time;//大号添加时间

    @Column
    private String alt_uuid;//小号uuid

    @Column
    private Timestamp alt_add_time;//小号添加时间
}
