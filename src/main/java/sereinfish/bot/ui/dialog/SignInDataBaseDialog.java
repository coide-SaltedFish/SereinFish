package sereinfish.bot.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 数据库登录窗口
 */
public class SignInDataBaseDialog extends JDialog {
    private JComboBox comboBox_state;//连接模式选择下拉框
    private JTextField textField_account;//账号
    private JPasswordField passwordField;//密码
    private JTextField textField_baseName;//数据库名
    private JTextField textField_ip;//ip
    private JTextField textField_port;//端口


    public SignInDataBaseDialog(Frame owner, boolean modal) {
        super(owner, "连接到数据库", modal);
    }

    /**
     * 构建面板
     * @return
     */
    public SignInDataBaseDialog build(){

        return this;
    }


}
