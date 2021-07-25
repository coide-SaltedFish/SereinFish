package sereinfish.bot.ui.context.edit;
/*
 * @Author: your name
 * @Date: 2021-07-22 15:24:53
 * @LastEditTime: 2021-07-22 15:32:58
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 */

import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.ServerListPing;
import sun.font.FontDesignMetrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * 关键字特殊处理面板
 *
 * @author Administrator
 */
public class SqlEdit extends JTextPane{
    protected StyleContext m_context;
    protected DefaultStyledDocument m_doc;
    private MutableAttributeSet keyAttr, normalAttr, numAttr, stringAttr;
    private MutableAttributeSet bracketAttr;
    private MutableAttributeSet inputAttributes = new RTFEditorKit()
            .getInputAttributes();

    private Color bgColor = new Color(43,43,43);//背景颜色
    private Color keyColor = Color.decode("#FFC66D");//关键字颜色
    private Color numColor = Color.decode("#6897BB");//数字颜色
    private Color textColor = new Color(167,182,197);//普通文本颜色
    private Color bracketColor = Color.lightGray;//括号颜色
    private Color notesColor = Color.decode("#629755");//注释颜色

    private Font font;//字体
    private float fontSize = 16;//大小

    private int textLineNum = 0;
    private int LINE_HEIGHT = 21;

    /**
     * 所有关键字
     */
    private final static String[] _keys = new String[]{"select", "from",
            "where", "like", "and", "or", "order", "group", "sum", "avg",
            "not", "in", "create", "grand", "null", "count", "max", "min",
            "start", "with", "connect", "update", "delete", "set", "values",
            "view", "table", "as", "distinct", "into", "drop", "is", "on",
            "exists", "by", "tree", "table", "cust", "union", "dual",
            "trigger", "function", "procedure", "begin", "end", "for", "loop",
            "while", "insert", "count", "if", "else", "then", "commit",
            "rollback", "return", "declare", "when", "elsif", "open", "fetch",
            "close", "exit", "exception", "execute"};
    /**
     * 所与排除字符集
     */
    private final static char[] _character = new char[]{'(', ')', ',', ';',
            ':', '\t', '\n', '+', '-', '*', '/', '{', '}', ' '};

