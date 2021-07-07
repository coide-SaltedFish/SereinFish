package sereinfish.bot.ui.textfield.plainDocument;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 只能输入IP工具类
 */
public class IPTextField extends PlainDocument {

    private int limit;
    public IPTextField(int limit) {
        super();
        this.limit = limit;
    }
    public void insertString
            (int offset, String str, AttributeSet attr)
            throws BadLocationException {
        if (str == null){
            return;
        }
        if ((getLength() + str.length()) <= limit) {

            char[] upper = str.toCharArray();
            int length=0;
            for (int i = 0; i < upper.length; i++) {
                //限制在0-9
                if ((upper[i]>='0' && upper[i]<='9') || upper[i] == '.'){
                    upper[length++] = upper[i];
                }
            }
            super.insertString(offset, new String(upper,0,length), attr);
        }
    }
}