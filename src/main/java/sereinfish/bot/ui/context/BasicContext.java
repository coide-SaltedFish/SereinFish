package sereinfish.bot.ui.context;

import sereinfish.bot.ui.dialog.FileChooseDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

/**
 * 基本控件
 */
public class BasicContext {
    /**
     * 字体选择下拉框
     * @return
     */
    public static JPanel getFontComboBox(String title, String tip, String var, FontComboBoxListener listener){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(tip);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        boolean isSelect = false;
        for (String font:fonts){
            if (font.equals(var)){
                isSelect = true;
            }
            comboBox.addItem(font);
        }
        comboBox.addItem("选择字体文件");

        if (!isSelect){
            comboBox.addItem("文件：" + new File(var).getName());
        }
        comboBox.setSelectedItem("文件：" + new File(var).getName());

        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    Object selectItem = comboBox.getSelectedItem();

                    if (e.getItem().equals("选择字体文件")){
                        //弹出文件选择框
                        new FileChooseDialog("选择字体文件", ".ttf(字体文件)", new FileChooseDialog.FileChooseListener() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void option(File f) {
                                comboBox.removeItem(selectItem);
                                comboBox.addItem("文件：" + f.getName());
                                comboBox.setSelectedItem("文件：" + f.getName());
                                if (listener != null){
                                    try {
                                        listener.option(Font.createFont(Font.TRUETYPE_FONT, f));
                                    } catch (FontFormatException fontFormatException) {
                                        listener.error(fontFormatException);
                                    } catch (IOException ioException) {
                                        listener.error(ioException);
                                    }
                                }
                            }

                            @Override
                            public void error() {

                            }
                        }, "ttf");
                    }else {
                        listener.option(new Font((String) e.getItem(), Font.PLAIN, 24));
                    }
                }
            }
        });
        panel.add(comboBox);

        return panel;
    }

    /**
     * 字体选择框监听
     */
    public interface FontComboBoxListener{
        public void error(Exception e);
        public void option(Font font);
    }
}
