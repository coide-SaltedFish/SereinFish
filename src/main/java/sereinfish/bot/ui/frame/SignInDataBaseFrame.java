package sereinfish.bot.ui.frame;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 数据库登录窗口
 */
public class SignInDataBaseFrame extends JFrame {
    private JPanel contentPane;//最底层面板

    private JComboBox comboBox_state;//连接模式选择下拉框
    private int comboBoxSelect = DataBaseConfig.MY_SQL;

    private JTextField textField_account;//账号
    private JPasswordField passwordField;//密码
    private JTextField textField_baseName;//数据库名
    private JTextField textField_ip;//ip
    private JTextField textField_port;//端口

    private JButton btn_ok;

    public SignInDataBaseFrame(String title) {
        setTitle(title);
    }

    /**
     * 构建面板
     * @return
     */
    public SignInDataBaseFrame build(){
        setBounds(100, 100, 315, 320);
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
        //input
        initComboBox();
        panel_input.add(comboBox_state);
        panel_input.add(getAccountPanel());
        panel_input.add(getPasswordPanel());
        panel_input.add(getDatabasePanel());
        panel_input.add(getIpPanel());
        panel_input.add(getPortPanel());

        //btn
        btn_ok = new JButton("确定");
        JButton btn_cancel = new JButton("取消");

        panel_btn.add(btn_cancel);
        panel_btn.add(btn_ok);

        //确定按钮
        btn_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
                btn_ok.setText("确定");
            }
        });

        //取消按钮
        btn_cancel.addActionListener(new ActionListener() {
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

        setLocationRelativeTo(null);
        return this;
    }

    /**
     * 初始化下拉框
     * @return
     */
    private void initComboBox(){
        comboBox_state = new JComboBox();

        comboBox_state.addItem("MySQL");
        comboBox_state.addItem("SQL Server");
        comboBox_state.addItem("Sqlite");

        comboBox_state.setSelectedIndex(0);

        comboBox_state.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    if (e.getItem().equals("MySQL")){
                        comboBoxSelect = DataBaseConfig.MY_SQL;
                        isSqlite(false);
                    }else if (e.getItem().equals("SQL Server")) {
                        comboBoxSelect = DataBaseConfig.SQL_SERVER;
                        isSqlite(false);
                    }else if(e.getItem().equals("Sqlite")) {
                        comboBoxSelect = DataBaseConfig.SQLITE;
                        isSqlite(true);
                    }
                }
            }
        });
    }

    /**
     * 是否开启sqlite模式
     */
    private void isSqlite(boolean modal){
        if (modal){
            textField_account.setEnabled(false);
            passwordField.setEnabled(false);
            textField_ip.setEnabled(false);
            textField_port.setEnabled(false);
        }else {
            textField_account.setEnabled(true);
            passwordField.setEnabled(true);
            textField_ip.setEnabled(true);
            textField_port.setEnabled(true);
        }
    }

    /**
     * 开始登录
     */
    private void login(){
        btn_ok.setText("连接中...");

        String account = textField_account.getText();
        if (account.trim().equals("")){
            new TipDialog(this,"错误","账号不能为空",true);
            return;
        }

        String password = passwordField.getText();
        if (password.trim().equals("")){
            new TipDialog(this,"错误","密码不能为空",true);
            return;
        }

        String databaseName = textField_baseName.getText();
        if (databaseName.trim().equals("")){
            new TipDialog(this,"错误","库名不能为空",true);
            return;
        }

        String ip = textField_ip.getText();
        if (ip.trim().equals("")){
            new TipDialog(this,"错误","ip不能为空",true);
            return;
        }

        String port_str = textField_port.getText();
        if (port_str.trim().equals("")){
            new TipDialog(this,"错误","端口不能为空",true);
            return;
        }

        try{
            int port = Integer.valueOf(port_str);

            DataBaseConfig dataBaseConfig = new DataBaseConfig(comboBoxSelect, account, password, databaseName, ip, port);
            DataBaseManager.getInstance().linkDataBase(dataBaseConfig);

            new TipDialog(this,"提示","已成功连接到[" + databaseName + "]",true);
            close();
        }catch (Exception e){
            SfLog.getInstance().e(this.getClass(),e);
            new TipDialog(this,"错误",e.getMessage(),true);
            return;
        } catch (IllegalModeException e) {
            SfLog.getInstance().e(this.getClass(),e);
        }
    }

    /**
     * 账号输入框
     * @return
     */
    private JPanel getAccountPanel(){
        textField_account = new JTextField();
        JPanel panelAccount = new JPanel(new BorderLayout());
        JLabel label = new JLabel("账号：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);
        panel.add(textField_account);

        panelAccount.add(label,BorderLayout.WEST);
        panelAccount.add(panel,BorderLayout.CENTER);
        return panelAccount;
    }

    /**
     * 密码输入框
     * @return
     */
    private JPanel getPasswordPanel(){
        passwordField = new JPasswordField();
        JPanel panelPassword = new JPanel(new BorderLayout());

        JLabel label = new JLabel("密码：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);

        panel.add(passwordField);

        panelPassword.add(label,BorderLayout.WEST);
        panelPassword.add(panel,BorderLayout.CENTER);
        return panelPassword;
    }

    /**
     * 数据库名面板
     * @return
     */
    private JPanel getDatabasePanel(){
        textField_baseName = new JTextField();
        JPanel panelDatabase = new JPanel(new BorderLayout());

        JLabel label = new JLabel("库名：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);

        panel.add(textField_baseName);

        panelDatabase.add(label,BorderLayout.WEST);
        panelDatabase.add(panel,BorderLayout.CENTER);
        return panelDatabase;
    }

    /**
     * IP面板
     * @return
     */
    private JPanel getIpPanel(){
        textField_ip = new JTextField();
        JPanel panelIp = new JPanel(new BorderLayout());

        JLabel label = new JLabel("IP：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);

        panel.add(textField_ip);

        panelIp.add(label,BorderLayout.WEST);
        panelIp.add(panel,BorderLayout.CENTER);

        //输入限制
        textField_ip.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(!((keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) || keyChar == '.')){
                    e.consume(); //屏蔽掉非法输入
                }
            }
        });
        return panelIp;
    }

    /**
     * 端口面板
     * @return
     */
    private JPanel getPortPanel(){
        textField_port = new JTextField();
        JPanel panelPort = new JPanel(new BorderLayout());

        JLabel label = new JLabel("端口：");

        VFlowLayout flowLayout = new VFlowLayout();
        flowLayout.setHorizontalFill(true);
        JPanel panel = new JPanel(flowLayout);

        panel.add(textField_port);

        panelPort.add(label,BorderLayout.WEST);
        panelPort.add(panel,BorderLayout.CENTER);

        //输入限制
        textField_port.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(!(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)){
                    e.consume(); //屏蔽掉非法输入
                }
            }
        });
        return panelPort;
    }

    public void close(){
        setVisible(false);
        dispose();
    }
}
