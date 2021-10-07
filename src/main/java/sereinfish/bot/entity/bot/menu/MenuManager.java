package sereinfish.bot.entity.bot.menu;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.entity.ClassManager;
import sereinfish.bot.entity.arknights.penguinStatistics.PenguinStatistics;
import sereinfish.bot.entity.bot.menu.annotation.Menu;
import sereinfish.bot.entity.bot.menu.annotation.MenuItem;
import sereinfish.bot.entity.mc.JsonColor;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.ServerListPing;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MenuManager {
    public static BufferedImage getMenuImage(Group group, Member sender, GroupConf conf){
        //判断图片是否存在
        if (FileHandle.helpMenuImageCacheFile.exists() && FileHandle.helpMenuImageCacheFile.isFile()){
            try {
                return ImageIO.read(FileHandle.helpMenuImageCacheFile);
            } catch (IOException e) {
                SfLog.getInstance().e(ClassManager.class, e);
            }
        }
        //得到所在权限组
        Integer[] permissions = Permissions.getInstance().getMemberPermissions(group, sender);
        //类扫描
        ArrayList<MenuEntity> menus = new ArrayList<>();
        for (Class cls:ClassManager.getInstance().getControllerClassList()){
            if (cls.isAnnotationPresent(Menu.class)){
                MenuEntity entity = new MenuEntity();
                entity.setMenu((Menu) cls.getAnnotation(Menu.class));
                //遍历函数
                for (Method method:cls.getDeclaredMethods()){
                    if (method.isAnnotationPresent(MenuItem.class)){
                        MenuItem menuItem = method.getAnnotation(MenuItem.class);
                        if (Permissions.getInstance().authorityCheck(permissions, menuItem.permission())){
                            entity.add(method.getAnnotation(MenuItem.class));
                        }
                    }
                }
                if (entity.getMenuItems().size() > 0){
                    menus.add(entity);
                }
            }
        }
        //生成图片
        FontDesignMetrics metrics;
        int topSpacing = 50;//上边距
        int downSpacing = 50;//下边距
        int leftSpacing = 50;//左边距
        int rightSpacing = 50;//右边距

        int textWidth = 1600;//文字最大宽度

        Color bgColor = Color.WHITE;//背景颜色
        Color textColor = Color.BLACK;//字体颜色
        BufferedImage bgImage = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);//背景图片
        int titleFontSize = 48;//主标题字体大小
        int menuFontSize = 36;//菜单标题字体大小
        int menuItemFontSize = 28;//字体大小
        Font font;//字体
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, PenguinStatistics.class.getClassLoader().getResourceAsStream("arknights/fonts/萝莉体.ttf"));
        } catch (Exception e) {
            SfLog.getInstance().e(ServerListPing.class, "默认字体加载失败：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDefaultFontFile), e);
            font = new Font("微软雅黑 Light", Font.PLAIN, menuItemFontSize);
        }
        int rowSpacing = 8;//行距
        int indentLength = 60;//缩进长度

        int menuHeight = 0;//分类行数
        int menuItemHeight = 0;//菜单行数
        for (MenuEntity menuEntity:menus){
            menuHeight++;//分类标题及权限
            /*
            分类：权限
                指令名称
                    指令格式
                    指令描述
             */
            menuItemHeight += menuEntity.getMenuItems().size() * 3;

        }
        int textY = 0;//绘制到了的文字Y轴
        int textX = 0;

        int width = textWidth + leftSpacing + rightSpacing;//宽
        int height = topSpacing //上边距
                + downSpacing //下边距
                + FontDesignMetrics.getMetrics(font.deriveFont(Font.BOLD, titleFontSize)).getHeight()//标题高度
                + FontDesignMetrics.getMetrics(font.deriveFont(Font.BOLD, menuFontSize)).getHeight() * (menuHeight + 1) //分类标题高度
                + FontDesignMetrics.getMetrics(font.deriveFont(Font.PLAIN, menuItemFontSize)).getHeight() * menuItemHeight //分类标题高度
                + rowSpacing * (menuHeight + menuItemHeight)//文字间距
                ;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);//暂定高度
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //绘制背景
        graphics2D.setColor(bgColor);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        graphics2D.setColor(textColor);
        graphics2D.setFont(font);
        //图片头
        textY += topSpacing;
        textX = leftSpacing;
        font = font.deriveFont(Font.BOLD, titleFontSize);
        textY += rowSpacing + drawString(graphics2D, textColor, font,"SereinFish Bot 指令菜单 " + MyYuQ.getVersion(), textX, textY);

        if (menus.size() == 0){
            textX = leftSpacing;
            font = font.deriveFont(Font.BOLD, menuFontSize);
            textY += rowSpacing + drawString(graphics2D,
                    textColor,
                    font,
                    "无可执行指令",
                    textX,
                    textY);

            graphics2D.dispose();
            return bufferedImage;
        }

        //遍历绘制文字
        for (MenuEntity menuEntity:menus){
            //绘制分类
            textX = leftSpacing;
            String type = "[生效范围:";
            switch (menuEntity.getMenu().type()){
                case ALL:
                    type += "群聊、私聊]";
                    break;
                case GROUP:
                    type += "群聊]";
                    break;
                case PRIVATE:
                    type += "私聊]";
                    break;
                default:
                    type += "未知]";
            }
            font = font.deriveFont(Font.BOLD, menuFontSize);
            textY += rowSpacing + drawString(graphics2D,
                    textColor,
                    font,
                    "● " + menuEntity.menu.name() + " " + type +  " " + Permissions.getInstance().getAuthorityName(menuEntity.menu.permissions()) + "：",
                    textX,
                    textY);
            //绘制指令
            font = font.deriveFont(Font.PLAIN, menuItemFontSize);
            for (int i = 0; i < menuEntity.getMenuItems().size(); i++){
                MenuItem menuItem = menuEntity.getMenuItems().get(i);
                //名称【权限】
                textX = leftSpacing + indentLength;
                textY += rowSpacing + drawString(graphics2D,textColor,font,
                        (i + 1) + "." + menuItem.name() + "：[" + Permissions.getInstance().getAuthorityName(menuItem.permission()) + "]",
                        textX,
                        textY);
                textX += indentLength;
                //格式
                textY += rowSpacing + drawString(graphics2D,Color.DARK_GRAY,font,"调用格式：" + menuItem.usage(), textX, textY);
                //描述
                textY += rowSpacing + drawString(graphics2D,textColor,font,"描述：" + menuItem.description(), textX, textY);
            }

        }
        graphics2D.dispose();
        return bufferedImage;
    }

    /**
     * 文字绘制
     * @param graphics2D
     * @param font
     * @param str
     * @param x
     * @param y
     */
    private static int drawString(Graphics2D graphics2D, Color color, Font font, String str, int x, int y){
        str = str.replace("●", "§1●§0");

        str = str.replace("[", "§c[");
        str = str.replace("]", "]§0");

        str = str.replace("{", "§9{");
        str = str.replace("}", "}§0");

        str = str.replace("@", "§9@§0");

        graphics2D.setPaint(color);
        graphics2D.setFont(font);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        int textX = x;
        for (int i = 0; i < str.length(); i++){
            String c = String.valueOf(str.charAt(i));
            if (c.equals("§")){
                i++;
                graphics2D.setPaint(JsonColor.getColor(c + str.charAt(i)));
            }else {
                graphics2D.drawString(c, textX, y + metrics.getAscent());
                textX += metrics.stringWidth(c);
            }
        }
        return metrics.getHeight();
    }

    @Getter
    @Setter
    public static class MenuEntity{
        @NonNull
        Menu menu;
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        public void add(MenuItem menuItem){
            menuItems.add(menuItem);
        }
    }
}
