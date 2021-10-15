package sereinfish.bot.entity.pixiv.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Illust {
    private long id;
    private String title;
    private String caption;
    private String type;
    private User user;
    private Image_urls image_urls;
    private int restrict;
    private Tag tags[];
    private String create_date;
    private MetaSinglePage meta_single_page;
    //代理链接
    private String proxyUrl = "";
    private int x_restrict;
    private MetaPages meta_pages[];

    /**
     * 获取代理链接
     * @return
     */
    public String getProxyUrl(){
        if (proxyUrl == null || proxyUrl.equals("")){
            if (meta_single_page != null && meta_single_page.original_image_url != null){
                proxyUrl = meta_single_page.original_image_url.replace("i.pximg.net", "i.pixiv.cat");
            }else {
                if (meta_pages != null && meta_pages.length > 0 && meta_pages[0].image_urls != null){
                    proxyUrl = meta_pages[0].image_urls.original.replace("i.pximg.net", "i.pixiv.cat");
                }
            }
        }
        return proxyUrl;
    }

    /**
     * 获取图片数量
     * @return
     */
    public int getPageMax(){
        if (meta_pages != null && meta_pages.length != 0){
            return meta_pages.length;
        }
        return 1;
    }

    /**
     * 获取指定页的代理链接
     * @param num
     * @return
     */
    public String getProxyUrl(int num){
        if (meta_pages != null && meta_pages.length > num){
            proxyUrl = meta_pages[num].image_urls.original.replace("i.pximg.net", "i.pixiv.cat");
        }else {
            return getProxyUrl();
        }
        return proxyUrl;
    }

    public boolean isR18(){
        if (x_restrict == 0){
            return false;
        }else {
            return true;
        }
    }

    public boolean isR18G(){
        return x_restrict == 2;
    }



    @Getter
    public class MetaSinglePage{
        private String original_image_url;
    }

    @Getter
    public class User{
        private long id;
        private String name;
        private String account;
        private ProfileImageUrls profile_image_urls;

        @Getter
        public class ProfileImageUrls{
            private String medium;
        }
    }

    @Getter
    public class MetaPages{
        Image_urls image_urls;

        @Getter
        public class Image_urls{
            private String square_medium;
            private String medium;
            private String large;
            private String original;
        }
    }

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
