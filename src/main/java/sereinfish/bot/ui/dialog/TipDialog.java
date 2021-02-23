package sereinfish.bot.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TipDialog extends JDialog {
    private String tip;
    private JPanel contentPane;
    private JTextArea textArea_tip;

    public TipDialog(JFrame owner, String title, String tip, boolean modal) {
        super(owner, title, modal);
        this.tip = tip;
        //设置窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                dispose();
            }
        });

        build();
        setVisible(true);
    }

    public TipDialog(String title,String tip, boolean model){
        this((JFrame) null,title,tip,model);
    }

    /**
     * 构建窗体
     */
    private void build(){
        setBounds(100, 100, 200, 170);
        setLocationRelativeTo(null);
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        textArea_tip = new JTextArea();
        textArea_tip.setText(tip);
        textArea_tip.setEnabled(false);
        textArea_tip.setOpaque(false);
        textArea_tip.setDisabledTextColor(Color.BLACK);

        contentPane.add(new JScrollPane(textArea_tip),BorderLayout.CENTER);

        JButton btn_ok = new JButton("确定");
        btn_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(btn_ok);
        contentPane.add(panel,BorderLayout.SOUTH);
    }

    public void setTip(String tip){
        this.tip = tip;
        textArea_tip.setText(tip);
    }

    public void setTitle(String title){
        setTitle(title);
    }
}