    /**
     * 初始化，包括关键字颜色，和非关键字颜色
     */
    public SqlEdit() {
        super();
        //setBackground(bgColor);
        setDoubleBuffered(true);//设置双缓冲绘制
        setForeground(textColor);
        setCaretColor(textColor);
        setOpaque(false);
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ServerListPing.class.getClassLoader().getResourceAsStream(FileHandle.JetBrainsMonoFontFile));
            font = font.deriveFont(fontSize);
        } catch (FontFormatException e) {
            SfLog.getInstance().e(this.getClass(), "字体文件加载异常", e);
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "字体文件加载异常", e);
        }
        setFont(font);

        m_context = new StyleContext();
        m_doc = new DefaultStyledDocument(m_context);
        this.setDocument(m_doc);

        textLineNum = getText().split("\n").length;

        this.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                SqlEdit.this.repaint();
                if (Math.abs(getText().split("\n").length - textLineNum) > 1){
                    syntaxParse();
                }else {
                    dealSingleRow();
                }
                textLineNum = getText().split("\n").length;
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SqlEdit.this.repaint();
            }
        });
        //关键字显示属性
        keyAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(keyAttr, keyColor);

        //数字字显示属性
        numAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(numAttr, numColor);

        //字符串显示属性
        stringAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(stringAttr, notesColor);

        //一般文本显示属性
        normalAttr = new SimpleAttributeSet();
        //StyleConstants.setFontFamily(normalAttr, "serif");
        StyleConstants.setBold(normalAttr, false);
        StyleConstants.setForeground(normalAttr, textColor);
        bracketAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(bracketAttr, bracketColor);
        //StyleConstants.setFontFamily(bracketAttr, "serif");
        StyleConstants.setBold(bracketAttr, true);
    }

    @Override
    public void paint(Graphics g) {
        drawLineBG(g);//绘制背景
        super.paint(g);
        StyleConstants.setFontSize(getInputAttributes(), (int) fontSize);
        drawLineNumber(g);//绘制行号
    }

    //绘制光标行背景
    protected void drawLineBG(Graphics graphics){
        Graphics2D graphics2D = (Graphics2D) graphics;
        //获得光标所在行
        Element root = m_doc.getDefaultRootElement();
        // 光标当前行
        int cursorPos = this.getCaretPosition(); // 前光标的位置
        int line = root.getElementIndex(cursorPos);// 当前行

        //设置颜色
        graphics2D.setColor(bgColor);
        //绘制背景
        graphics2D.fillRect(0 ,0, getWidth(), getHeight());
        //设置颜色
        graphics2D.setColor(new Color(50,50,50));
        //绘制
        graphics2D.fillRect(0, line * LINE_HEIGHT, getWidth(), LINE_HEIGHT);
    }

    protected void drawLineNumber(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        // 获得有多少行
        StyledDocument docu = getStyledDocument();
        Element element = docu.getDefaultRootElement();
        int rows = element.getElementCount();

        //计算行高
        int lineH = getHeight() / rows;

        // 绘制行号的背景色
        graphics2D.setColor(new Color(49,51,53));
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        graphics2D.fillRect(0, 0, metrics.stringWidth(rows + "") + 5, getHeight());
        //绘制边界
        graphics2D.setColor(new Color(85,85,85));
        graphics2D.fillRect(metrics.stringWidth(rows + "") + 4, 0, 1, getHeight());

        setMargin(new Insets(0, metrics.stringWidth(rows + "") + 10, 0, 0));


        //System.out.println("y:" + getY());
        graphics2D.setFont(font);

        //获得光标所在行
        Element root = m_doc.getDefaultRootElement();
        // 光标当前行
        int cursorPos = this.getCaretPosition(); // 前光标的位置
        int line = root.getElementIndex(cursorPos);// 当前行

        int y = 0;
        for (int row = 0; row < rows; row++) {
            // 绘制行号的颜色
            if (row == line){
                graphics2D.setColor(new Color(162,161,161));
            }else {
                graphics2D.setColor(new Color(94,94,83));
            }

            y += metrics.getAscent();
            graphics2D.drawString((row + 1) + "", 2, y);
            y += 4;
        }
    }

    /**
     * 单行注释
     */
    public void setSingleLineNoteCharacterAttributes() {
        String text = this.getText();
        int startPointer = 0;
        int endPointer = 0;
        if (text.indexOf("--") == -1) {
            return;
        }

        String lines[] = text.split("\n");
        for (int i = 0 ; i < lines.length; i++) {
            if ((startPointer = lines[i].indexOf("--")) == -1) {
                continue;
            }
            endPointer = lines[i].length();
            if (startPointer >= endPointer) {
                break;
            }
            int start = 0;
            for (int j = 0 ; j < i; j++){
                start += lines[j].length();
            }
            SwingUtilities.invokeLater(new ColouringWord(this, start + startPointer, start + endPointer, notesColor));
        }
    }

    /**
     * 判断字符是不是在排除字符行列
     *
     * @param _ch
     * @return
     */
    private boolean isCharacter(char _ch) {
        for (int i = 0; i < _character.length; i++) {
            if (_ch == _character[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置关键字颜色
     *
     * @param _key
     * @param _start
     * @param _length
     * @return
     */
    private int setKeyColor(String _key, int _start, int _length) {
        for (int i = 0; i < _keys.length; i++) {
            int li_index = _key.indexOf(_keys[i]);
            if (li_index < 0) {
                continue;
            }
            int li_legnth = li_index + _keys[i].length();
            if (li_legnth == _key.length()) {
                if (li_index == 0) {//处理单独一个关键字的情况，例如：if else 等
                    m_doc.setCharacterAttributes(_start, _keys[i].length(),
                            keyAttr, false);
                } else {//处理关键字前面还有字符的情况，例如：)if ;else 等
                    char ch_temp = _key.charAt(li_index - 1);
                    if (isCharacter(ch_temp)) {
                        m_doc.setCharacterAttributes(_start + li_index,
                                _keys[i].length(), keyAttr, false);
                    }
                }
            } else {
                if (li_index == 0) {//处理关键字后面还有字符的情况，例如：if( end;等
                    char ch_temp = _key.charAt(_keys[i].length());
                    if (isCharacter(ch_temp)) {
                        m_doc.setCharacterAttributes(_start, _keys[i].length(),
                                keyAttr, false);
                    }
                } else {//处理关键字前面和后面都有字符的情况，例如：)if( 等
                    char ch_temp = _key.charAt(li_index - 1);
                    char ch_temp_2 = _key.charAt(li_legnth);
                    if (isCharacter(ch_temp) && isCharacter(ch_temp_2)) {
                        m_doc.setCharacterAttributes(_start + li_index,
                                _keys[i].length(), keyAttr, false);
                    }
                }
            }
        }
        return _length + 1;
    }

    /**
     * 设置数字颜色
     *
     * @param _key
     * @param _start
     * @param _length
     * @return
     */
    private void setNumColor(String _key, int _start, int _length) {
        boolean f = false;
        for(char c:_key.toCharArray()){
            if (c >= '0' && c <= '9'){
                f = true;
            }
        }
        if (!f){
            return;
        }
        ArrayList<String> keys = new ArrayList<>();
        String str = "";
        for (int i = 0; i < _key.length(); i++){
            //如果是忽略符号
            if (isCharacter(_key.charAt(i))){
                keys.add(str);
                str = "";

                keys.add(String.valueOf(_key.charAt(i)));
            }else {
                str += _key.charAt(i);
            }
        }
        keys.add(str);

        for (String key:keys){
            try{
                Double.valueOf(key);
                m_doc.setCharacterAttributes(_start, key.length(), numAttr, false);
            }catch (Exception e){

            }
            _start += key.length();
        }
    }

    /**
     * 设置字符串
     * @param _key
     * @param _start
     * @param _length
     */
    private void setString(String _key, int _start, int _length) {
        boolean f = false;
        for (char c : _key.toCharArray()) {
            if (c == '"' || c == '\'') {
                f = true;
            }
        }
        if (!f) {
            return;
        }

        ArrayList<String> keys = new ArrayList<>();
        int start = 0;
        String tem = "";
        for (int i = 0; i < _key.length(); i++) {

            if (_key.charAt(i) == '"' && (i >= 1 && _key.charAt(i - 1) != '\\')){
                keys.add(tem);
                tem = "";
                //查找后一个引号
                start = i;
                if (_key.substring(start).indexOf('"') == -1) {
                    keys.add(_key.substring(start));
                    break;
                } else {
                    int end = 0;
                    if ((end = _key.substring(start + 1).indexOf('"')) != -1  && end != start){
                        end = start + end + 2;
                        keys.add(_key.substring(start, end));
                        start = end;
                    }else{
                        keys.add(_key.substring(start));
                        break;
                    }
                }
                i = start + 2;
            } else if (_key.charAt(i) == '\'') {
                keys.add(tem);
                tem = "";
                //查找后一个引号
                start = i;
                if (_key.substring(start).indexOf('\'') == -1) {
                    keys.add(_key.substring(start));
                    break;
                } else {
                    int end = 0;
                    if ((end = _key.substring(start + 1).indexOf('\'')) != -1  && end != start){
                        end = start + end + 2;
                        keys.add(_key.substring(start, end));
                        start = end;
                    }else{
                        keys.add(_key.substring(start));
                        break;
                    }
                }
                i = start + 2;
            }else {
                tem += String.valueOf(_key.charAt(i));
            }
        }

        for (String key:keys){
            if (key.indexOf('"') != -1 || key.indexOf('\'') != -1){
                m_doc.setCharacterAttributes(_start, key.length(), stringAttr, false);
            }
            _start += key.length();
        }
    }

    /**
     * 处理一行的数据
     *
     * @param _start
     * @param _end
     */
    private void dealText(int _start, int _end) {
        String text = "";
        try {
            text = m_doc.getText(_start, _end - _start).toUpperCase();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (text == null || text.equals("")) {
            return;
        }
        int xStart = 0;
        // 析关键字---
        m_doc.setCharacterAttributes(_start, text.length(), normalAttr, false);
        MyStringTokenizer st = new MyStringTokenizer(text);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s == null)
                return;
            xStart = st.getCurrPosition();
            setKeyColor(s.toLowerCase(), _start + xStart, s.length());
            setNumColor(s.toLowerCase(), _start + xStart, s.length());
            setString(s.toLowerCase(), _start + xStart, s.length());
        }
        setSingleLineNoteCharacterAttributes();
        inputAttributes.addAttributes(normalAttr);
    }

    /**
     * 在进行文本修改的时候
     * 获得光标所在行，只对该行进行处理
     */
    private void dealSingleRow() {
        Element root = m_doc.getDefaultRootElement();
        // 光标当前行
        int cursorPos = this.getCaretPosition(); // 前光标的位置
        int line = root.getElementIndex(cursorPos);// 当前行
        Element para = root.getElement(line);
        int start = para.getStartOffset();
        int end = para.getEndOffset() - 1;// 除\r字符
        dealText(start, end);
    }

    /**
     * 在初始化面板的时候调用该方法，
     * 查找整个篇幅的关键字
     */
    public void syntaxParse() {
        Element root = m_doc.getDefaultRootElement();
        int li_count = root.getElementCount();
        for (int i = 0; i < li_count; i++) {
            Element para = root.getElement(i);
            int start = para.getStartOffset();
            int end = para.getEndOffset() - 1;// 除\r字符
            dealText(start, end);
        }
    }

    /**
     * 多线程绘制颜色
     */
    class ColouringWord implements Runnable {
        private int startPointer;
        private int endPointer;
        private Color color;
        private JTextPane jTextPane;

        public ColouringWord(JTextPane jTextPane, int pos, int len, Color color) {
            this.jTextPane = jTextPane;
            this.startPointer = pos;
            this.endPointer = len;
            this.color = color;
        }

        @Override
        public void run() {
            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            StyleConstants.setForeground(attributeSet, color);
            boolean replace = false;
            int p0 = startPointer;
            int p1 = endPointer;
            if (p0 != p1) {
                StyledDocument doc = jTextPane.getStyledDocument();
                doc.setCharacterAttributes(p0, p1 - p0, attributeSet, replace);
            } else {
                MutableAttributeSet inputAttributes = jTextPane
                        .getInputAttributes();
                if (replace) {
                    inputAttributes.removeAttributes(inputAttributes);
                }
                inputAttributes.addAttributes(attributeSet);
            }
        }
    }
}

/**
 * 在分析字符串的同时，记录每个token所在的位置
 */
class MyStringTokenizer extends StringTokenizer {
    String sval = " ";
    String oldStr, str;
    int m_currPosition = 0, m_beginPosition = 0;

    MyStringTokenizer(String str) {
        super(str, " ");
        this.oldStr = str;
        this.str = str;
    }

    public String nextToken() {
        try {
            String s = super.nextToken();
            int pos = -1;
            if (oldStr.equals(s)) {
                return s;
            }
            pos = str.indexOf(s + sval);
            if (pos == -1) {
                pos = str.indexOf(sval + s);
                if (pos == -1)
                    return null;
                else
                    pos += 1;
            }
            int xBegin = pos + s.length();
            str = str.substring(xBegin);
            m_currPosition = m_beginPosition + pos;
            m_beginPosition = m_beginPosition + xBegin;
            return s;
        } catch (java.util.NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 返回token在字符串中的位置
    public int getCurrPosition() {
        return m_currPosition;
    }
}