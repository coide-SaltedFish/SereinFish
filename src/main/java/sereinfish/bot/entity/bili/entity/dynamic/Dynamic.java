package sereinfish.bot.entity.bili.entity.dynamic;

import lombok.Getter;
import sereinfish.bot.myYuq.MyYuQ;

/**
 * 动态类
 */
@Getter
public class Dynamic {
    int code;
    String msg;
    String message;
    Data data;

    public int getCardLen(){
        return data.getCards().length;
    }

    @Getter
    public class Data{
        int has_more;
        long next_offset;
        int _gt_;
        Card[] cards;


        @Getter
        public class Card{
            public static final int TYPE_EXTEND = 1;//转发动态
            public static final int TYPE_IMAGE_TEXT = 2;//图文动态
            public static final int TYPE_TEXT = 4;//文字动态
            public static final int TYPE_VIDEO = 8;//视频动态

            Desc desc;
            String card;
            Extra extra;


            /**
             * 是否是转发的动态
             * @return
             */
            public boolean isExtend(){
                return desc.getType() == TYPE_EXTEND;
            }

            /**
             * 是否空间置顶动态
             */
            public boolean isSpaceTop(){
                return extra.getIs_space_top() == 1;
            }

            @Getter
            public class Extra{
                int is_space_top;
            }

            @Getter
            public class Desc{
                int type;
                long rid;
                long dynamic_id;
                long timestamp;
                String card;
                UserProfile user_profile;

                @Getter
                public class UserProfile{
                    DynamicCard.User info;
                }
            }

            /**
             * 得到Up名称
             * @return
             */
            public String getUserName(){
                if (desc.getUser_profile().getInfo().getName() != null && !desc.getUser_profile().getInfo().getName().equals("")){
                    return desc.getUser_profile().getInfo().getName();
                }

                DynamicCard dynamicCard = getDynamicCard();
                return dynamicCard.getUserName();
            }

            public DynamicCard getDynamicCard(){
                return MyYuQ.toClass(card, DynamicCard.class);
            }

            public DynamicVideo getDynamicVideo(){
                return MyYuQ.toClass(card, DynamicVideo.class);
            }
        }
    }

    /**
     * 得到动态
     * @param i
     * @return
     */
    public Data.Card getCard(int i){
        if (i >= 0 && i < data.getCards().length){
            return data.getCards()[i];
        }
        return null;
    }
}
