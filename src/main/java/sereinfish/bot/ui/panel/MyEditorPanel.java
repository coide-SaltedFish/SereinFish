package sereinfish.bot.ui.panel;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.context.BasicContext;
import sereinfish.bot.ui.context.edit.RainCodeEdit;
import sereinfish.bot.ui.context.edit.SqlEdit;
import sereinfish.bot.ui.context.edit.TextAndImageEdit;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.rain.RainCodeFrame;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyEditorPanel extends JPanel {
    public static final int MODE_IMAGE = 0;//图文模式
    public static final int MODE_SQL = 1;//sql语法高亮模式
    public static final int MODE_RAIN = 2;//Rain码高亮
    //图文模式
    //语法高亮
    //普通文本模式

    private int mode;//编辑模式
    private String text = "";//编辑框的文字
    private JTextPane textPane;//编辑框面板

    private JFrame frame;//所在父窗口


    public MyEditorPanel(int mode) {
        setLayout(new BorderLayout());


        switch (mode){
            case MODE_SQL:
                textPane = new SqlEdit();
                break;
            case MODE_IMAGE:
                textPane = new TextAndImageEdit();
                break;
            case MODE_RAIN:
                textPane = new RainCodeEdit();
                break;
            default:
                textPane = new JTextPane();
        }

        JPanel panel_tool = new JPanel(new FlowLayout(FlowLayout.LEFT));


        panel_tool.add(getFontComboBox());//字体选择
        //rain码快捷填充
        if (mode == MODE_RAIN){
            panel_tool.add(getRainQuickFill());
        }

        add(panel_tool, BorderLayout.NORTH);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
    }

    public void setFrame(JFrame frame){
        this.frame = frame;
    }

    /**
     * 得到字体选择框
     * @return
     */
    private JComboBox getFontComboBox(){
        JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText("字体选择");
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String font:fonts){
            comboBox.addItem(font);

            //设置默认字体
            if (font.equals("微软雅黑")){
                textPane.setFont(new Font(font, Font.PLAIN, 16));
                comboBox.setSelectedItem(font);
            }

        }

        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    textPane.setFont(new Font((String) e.getItem(), Font.PLAIN, 16));
                }
            }
        });
        return comboBox;
    }

    /**
     * rain码快捷填充
     */
    private JButton getRainQuickFill(){
        JButton button_rainCode = new JButton("Rain码生成");
        button_rainCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RainCodeFrame(new RainCodeFrame.RainCodeFrameListener() {
                    @Override
                    public void ok(RainCodeFrame frame, String code) {
                        StyledDocument doc = textPane.getStyledDocument();
//                            //设置插入文本高亮
//                            SimpleAttributeSet keyWord = new SimpleAttributeSet();
//                            StyleConstants.setForeground(keyWord, Color.RED);
//                            StyleConstants.setBackground(keyWord, Color.YELLOW);

                        try {
                            int start = textPane.getCaretPosition();
                            doc.insertString(start, code, new SimpleAttributeSet());
//                                doc.setCharacterAttributes(start, code.length(), keyWord, false);
                        } catch (BadLocationException badLocationException) {
                            SfLog.getInstance().e(RainCodeFrame.class, badLocationException);
                            new TipDialog(frame,"错误", badLocationException.getMessage(), true);
                        }

                        frame.setVisible(false);
                        frame.dispose();
                    }

                    @Override
                    public void cancel(RainCodeFrame frame) {
                        frame.setVisible(false);
                        frame.dispose();
                    }

                    @Override
                    public void error(RainCodeFrame frame, String e) {
                        new TipDialog(frame,"错误", e, true);
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }).setVisible(true);
            }
        });

        return button_rainCode;
    }

    private void showImage(String str){
        StringBuffer stringBuffer = new StringBuffer(str);
    }

    public void setText(String str){
        if (str != null){
            textPane.setText(str);
        }
    }

    public String getText(){
        return textPane.getText();
    }

    public JTextPane getTextPane() {
        return textPane;
    }
}
