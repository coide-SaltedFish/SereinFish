package sereinfish.bot.ui.panel;

import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.context.BasicContext;
import sereinfish.bot.ui.context.edit.SqlEdit;
import sereinfish.bot.ui.context.edit.TextAndImageEdit;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyEditorPanel extends JPanel {
    public static final int MODE_IMAGE = 0;//图文模式
    public static final int MODE_SQL = 1;//sql语法高亮模式
    //图文模式
    //语法高亮
    //普通文本模式


    private String text = "";//编辑框的文字
    private JTextPane textPane;//编辑框面板
    private JPanel fontComboBox;//字体选择框


    public MyEditorPanel(int mode) {
        setLayout(new BorderLayout());


        switch (mode){
            case MODE_SQL:
                textPane = new SqlEdit();
                break;
            case MODE_IMAGE:
                textPane = new TextAndImageEdit();
                break;
            default:
                textPane = new JTextPane();
        }

        JPanel panel_tool = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //TODO:此处需要改
        fontComboBox = BasicContext.getFontComboBox("字体", "选择编辑框所用字体", "黑体", new BasicContext.FontComboBoxListener() {
            @Override
            public void error(Exception e) {

            }

            @Override
            public void option(Font font) {

            }
        });

        panel_tool.add(fontComboBox);

        add(panel_tool, BorderLayout.NORTH);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
    }

    private void showImage(String str){
        StringBuffer stringBuffer = new StringBuffer(str);
    }

    public void setText(String str){
        textPane.setText(str);
    }

    public String getText(){
        return textPane.getText();
    }
}
