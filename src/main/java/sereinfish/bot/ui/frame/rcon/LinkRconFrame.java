package sereinfish.bot.ui.frame.rcon;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.net.mc.rcon.ex.AuthenticationException;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.layout.VFlowLayout;
import sereinfish.bot.ui.textfield.plainDocument.IPTextField;
import sereinfish.bot.ui.textfield.plainDocument.NumberTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class LinkRconFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    private JTextField textField_ip;
    private JTextField textField_port;
    private JPasswordField passwordField;

    private JButton button_link;
    private JButton button_cancel;

    public LinkRconFrame(){
        setTitle("连接到Rcon");
        setBounds(100, 100, 315, 320);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        VFlowLayout flowLayout_input = new VFlowLayout();
        flowLayout_input.setHorizontalFill(true);
        JPanel panel_input = new JPanel(flowLayout_input);//输入控件部分
        contentPane.add(new JScrollPane(panel_input),BorderLayout.CENTER);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        JPanel panel_btn = new JPanel(flowLayout);//按钮控件部分
        contentPane.add(panel_btn,BorderLayout.SOUTH);

        panel_input.add(getIpPanel());
        panel_input.add(getPasWPanel());
        panel_input.add(getPortPanel());

        button_link = new JButton("连接");
        button_cancel = new JButton("取消");

        panel_btn.add(button_link);
        panel_btn.add(button_cancel);

        //连接按钮
        button_link.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = textField_ip.getText();
                String portStr = textField_port.getText();
                String passW = new String(passwordField.getPassword());
                //验证合法性
                if (ip.equals("")){
                    new TipDialog(LinkRconFrame.this,"错误","IP不能为空",true);
                    return;
                }

                if (passW.equals("")){
                    new TipDialog(LinkRconFrame.this,"错误","密码不能为空",true);
                    return;
                }
                if (portStr.equals("")){
                    new TipDialog(LinkRconFrame.this,"错误","端口不能为空",true);
                    return;
                }
                try{
                    int port = Integer.valueOf(portStr);
                    RconManager.getInstance().link(new RconConf(ip, port, passW));
                    new TipDialog(LinkRconFrame.this,"提示","连接成功",true);
                    LinkRconFrame.this.dispose();
                }catch (NumberFormatException e1){
                    SfLog.getInstance().e(this.getClass(), e1);
                    new TipDialog(LinkRconFrame.this,"错误","端口类型错误",true);
                    return;
                } catch (IOException ioException) {
                    SfLog.getInstance().e(this.getClass(), ioException);
                    new TipDialog(LinkRconFrame.this,"提示","连接失败，IO异常",true);
                } catch (AuthenticationException authenticationException) {
                    SfLog.getInstance().e(this.getClass(), authenticationException);
                    new TipDialog(LinkRconFrame.this,"提示","连接失败，身份验证异常",true);
                }

            }
        });

        //取消按钮
        button_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
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

        setVisible(true);
    }

    /**
     * IP输入框
     * @return
     */
    private JPanel getIpPanel(){
        textField_ip = new JTextField();
        textField_ip.setDocument(new IPTextField(16));
        JPanel panelAccount = new JPanel(new BorderLayout());
        JLabel label = new JLabel("IP：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);
        panel.add(textField_ip);

        panelAccount.add(label,BorderLayout.WEST);
        panelAccount.add(panel,BorderLayout.CENTER);
        return panelAccount;
    }

    /**
     * 端口输入框
     * @return
     */
    private JPanel getPortPanel(){
        textField_port = new JTextField();
        textField_port.setDocument(new NumberTextField(5));
        JPanel panelAccount = new JPanel(new BorderLayout());
        JLabel label = new JLabel("端口：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);
        panel.add(textField_port);

        panelAccount.add(label,BorderLayout.WEST);
        panelAccount.add(panel,BorderLayout.CENTER);
        return panelAccount;
    }

    /**
     * 密码输入框
     * @return
     */
    private JPanel getPasWPanel(){
        passwordField = new JPasswordField();
        JPanel panelAccount = new JPanel(new BorderLayout());
        JLabel label = new JLabel("密码：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);
        panel.add(passwordField);

        panelAccount.add(label,BorderLayout.WEST);
        panelAccount.add(panel,BorderLayout.CENTER);
        return panelAccount;
    }
}
