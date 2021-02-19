package sereinfish.bot.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 数据库登录窗口
 */
public class SignInDataBaseDialog extends JDialog {
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
