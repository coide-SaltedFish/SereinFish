package sereinfish.bot.ui.frame.job;

import sereinfish.bot.job.MyJob;
import sereinfish.bot.job.entity.JobMsg;
import sereinfish.bot.job.entity.JobType;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.layout.VFlowLayout;
import sereinfish.bot.ui.textfield.plainDocument.NumberTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class InsertJobMsgFrame extends JFrame {
    private long recipient;
    private InsertListener listener;
    private JPanel contentPane;

    private JCheckBox checkBox_isGroup;
    private JButton btn_value;
    private String value = "";
    private JTextField textField_recipient;

    public InsertJobMsgFrame(long recipient, InsertListener listener){
        this.recipient = recipient;
        this.listener = listener;

        setTitle("添加JobMsg");
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        build();
    }

    private void build(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 440, 500);
        setLocationRelativeTo(null);

        JPanel panel_input = new JPanel();
        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        panel_input.setLayout(vFlowLayout);
        contentPane.add(new JScrollPane(panel_input), BorderLayout.CENTER);


        //是否群消息
        JPanel panel_isGroup = new JPanel(new BorderLayout());
        checkBox_isGroup = new JCheckBox("是否群消息");
        checkBox_isGroup.setSelected(true);
        panel_isGroup.add(checkBox_isGroup, BorderLayout.CENTER);
        panel_input.add(panel_isGroup);

        //value
        JPanel panel_value = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btn_value = new JButton("消息内容");
        btn_value.addActionListener(e -> {
            EditFrame editFrame = new EditFrame("消息内容", new EditFrame.EditListener() {
                @Override
                public void save(EditFrame editFrame, String text) {
                    value = text;
                    editFrame.close();
                }

                @Override
                public void cancel(EditFrame editFrame) {
                    editFrame.close();
                }
            });
            editFrame.setText(value);
            editFrame.setVisible(true);
        });

        //接收者
        JPanel panel_recipient = new JPanel(new BorderLayout());
        textField_recipient = new JTextField();
        textField_recipient.setDocument(new NumberTextField(10));
        textField_recipient.setText(recipient + "");
        System.out.println(recipient);
        panel_recipient.add(new JLabel("接收者："), BorderLayout.WEST);
        panel_recipient.add(textField_recipient, BorderLayout.CENTER);
        panel_input.add(panel_recipient);

        panel_value.add(btn_value);
        panel_input.add(panel_value);

        //按钮列表
        JButton btn_ok = new JButton("确定");
        JButton btn_cancel = new JButton("取消");

        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel_btn.add(btn_cancel);
        panel_btn.add(btn_ok);
        contentPane.add(panel_btn, BorderLayout.SOUTH);



        btn_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isGroup = checkBox_isGroup.isSelected();
                long recipient = 0;

                if (textField_recipient.getText().trim().equals("")){
                    new TipDialog(InsertJobMsgFrame.this,"错误","接收者不能为空",true);
                    return;
                }
                try{
                    recipient = Long.valueOf(textField_recipient.getText());
                }catch (Exception e1){
                    new TipDialog(InsertJobMsgFrame.this,"错误","接收者非法字符",true);
                    return;
                }

                if (value.trim().equals("")){
                    new TipDialog(InsertJobMsgFrame.this,"错误","消息不能为空",true);
                    return;
                }

                JobMsg jobMsg = new JobMsg(isGroup, value, recipient);

                if (listener != null){
                    listener.save(InsertJobMsgFrame.this, jobMsg);
                }
            }
        });

        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null){
                    listener.cancel(InsertJobMsgFrame.this);
                }

                InsertJobMsgFrame.this.setVisible(false);
                InsertJobMsgFrame.this.dispose();
            }
        });
    }

    public interface InsertListener{
        public void save(InsertJobMsgFrame frame, JobMsg jobMsg);
        public void cancel(InsertJobMsgFrame frame);
    }
}
