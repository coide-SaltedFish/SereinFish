package sereinfish.bot.entity.jsonEx;

public class JsonMsg {
    /**
     * 链接卡片
     * @param title     标题
     * @param desc      显示的文字内容
     * @param preview   预览图
     * @param jumpUrl   跳转链接
     * @return
     */
    public static String getUrlCard(String title,String desc,String preview,String jumpUrl){
        String msg = "{\"app\":\"com.tencent.structmsg\",\"desc\":\"新闻\",\"view\":\"news\",\"ver\":\"0.0.0.1\",\"prompt\":\"\",\"meta\":{\"news\":{\"title\":\"" + title
                + "\",\"desc\":\"" + desc + "\",\"preview\":\"" + preview
                + "\",\"tag\":\"SereinFish Bot\",\"jumpUrl\":\"" + jumpUrl +
                "\",\"appid\":100446242,\"app_type\":1,\"action\":\"\",\"source_url\":\"\",\"source_icon\":\"\",\"android_pkg_name\":\"\"}}}";

        return msg;
    }

    /**
     * 菜单卡片，选项不可点击
     * @param prompt    预览消息
     * @param name      顶部名称
     * @param iconUrl   图片链接
     * @param buttons   按钮名称
     * @return
     */
    public static String getMenuCardNoAction(String prompt,String name,String iconUrl,String[] buttons){
        String msg = "{\"app\":\"com.tencent.miniapp\",\"desc\":\"\",\"view\":\"notification\",\"ver\":\"0.0.0.1\",\"prompt\":\"" + prompt +
                "\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"sourceUrl\":\"\",\"meta\":{\"notification\":{\"appInfo\":{\"appName\":\"" + name +
                "\",\"appType\":4,\"ext\":\"\",\"img\":\"https:\\/\\/url.cn\\/5ANgkzI\",\"img_s\":\"\",\"appid\":1108249016,\"iconUrl\":\"" + iconUrl +
                "\"},\"button\":[";

        for (int i = 0; i < buttons.length; i++){
            if (i != 0){
                msg += ",";
            }
            msg += "{\"action\":\"\",\"name\":\"" + buttons[i] + "\"}";
        }
        msg += "],\"emphasis_keyword\":\"\"}},\"text\":\"\",\"sourceAd\":\"\"}";
        return msg;
    }

    public static String getNoticeList(String prompt, String title, String iconUrl, String[][] data){
        String d = "";
        boolean flag = false;
        for (String[] da:data){
            if (flag){
                d += ",{\"title\":\"" + da[0] + "\",\"value\":\"" + da[1] + "\"}";
            }else {
                flag = true;
                d += "{\"title\":\"" + da[0] + "\",\"value\":\"" + da[1] + "\"}";
            }
        }

        String msg = "{\"app\":\"com.tencent.miniapp\",\"desc\":\"\",\"view\":\"notification\",\"ver\":\"0.0.0.1\"," +
                "\"prompt\":\"" + prompt + "\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\"," +
                "\"sourceUrl\":\"\",\"meta\":{\"notification\":{\"appInfo\":{\"appName\":\"" + title + "\",\"appType\":4," +
                "\"appid\":3220014955,\"iconUrl\":\"" + iconUrl + "\"}," +
                "\"data\":[" + d + "]," +
                "\"emphasis_keyword\":\"\"}},\"text\":\"\",\"sourceAd\":\"\",\"extra\":\"\"}";

        return msg;
    }
}
