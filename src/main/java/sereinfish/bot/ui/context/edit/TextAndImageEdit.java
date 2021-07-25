package sereinfish.bot.ui.context.edit;

import sereinfish.bot.myYuq.MyYuQ;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 图文编辑框
 */
public class TextAndImageEdit extends JTextPane {

    private static final Map<Integer, String> flagMap = new LinkedHashMap<>();//标签列表
    private static final int FLAG_TEXT = 0;
    private static final int FLAG_IMAGE = 1;

    public TextAndImageEdit(){
        super();
        initFlag();
    }

    /**
     * 初始化标签列表
     */
    private void initFlag(){
        flagMap.put(FLAG_IMAGE, MyYuQ.FLAG_IMAGE);
    }

    /**
     * 重写绘制方法
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * 根据Rain码解析图片
     */
    private void rainImageDraw(){

    }
}
