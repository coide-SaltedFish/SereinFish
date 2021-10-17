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

    @Getter
    public class Data{
        int has_more;
        long next_offset;
        int _gt_;
        Card[] cards;


        @Getter
        public class Card{
            Desc desc;
            String card;

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
