package sereinfish.bot.entity.arknights.penguinStatistics;

import lombok.Getter;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.myYuq.time.Time;
import sereinfish.bot.net.mc.ServerListPing;
import sereinfish.bot.utils.OkHttpUtils;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 企鹅物流数据管理
 */
public class PenguinStatistics {
    private Item[] items;//物品列表

    private static PenguinStatistics penguinStatistics;

    private PenguinStatistics() throws IOException {
        //初始化物品列表
        String itemStr = OkHttpUtils.getStr("https://penguin-stats.io/PenguinStats/api/v2/items");
        items = MyYuQ.toClass(itemStr, Item[].class);
    }

    public static PenguinStatistics getInstance() throws IOException {
        if (penguinStatistics == null) {
            penguinStatistics = new PenguinStatistics();
        }
        return penguinStatistics;
    }

    /**
     * 得到物品itemId
     *
     * @param name
     * @return
     */
    public String getItemId(String name) {
        for (Item item : items) {
            //官方名称匹配
            if (name.equals(item.getName_i18n().getZh())) {
                return item.getItemId();
            } else if (name.equals(item.getName_i18n().getEn())) {
                return item.getItemId();
            } else if (name.equals(item.getName_i18n().getJa())) {
                return item.getItemId();
            } else if (name.equals(item.getName_i18n().getKo())) {
                return item.getItemId();
            }

            //别名匹配
            if (item.getAlias() != null && item.getAlias().getZh() != null) {
                for (String alias : item.getAlias().getZh()) {
                    if (alias.equals(name)) {
                        return item.getItemId();
                    }
                }
            }

            if (item.getAlias() != null && item.getAlias().getJa() != null) {
                for (String alias : item.getAlias().getJa()) {
                    if (alias.equals(name)) {
                        return item.getItemId();
                    }
                }
            }

            //详细名称匹配
            if (item.getPron() != null && item.getPron().getZh() != null) {
                for (String pron : item.getPron().getZh()) {
                    if (pron.equals(name)) {
                        return item.getItemId();
                    }
                }
            }

            if (item.getPron() != null && item.getPron().getJa() != null) {
                for (String pron : item.getPron().getJa()) {
                    if (pron.equals(name)) {
                        return item.getItemId();
                    }
                }
            }
        }

        return "";
    }

    /**
     * 向企鹅物流查询信息
     *
     * @param itemId
     * @return
     */
    public PenguinWidgetData getPenguinWidgetData(String itemId) throws IOException {
        //获取页面信息并剪切出json
        String data = OkHttpUtils.getStr("https://widget.penguin-stats.io/result/CN/item/" + itemId);
        if (data.indexOf("<script type=\"application/json\" id=\"penguinWidgetData\">") == -1) {
            return null;
        }
        if (data.indexOf("</script>") == -1) {
            return null;
        }
        String startStr = "<script type=\"application/json\" id=\"penguinWidgetData\">";
        String json = data.substring(data.indexOf(startStr) + startStr.length());
        json = json.substring(0, json.indexOf("</script>"));
        return MyYuQ.toClass(json, PenguinWidgetData.class);
    }

