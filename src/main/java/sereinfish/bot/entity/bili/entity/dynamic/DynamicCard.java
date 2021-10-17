package sereinfish.bot.entity.bili.entity.dynamic;

import lombok.Getter;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 一个动态
 */
@Getter
public class DynamicCard {
    User user;
    Item item;
    String origin;
    String origin_extend_json;

    @Getter
    public class User{
        long uid;
        String uname;
        String face;
        String head_url;
        String name;
    }

    @Getter
    public class Item{
        long id;
        long rp_id;
        long uid;
        String content;
        String ctrl;
        long timestamp;
        String description;//动态文字
        Picture[] pictures;//动态图片
        String title;
        int pictures_count;
        int reply;
        long upload_time;

        @Getter
        public class Picture{
            int img_height;
            double img_size;
            String img_src;
            int img_width;
        }
    }

    @Getter
    public class OriginUser{
        User info;
    }

    /**
     * 得到Up名称
     * @return
     */
    public String getUserName(){
        if (user == null){
            return "未知";
        }

        if (user.getName() == null || user.getName().equals("")){
            return user.getUname();
        }
        return user.getName();
    }

    /**
     * 是否是转发的动态
     * @return
     */
    public boolean isExtend(){
        return origin != null && !origin.equals("");
    }

    public DynamicCard getOrigin(){
        DynamicCard dynamicCard = MyYuQ.toClass(origin, DynamicCard.class);
        return dynamicCard;
    }

    public String getDescription(){
        if (item == null){
            return "";
        }

        if (item.getDescription() == null){
            return item.getContent();
        }
        return item.getDescription();
    }

    public Item.Picture[] getPictures(){
        if (item == null || item.getPictures() == null){
            return new Item.Picture[]{};
        }
        return item.getPictures();
    }
}
