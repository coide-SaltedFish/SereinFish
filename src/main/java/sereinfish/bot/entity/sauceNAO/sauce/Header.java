package sereinfish.bot.entity.sauceNAO.sauce;

import lombok.Getter;

import java.util.Map;

/**
 * nao返回的json的header
 */
@Getter
public class Header {
    String user_id;
    String account_type;
    String short_limit;
    String long_limit;
    int long_remaining;
    int short_remaining;// 短时剩余
    int status;
    String message;
    int results_requested;

    Map<String,Index> index;

    String search_depth;
    float minimum_similarity;
    String query_image_display;
    String query_image;
    int results_returned;

    /**
     * index项
     */
    public class Index{
        int status;
        int parent_id;
        int id;
        int results;
    }
}
