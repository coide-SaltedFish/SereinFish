package sereinfish.bot.ui.context;

import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlType;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ConfContext {

    public static Component getContext(GroupConf conf, GroupConf.Control control){
        Component component = null;
        //如果是单选框
        if (control.getType() == GroupControlType.CheckBox){
            component = ConfContext.getCheckBox(conf, control);
        }else if (control.getType() == GroupControlType.Edit){
            //如果是编辑框
            component = ConfContext.getEdit(conf, control);
        }else if (control.getType() == GroupControlType.WebLink){
            //链接
            component = ConfContext.getWebLink(conf, control);
        }else if(control.getType() == GroupControlType.Font_ComboBox){
            //字体选择下拉框
            component = ConfContext.getFontComboBox(conf, control);
        }else if(control.getType() == GroupControlType.Edit_Small_Plain){
            //简单输入框
            component = ConfContext.EditSmallPlain(conf, control);
        }

        return component;
    }

    /**
     * 得到单选框
     * @param conf
     * @param control
     * @return
     */
    public static JCheckBox getCheckBox(GroupConf conf, GroupConf.Control control){
        JCheckBox checkBox = new JCheckBox(control.getName());
        checkBox.setToolTipText(control.getTip());//设置提示
        checkBox.setSelected((Boolean) control.getValue());//设置值
        //设置监听
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                control.setValue(checkBox.isSelected());
                GroupConfManager.getInstance().put(conf);
            }
        });
        return checkBox;
    }

    /**
     * 得到编辑框
     * @param conf
     * @param control
     * @return
     */
    public static JButton getEdit(GroupConf conf, GroupConf.Control control){
        JButton button = new JButton(control.getName());
        button.setToolTipText(control.getTip());

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditFrame editFrame = new EditFrame(control.getName(), new EditFrame.EditListener() {
                    @Override
                    public void save(EditFrame editFrame, String text) {
                        control.setValue(text);
                        GroupConfManager.getInstance().put(conf);
                        editFrame.close();
                    }

                    @Override
                    public void cancel(EditFrame editFrame) {
                        editFrame.close();
                    }
                });
                editFrame.setText((String) control.getValue());
                editFrame.setVisible(true);
            }
        });
        return button;
    }

    /**
     * 网页链接
     * @param conf
     * @param control
     * @return
     */
    public static JButton getWebLink(GroupConf conf, GroupConf.Control control){
        JButton button = new JButton(control.getName());
        button.setToolTipText(control.getTip());
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //放着先
                Desktop desktop=Desktop.getDesktop();
                String url = (String) control.getValue();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(), url, e);
                } catch (URISyntaxException e) {
                    SfLog.getInstance().e(this.getClass(), url, e);
                }
            }
        });
        return button;
    }

    /**
     * 字体选择下拉框
     * @param conf
     * @param control
     * @return
     */
    public static JPanel getFontComboBox(GroupConf conf, GroupConf.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(control.getTip());

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String font:fonts){
            comboBox.addItem(font);
        }
        comboBox.setSelectedItem(control.getValue());
        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    control.setValue(e.getItem());
                    GroupConfManager.getInstance().put(conf);
                }
            }
        });
        panel.add(comboBox);

        return panel;
    }

    /**
     * 简单输入框
     * @param conf
     * @param control
     * @return
     */
    public static JPanel EditSmallPlain(GroupConf conf, GroupConf.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        JTextField textField = new JTextField();
        textField.setToolTipText(control.getTip());
        textField.setText(control.getValue().toString());
        //保存事件
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER ){
                    //保存内容
                    control.setValue(textField.getText());
                    GroupConfManager.getInstance().put(conf);
                    panel.setBorder(BorderFactory.createTitledBorder(control.getName()));
                    panel.requestFocus();
                }
            }
        });
        //内容变化事件
        Document document = textField.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (textField.getText().equals(control.getValue())){
                    panel.setBorder(BorderFactory.createTitledBorder(control.getName()));
                }else {
                    panel.setBorder(BorderFactory.createTitledBorder("*" + control.getName()));
                }
            }
        });

        panel.add(textField);
        return panel;
    }

    /**
     * 得到整数编辑框
     * @param conf
     * @param control
     * @return
     */
    public static JPanel getEditIntNum(GroupConf conf, GroupConf.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        return panel;
    }
}
