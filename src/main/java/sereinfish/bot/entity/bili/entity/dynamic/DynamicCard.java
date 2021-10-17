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
    OriginUser origin_user;

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
        String at_control;
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
        int orig_type;

        public AtControl[] getAtControl(){
            return MyYuQ.toClass(at_control, AtControl[].class);
        }

        @Getter
        public class AtControl{
            String data;
            int length;
            int location;
            int type;
        }

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

    public DynamicCard getOrigin(){
        DynamicCard dynamicCard = MyYuQ.toClass(origin, DynamicCard.class);
        return dynamicCard;
    }

    public DynamicVideo getVideoOrigin(){
        DynamicVideo dynamicVideo = MyYuQ.toClass(origin, DynamicVideo.class);
        return dynamicVideo;
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
