package sereinfish.bot.database.entity;

import com.icecreamqaq.yudb.annotation.DB;
import com.icecreamqaq.yuq.message.Message;
import lombok.Data;

import javax.persistence.*;

/**
 * 群历史消息数据表
 */
@Data
@Table(name = "history_group_msg")
@Entity
@DB
public class GroupHistoryMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private long time = System.currentTimeMillis();//发送时间

    @Column(nullable = false)
    private long source;//来自群

    @Column(nullable = false)
    private long qq;//来自qq

    @Column(nullable = false)
    private int mid;

    @Column(nullable = false, length = Integer.MAX_VALUE)
    private String rainCodeMsg;//消息内容

    public GroupHistoryMsg() {
    }

    public GroupHistoryMsg(long source, long qq, int mid, String rainCodeMsg) {
        this.source = source;
        this.qq = qq;
        this.mid = mid;
        this.rainCodeMsg = rainCodeMsg;
    }

    public Message getMessage(){
        return Message.Companion.toMessageByRainCode(rainCodeMsg);
    }
}
