package sereinfish.bot.ui.frame.rain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.dialog.FileChooseDialog;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RainCodeFrame extends JFrame {
    private JPanel contentPane;//最底层面板
    private RainCodeFrameListener listener;
    private JComboBox comboBox_ModeChoose;//模式选择

    private JLabel label_tip;//提示文字
    private JTextField textField_input;//输入参数
    private JButton button_chooseFile;//选择文件
    private JCheckBox checkBox_isFile;//是否文件，图片专用

    private static final int MODE_IMAGE = 0;
    private static final int MODE_AT = 1;
    private static final int MODE_FACE = 2;

    private int mode = MODE_IMAGE;

    private ArrayList<Mode> modeList = new ArrayList<>();

    private File file;//选中的文件



    public RainCodeFrame(@NonNull RainCodeFrameListener listener) {
        this.listener = listener;
        setTitle("RainCode 自动生成");
        init();
        build();
    }

    private void init(){
        modeList.add(new Mode("图片", MODE_IMAGE));
        modeList.add(new Mode("AT", MODE_AT));
        modeList.add(new Mode("表情", MODE_FACE));
    }

    /**
     * 构建面板
     * @return
     */
    private void build(){
        setBounds(100, 100, 500, 250);
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        JPanel panel_btn = new JPanel(flowLayout);//按钮控件部分
        contentPane.add(panel_btn,BorderLayout.SOUTH);

        //操作部分
        JPanel panel_info = new JPanel();
        VFlowLayout vFlowLayout = new VFlowLayout(VFlowLayout.TOP);
        vFlowLayout.setHorizontalFill(true);
        panel_info.setLayout(vFlowLayout);

        panel_info.add(new JPanel(new FlowLayout()).add(getModeChoose()));

        JPanel panel_input = new JPanel();//输入部分
        panel_input.setLayout(new BoxLayout(panel_input,BoxLayout.LINE_AXIS));

        label_tip = new JLabel("选择文件或输入参数:");
        panel_input.add(label_tip);
        //输入框
        textField_input = new JTextField();
        panel_input.add(textField_input);

        //选择文件按钮
        button_chooseFile = new JButton("选择文件");
        panel_input.add(button_chooseFile);

        button_chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出文件选择框
                new FileChooseDialog("选择图片文件", "图片文件", new FileChooseDialog.FileChooseListener() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void option(File f) {
                        file = f;
                        if (checkBox_isFile.isSelected()){
                            textField_input.setText(f.getAbsolutePath());
                        }else{
                            try {
                                textField_input.setText(DigestUtils.md5Hex(new FileInputStream(f)).toUpperCase());
                            } catch (IOException ioException) {
                                SfLog.getInstance().e(RainCodeFrame.class, ioException);
                                listener.error(RainCodeFrame.this, ioException.getMessage());
                            }
                        }
                    }

                    @Override
                    public void error() {
                        listener.error(RainCodeFrame.this, "在选择文件时出现错误");
                    }
                }, "jpg", "jpeg", "png", "gif");
            }
        });

        panel_info.add(panel_input);
        //是否文件
        checkBox_isFile = new JCheckBox("使用本地文件");
        checkBox_isFile.setToolTipText("启用后，生成的RainCode将会指向本地文件");

        panel_info.add(new JPanel(new FlowLayout()).add(checkBox_isFile));

        contentPane.add(new JScrollPane(panel_info), BorderLayout.CENTER);

        //btn
        JButton btn_ok = new JButton("确定");
        JButton btn_cancel = new JButton("取消");

        panel_btn.add(btn_cancel);
        panel_btn.add(btn_ok);

        //确定按钮
        btn_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.ok(RainCodeFrame.this, getRainCode());
            }
        });

        //取消按钮
        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.cancel(RainCodeFrame.this);
            }
        });

        //设置窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                dispose();
            }
        });

        setLocationRelativeTo(null);

        update();
    }

    /**
     * 模式选择
     * @return
     */
    public JComboBox getModeChoose(){
        comboBox_ModeChoose = new JComboBox();

        for (Mode mode:modeList){
            comboBox_ModeChoose.addItem(mode);
        }

        comboBox_ModeChoose.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Object selectItem = comboBox_ModeChoose.getSelectedItem();
                    if (selectItem instanceof Mode){
                        Mode mode = (Mode) selectItem;
                        RainCodeFrame.this.mode = mode.getId();
                        update();
                    }
                }
            }
        });

        return comboBox_ModeChoose;
    }

    /**
     * 界面更新
     */
    private void update(){
        if (mode == MODE_IMAGE){
            label_tip.setText("选择文件或输入参数:");
            button_chooseFile.setEnabled(true);
            checkBox_isFile.setEnabled(true);
        }else if (mode == MODE_AT){
            label_tip.setText("输入要@的QQ号:");
            button_chooseFile.setEnabled(false);
            checkBox_isFile.setEnabled(false);
        }else if (mode == MODE_FACE){
            label_tip.setText("输入表情编号:");
            button_chooseFile.setEnabled(false);
            checkBox_isFile.setEnabled(false);
        }
    }

    private String getRainCode(){
        switch (mode){
            case MODE_AT:
                return "<Rain:At:" + textField_input.getText() + ">";
            case MODE_FACE:
                return "<Rain:Face:" + textField_input.getText() + ">";
            case MODE_IMAGE:
                if (checkBox_isFile.isSelected()){
                    return "<Rain:Image:" + textField_input.getText() + ",File>";
                }else {
                    if (file != null){
                        return "<Rain:Image:" + textField_input.getText() + "." + file.getName().substring(file.getName().lastIndexOf(".") + 1) + ">";
                    }
                }

        }
        return "";
    }

    @AllArgsConstructor
    @Getter
    private class Mode{
        String name;
        int id;

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * 回调事件
     */
    public interface RainCodeFrameListener{
        public void ok(RainCodeFrame frame, String code);
        public void cancel(RainCodeFrame frame);
        public void error(RainCodeFrame frame, String e);
    }
}
