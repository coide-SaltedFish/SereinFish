package sereinfish.bot.ui.frame.job;

import sereinfish.bot.job.MyJob;
import sereinfish.bot.job.entity.JobMsg;
import sereinfish.bot.job.entity.JobType;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.frame.database.insert.InsertFrame;
import sereinfish.bot.ui.layout.VFlowLayout;
import sereinfish.bot.ui.textfield.plainDocument.NumberTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class InsertJobFrame extends JFrame {
    private long recipient;
    private InsertListener listener;
    private JPanel contentPane;

    private JTextField textField_name;
    private JComboBox comboBox_type;
    private JButton btn_value;
    private String value = "";
    private JTextField textField_atTime_H;//时
    private JTextField textField_atTime_M;//分

    public InsertJobFrame(long recipient, InsertListener listener){
        this.recipient = recipient;
        this.listener = listener;

        setTitle("添加Job");
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
        //name
        JPanel panel_name = new JPanel(new BorderLayout());
        textField_name = new JTextField();
        panel_name.add(new JLabel("名称："), BorderLayout.WEST);
        panel_name.add(textField_name, BorderLayout.CENTER);
        panel_input.add(panel_name);
        //type
        buildTypeComboBox();
        JPanel panel_type = new JPanel(new BorderLayout());
        panel_type.add(new JLabel("类型："), BorderLayout.WEST);
        panel_type.add(comboBox_type, BorderLayout.CENTER);
        panel_input.add(panel_type);
        //atTime
        JPanel panel_atTime = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textField_atTime_H = new JTextField();
        textField_atTime_H.setColumns(3);
        textField_atTime_H.setDocument(new NumberTextField(2));

        textField_atTime_M = new JTextField();
        textField_atTime_M.setColumns(3);
        textField_atTime_M.setDocument(new NumberTextField(2));

        panel_atTime.add(new JLabel("时间："));
        panel_atTime.add(textField_atTime_H);
        panel_atTime.add(new JLabel(":"));
        panel_atTime.add(textField_atTime_M);
        panel_input.add(panel_atTime);

        //value
        JPanel panel_value = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btn_value = new JButton("值");
        btn_value.addActionListener(e -> {
            int type = JobType.typeMap.get(comboBox_type.getSelectedItem());
            if (type == JobType.sendMsgJob){
                new InsertJobMsgFrame(recipient, new InsertJobMsgFrame.InsertListener() {
                    @Override
                    public void save(InsertJobMsgFrame frame, JobMsg jobMsg) {
                        value = MyYuQ.toJson(jobMsg, JobMsg.class);

                        frame.setVisible(false);
                        frame.dispose();
                    }

                    @Override
                    public void cancel(InsertJobMsgFrame frame) {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }).setVisible(true);
            }else {
                EditFrame editFrame = new EditFrame("任务值", new EditFrame.EditListener() {
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
                editFrame.setText((String) value);
                editFrame.setVisible(true);
            }
        });
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
                String name = textField_name.getText();
                int type = JobType.typeMap.get(comboBox_type.getSelectedItem());
                String atTime = getAtTime();

                if (name.trim().equals("")){
                    new TipDialog(InsertJobFrame.this,"错误","名称不能为空",true);
                    return;
                }

                if (atTime.trim().equals("")){
                    new TipDialog(InsertJobFrame.this,"错误","时间不能为空",true);
                    return;
                }

                MyJob myJob = new MyJob("", name, type, value, atTime);

                if (listener != null){
                    listener.save(InsertJobFrame.this, myJob);
                }
            }
        });

        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null){
                    listener.cancel(InsertJobFrame.this);
                }

                InsertJobFrame.this.setVisible(false);
                InsertJobFrame.this.dispose();
            }
        });
    }

    /**
     * 得到时间
     * @return
     */
    private String getAtTime(){
        //判断是否为空
        if (textField_atTime_M.getText().trim().equals("")){
            return "";
        }
        //小时
        try{
            int tH = Integer.decode(textField_atTime_H.getText());
            int tM = Integer.decode(textField_atTime_M.getText());

            tH = Math.abs(tH);
            tM = Math.abs(tM);

            if (tH > 23){
                tH = 23;
            }

            if (tM > 59){
                tM = 59;
            }


            String strH = "00";
            String strM = "00";

            if (tH < 10){
                strH = "0" + tH;
            }else {
                strH = tH + "";
            }

            if (tM < 10){
                strM = "0" + tM;
            }else {
                strM = tM + "";
            }

            if (textField_atTime_H.getText().trim().equals("")){
                return strM;
            }else {
                return strH + ":" + strM;
            }
        }catch (NumberFormatException e){
            new TipDialog(InsertJobFrame.this,"错误","时间输入错误",true);
            return "";
        }
    }


    /**
     * 初始化类型选择器
     */
    private void buildTypeComboBox(){
        comboBox_type = new JComboBox();
        for (Map.Entry<String, Integer> entry:JobType.typeMap.entrySet()){
            comboBox_type.addItem(entry.getKey());
        }
    }

    public interface InsertListener{
        public void save(InsertJobFrame frame, MyJob myJob);
        public void cancel(InsertJobFrame frame);
    }
}
