package sereinfish.bot.entity.pixiv.entity;

import lombok.Getter;

@Getter
public class Illust {
    private long id;
    private String title;
    private String type;
    private Image_urls image_urls;
    private int restrict;
    private Tag tags[];


    @Getter
    public class Image_urls{
        private String square_medium;
        private String medium;
        private String large;
    }

    @Getter
    public class Tag{
        private String name;
        private String translated_name;
    }
}