    /**
     * 生成结果图片
     *
     * @param data
     * @return
     */
    public BufferedImage getDataImage(PenguinWidgetData data) {
        int y = 0;//当前绘制到的y坐标

        Color bgColor = Color.WHITE;//背景
        float bgColorAlp = 0.8f;//透明度

        Color separateLineColor = Color.LIGHT_GRAY;//分割线颜色

        int width = 1600;//宽度

        int titleHeight = 100;//标题高度
        int namesHeight = 65;//标题栏高度
        int lineHeight = 60;//每一行数据高度
        int separateLineHeight = 1;//分割线高度

        int height = titleHeight //标题
                + separateLineHeight
                + namesHeight   //标题栏
                + separateLineHeight
                + data.getMatrix().length * lineHeight //高度
                + separateLineHeight * data.getMatrix().length;
        //最小高度1080
        if (height < 900) {
            height = 900;
        }

        Font font;//字体
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, PenguinStatistics.class.getClassLoader().getResourceAsStream("arknights/fonts/萝莉体.ttf"));
        } catch (Exception e) {
            SfLog.getInstance().e(ServerListPing.class, "默认字体加载失败：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDefaultFontFile), e);
            font = new Font("微软雅黑 Light", Font.PLAIN, 24);
        }

        Font stagesCodeFont = new Font("微软雅黑 Light", Font.PLAIN, 16);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);//
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        //绘制底图
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        BufferedImage bgImage;//获取背景图片干员
        String bgImagePath = "arknights/" + MyYuQ.getRandom(1, 5) + ".png";
        try {
            bgImage = ImageIO.read(getClass().getClassLoader().getResource(bgImagePath));
            graphics2D.drawImage(bgImage, width - bgImage.getWidth(), height - bgImage.getHeight(), null);//绘制干员图片
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "资源读取错误：" + bgImagePath);
        }
        //绘制底层颜色
        graphics2D.setColor(bgColor);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, bgColorAlp));//半透明颜色覆盖
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.setColor(Color.WHITE);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));

        //绘制标题
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ;//抗锯齿
        graphics2D.setColor(Color.BLACK);
        font = font.deriveFont(Font.BOLD, 36);
        graphics2D.setFont(font);
        String title = "";
        for (int i = 0; i < data.getItems().length; i++) {
            Item item = data.getItems()[i];
            if (i != 0) {
                title += "、";
            }
            title += item.getName_i18n().getZh();
        }
        title += " 统计结果";
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        graphics2D.drawString(title, 5, y + 5 + metrics.getAscent());

        y += 5 + metrics.getAscent();
        font = font.deriveFont(Font.PLAIN, 18);
        graphics2D.setFont(font);
        graphics2D.drawString("数据来源：企鹅物流（penguin-stats.io）", 5, y + 10 + FontDesignMetrics.getMetrics(font).getAscent());
        y += 10 + FontDesignMetrics.getMetrics(font).getAscent();

        graphics2D.setFont(stagesCodeFont);
        metrics = FontDesignMetrics.getMetrics(stagesCodeFont);
        graphics2D.drawString("Image by SereinFish", 5, y + 5 + metrics.getAscent());
        graphics2D.setFont(font);

        //绘制分割线
        y = titleHeight;
        graphics2D.setColor(separateLineColor);
        graphics2D.fillRect(0, y, bufferedImage.getWidth(), separateLineHeight);

        //绘制分类栏
        graphics2D.setColor(Color.BLACK);
        font = font.deriveFont(42);
        graphics2D.setFont(font);
        metrics = FontDesignMetrics.getMetrics(font);
        String titleNames[] = {"作战", "掉落数", "样本数", "百分比", "理智", "最短通关用时", "统计区间"};
        int titleNameWidth = width / titleNames.length;
        for (int i = 0; i < titleNames.length; i++) {
            graphics2D.drawString(titleNames[i], 5 + i * titleNameWidth, y + (namesHeight + metrics.getAscent()) / 2);
        }

        //绘制分割线
        y += namesHeight;
        graphics2D.setColor(separateLineColor);
        graphics2D.fillRect(0, y, bufferedImage.getWidth(), separateLineHeight);

        y += 10;
        //绘制数据
        metrics = FontDesignMetrics.getMetrics(font);
        for (int i = 0; i < data.getMatrix(penguinStatistics).length; i++) {
            PenguinWidgetData.Matrix matrix = data.getMatrix()[i];
            graphics2D.setColor(Color.BLACK);

            PenguinWidgetData.Stages stages = getStages(data, matrix.getStageId());
            PenguinWidgetData.Zone zone = getZone(data, getStages(data, matrix.getStageId()).getZoneId());
            //作战
            graphics2D.drawString(zone.getZoneName_i18n().getZh(), 5, 5 + y + metrics.getAscent());

            graphics2D.setFont(stagesCodeFont);
            FontDesignMetrics stagesCodeMetrics = FontDesignMetrics.getMetrics(stagesCodeFont);
            graphics2D.drawString(stages.getCode_i18n().getZh(), 5, 12 + y + stagesCodeMetrics.getAscent() * 2);
            graphics2D.setFont(font);

            int lineY = y + (lineHeight + metrics.getAscent()) / 2;

            //掉落数
            graphics2D.drawString(matrix.getQuantity() + "", 5 + titleNameWidth, lineY);

            //样本数
            graphics2D.drawString(matrix.getTimes() + "", 5 + titleNameWidth * 2, lineY);

            //百分比
            graphics2D.drawString(((float) matrix.getQuantity() / matrix.getTimes() * 100) + "%", 5 + titleNameWidth * 3, lineY);

            //单件期望理智
            graphics2D.setColor(Color.RED);
            graphics2D.drawString(stages.getApCost() + "", 5 + titleNameWidth * 4, lineY);
            graphics2D.setColor(Color.BLACK);

            //最短通关用时
            graphics2D.drawString(getminClearTime(stages.getMinClearTime()), 5 + titleNameWidth * 5, lineY);

            //统计区间
            String timeRange = Time.dateToString(matrix.getStart(), "yy.MM.dd");
            if (matrix.getEnd() != 0) {
                timeRange += " 至 " + Time.dateToString(matrix.getEnd(), "yy.MM.dd");
            } else {
                timeRange += " 至今";
            }
            graphics2D.drawString(timeRange, 5 + titleNameWidth * 6, lineY);

            y += lineHeight;
            graphics2D.setColor(separateLineColor);
            graphics2D.fillRect(0, y, bufferedImage.getWidth(), separateLineHeight);
        }

        graphics2D.dispose();
        return bufferedImage;
    }


    public String getminClearTime(long time) {
        long min = time % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) / (1000 * 60);
        long s = time % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) % (1000 * 60) / 1000;

        return min + "分" + s + "秒";
    }

    /**
     * 得到作战地图
     *
     * @param zoneId
     * @return
     */
    private PenguinWidgetData.Zone getZone(PenguinWidgetData data, String zoneId) {
        for (PenguinWidgetData.Zone zone : data.getZones()) {
            if (zone.getZoneId().equals(zoneId)) {
                return zone;
            }
        }
        return null;
    }

    /**
     * 得到作战地图
     *
     * @param stageId
     * @return
     */
    private PenguinWidgetData.Stages getStages(PenguinWidgetData data, String stageId) {
        for (PenguinWidgetData.Stages stages : data.getStages()) {
            if (stages.getStageId().equals(stageId)) {
                return stages;
            }
        }
        return null;
    }

    /**
     * 方舟查询返回数据
     */
    @Getter
    public class PenguinWidgetData {
        private Request request;
        private Query query;
        private Item[] items;
        private Matrix[] matrix;
        private Stages[] stages;
        private Zone[] zones;
        private Error error;

        public Matrix[] getMatrix(PenguinStatistics penguinStatistics) {
            if (matrix == null){
                return new Matrix[]{};
            }

            for (int i = 0; i < matrix.length - 1; i++) {
                for (int j = 0; j < matrix.length - 1 - i; j++) {
                    float dl_1 = ((float)  matrix[j].getQuantity() /  matrix[j].getTimes() * 100);
                    float dl_2 = ((float)  matrix[j + 1].getQuantity() /  matrix[j + 1].getTimes() * 100);
                    if (dl_1 < dl_2) {
                        Matrix temp = matrix[j];
                        matrix[j] = matrix[j + 1];
                        matrix[j + 1] = temp;
                    }
                }
            }

            return matrix;
        }

        public Matrix[] getMatrix() {
            if(matrix == null){
                return new Matrix[]{};
            }
            return matrix;
        }

        public Stages[] getStages() {
            if (stages == null){
                return new Stages[]{};
            }
            return stages;
        }

        public Item[] getItems() {
            if (items == null){
                return new Item[]{};
            }
            return items;
        }

        @Getter
        public class Error{
            String type;
            String details;
        }

        @Getter
        class Request {
            private String mirror;
        }

        @Getter
        class Query {
            private String itemId;
            private String server;
        }

        /**
         * 矩阵模型
         */
        @Getter
        public class Matrix {
            private String stageId;//关卡id
            private String itemId;//物品Id
            private int quantity;//掉落数
            private int times;//样本数
            private long start = 0;//开始统计时间
            private long end = 0;//结束统计时间
        }

        @Getter
        public class Stages {
            private String zoneId;//区域Id
            private String stageId;//阶段Id
            private Code_i18n code_i18n;//关卡代码
            private int apCost;//理智
            private long minClearTime;//最小清理时间

            @Getter
            class Code_i18n {
                private String ko;
                private String ja;
                private String en;
                private String zh;
            }
        }

        @Getter
        public class Zone {
            private String zoneId;
            private String type;
            private ZoneName_i18n zoneName_i18n;

            @Getter
            class ZoneName_i18n {
                private String ko;
                private String ja;
                private String en;
                private String zh;
            }
        }
    }

    /**
     * 方舟物品数据
     */
    @Getter
    class Item {
        private String itemId;
        private String name;
        private int sortId;
        private int rarity;//稀有
        private Existence existence;
        private String itemType;
        private int addTimePoint;
        private int[] spriteCoord;
        private String groupID;
        private Name name_i18n;//官方名称
        private Alias alias;
        private Pron pron;


        //区服
        @Getter
        class Existence {
            private Server US;
            private Server JP;
            private Server KR;
            private Server CN;

            @Getter
            class Server {
                boolean exist;
            }
        }

        //名称
        @Getter
        class Name {
            String ko;
            String ja;
            String en;
            String zh;
        }

        //别名
        @Getter
        class Alias {
            String[] ja;
            String[] zh;
        }

        //详细名称
        @Getter
        class Pron {
            String[] ja;
            String[] zh;
        }
    }
}
