package sereinfish.bot.ui.dialog;

import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据传入数据生成数据填写窗口
 */
public class LinkDialog extends JDialog {
    //提示等级
    public final static int TIP_NORMAL = 0;
    public final static int TIP_WARN = 1;
    public final static int TIP_ERROR = 2;

    private JPanel contentPane;

    private JButton btn_pass,btn_cancel;//两按钮
    private Map<String,JTextField> textFieldMap = new HashMap<>();//填写框

    private LinkFrameListener linkFrameListener;//窗口事件

    private JLabel label_tip;//提示信息

    private String title;
    private String keys[];
    private String values[] = null;

    /**
     * 连接窗口
     */
    public LinkDialog(Dialog dialog,boolean model){
        super(dialog,model);
    }

    /**
     * 创建面板
     */
    private void build(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        setTitle(title);

        //生成填写框
        JPanel panel = new JPanel();
        VFlowLayout vFlowLayout = new VFlowLayout(VFlowLayout.TOP);
        vFlowLayout.setHorizontalFill(true);
        panel.setLayout(vFlowLayout);

        for(int i = 0; i < keys.length; i++){
            String s = keys[i];

            JPanel jPanel_line = new JPanel();
            jPanel_line.setLayout(new BoxLayout(jPanel_line,BoxLayout.LINE_AXIS));
            JLabel jLabel = new JLabel(s + ":");

            JTextField jTextField = new JTextField();
            if (values != null){
                jTextField.setText(values[i]);
            }

            jPanel_line.add(jLabel);
            jPanel_line.add(jTextField);

            textFieldMap.put(s,jTextField);

            jPanel_line.setAlignmentX(Component.LEFT_ALIGNMENT);
            jPanel_line.setAlignmentY(Component.CENTER_ALIGNMENT);

            panel.add(jPanel_line);
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        contentPane.add(scrollPane,BorderLayout.CENTER);

        //生成按钮
        JPanel btn_panel = new JPanel(new FlowLayout());
        btn_pass = new JButton("确定");
        btn_cancel = new JButton("取消");

        //按钮事件
        btn_pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String,String> map = new HashMap<>();
                //得到所有值
                for (String s:keys){
                    String str = textFieldMap.get(s).getText().toString();
                    if (str.equals("")){
                        setTip("不能为空：" + s,TIP_ERROR);
                        return;
                    }
                    map.put(s,str);
                }
                linkFrameListener.passOnclick(LinkDialog.this,map);
            }
        });

        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linkFrameListener.cancelOnClick(LinkDialog.this);
            }
        });

        btn_panel.add(btn_pass);
        btn_panel.add(btn_cancel);

        contentPane.add(btn_panel,BorderLayout.SOUTH);

        //提示信息
        label_tip = new JLabel("提示");
        label_tip.setOpaque(true);
        label_tip.setBackground(Color.lightGray);
        contentPane.add(label_tip,BorderLayout.NORTH);
    }

    /**
     * 设置窗口数据
     * @param title
     * @param key
     * @param linkFrameListener
     * @return
     */
    public LinkDialog setLinkDialog(String title, String[] key,LinkFrameListener linkFrameListener){
        this.linkFrameListener = linkFrameListener;
        this.title = title;
        this.keys = key;

        build();

        return this;
    }

    public LinkDialog setLinkDialog(String title, String[] key,String[] values,LinkFrameListener linkFrameListener){
        this.linkFrameListener = linkFrameListener;
        this.title = title;
        this.keys = key;
        this.values = values;

        build();

        return this;
    }

    /**
     * 清空编辑框
     */
    public void clean(){
        for (Map.Entry<String,JTextField> entry:textFieldMap.entrySet()){
            entry.getValue().setText("");
        }
    }

    /**
     * 窗口事件
     */
    public interface LinkFrameListener{
        public void passOnclick(LinkDialog linkDialog, Map<String,String> values);//确定按钮点击事件
        public void cancelOnClick(LinkDialog linkDialog);//取消按钮点击事件
    }

    @Override
    @Deprecated
    public void dispose() {
        super.dispose();
    }

    /**
     * 关闭事件
     */
    public void close(){
        setVisible(false);
        setModal(false);
        super.dispose();
    }


    /**
     * 提示信息
     * @param s
     */
    public void setTip(String s,int mode){
        switch (mode){
            case TIP_NORMAL:
                label_tip.setForeground(Color.BLACK);
                break;
            case TIP_WARN:
                label_tip.setForeground(Color.ORANGE);
                break;
            case TIP_ERROR:
                label_tip.setForeground(Color.RED);
                break;
        }
        label_tip.setText(s);
    }
}
