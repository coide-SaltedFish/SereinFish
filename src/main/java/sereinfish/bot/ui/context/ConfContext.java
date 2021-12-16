package sereinfish.bot.ui.context;

import com.icecreamqaq.yuq.entity.UserSex;
import sereinfish.bot.permissions.Permissions;
import sereinfish.bot.data.conf.ControlType;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.ui.context.entity.ConfControls;
import sereinfish.bot.ui.dialog.FileChooseDialog;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.frame.rcon.SelectRconFrame;
import sereinfish.bot.ui.textfield.plainDocument.NumberTextField;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

public class ConfContext {

    public static Component getContext(ConfControls.Control control){
        Component component = null;
        //如果是单选框
        if (control.getType() == ControlType.CheckBox){
            component = ConfContext.getCheckBox(control);
        }else if (control.getType() == ControlType.Edit){
            //如果是编辑框
            component = ConfContext.getEdit(control);
        }else if (control.getType() == ControlType.WebLink){
            //链接
            component = ConfContext.getWebLink(control);
        }else if(control.getType() == ControlType.Font_ComboBox){
            //字体选择下拉框
            component = ConfContext.getFontComboBox(control);
        }else if(control.getType() == ControlType.Edit_Small_Plain){
            //简单输入框
            component = ConfContext.EditSmallPlain(control);
        }else if(control.getType() == ControlType.Edit_IntNum){
            //整数输入框
            component = ConfContext.getEditIntNum(control);
        }else if (control.getType() == ControlType.SelectRcon){
            component = ConfContext.getRconSelect(control);
        }else if (control.getType() == ControlType.Authority_ComboBox){
            component = ConfContext.getAuthorityComboBox(control);
        }else if (control.getType() == ControlType.SelectSex){
            component = ConfContext.getSelectSex(control);
        }

        return component;
    }

    /**
     * 得到性别选择器
     * @param control
     * @return
     */
    public static JPanel getSelectSex(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(control.getTip());

        //初始化列表
        comboBox.addItem("无条件");
        comboBox.addItem(UserSex.none.name());
        comboBox.addItem(UserSex.man.name());
        comboBox.addItem(UserSex.woman.name());

        comboBox.setSelectedItem(control.getValue());

        control.setListener(control1 -> {
            comboBox.setSelectedItem(control.getValue());
        });

        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    control.setValue(e.getItem());
                }
            }
        });

        panel.add(comboBox);
        return panel;
    }

    /**
     * 得到单选框
     * @param control
     * @return
     */
    public static JCheckBox getCheckBox(ConfControls.Control control){
        final JCheckBox checkBox = new JCheckBox(control.getName());
        checkBox.setToolTipText(control.getTip());//设置提示
        checkBox.setSelected(control.getValue());//设置值

        control.setListener(control1 -> {
            checkBox.setSelected(control1.getValue());//设置值
        });
        //设置监听
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                control.setValue(checkBox.isSelected());
            }
        });
        return checkBox;
    }

    /**
     * 得到编辑框
     * @param control
     * @return
     */
    public static JButton getEdit(ConfControls.Control control){
        JButton button = new JButton(control.getName());
        button.setToolTipText(control.getTip());

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditFrame editFrame = new EditFrame(control.getName(), new EditFrame.EditListener() {
                    @Override
                    public void save(EditFrame editFrame, String text) {
                        control.setValue(text);
                        editFrame.close();
                    }

                    @Override
                    public void cancel(EditFrame editFrame) {
                        editFrame.close();
                    }
                });
                editFrame.setText(control.getValue());
                editFrame.setVisible(true);
            }
        });
        return button;
    }

    /**
     * 网页链接
     * @param control
     * @return
     */
    public static JButton getWebLink(ConfControls.Control control){
        JButton button = new JButton(control.getName());
        button.setToolTipText(control.getTip());
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Desktop desktop=Desktop.getDesktop();
                String url = control.getValue();
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
     * @param control
     * @return
     */
    public static JPanel getFontComboBox(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        final JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(control.getTip());

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = graphicsEnvironment.getAvailableFontFamilyNames();
        boolean isSelect = false;//是否已设置值
        for (String font:fonts){
            comboBox.addItem(font);
            if (font.equals(control.getValue())){
                isSelect = true;
                comboBox.setSelectedItem(font);
            }
        }
        comboBox.addItem("选择字体文件");

        if (!isSelect) {
            comboBox.addItem("文件：" + new File((String) control.getValue()).getName());
            comboBox.setSelectedItem("文件：" + new File((String) control.getValue()).getName());
        }

        control.setListener(control1 -> {
            boolean isSelects = false;//是否已设置值
            for (String font:fonts){
                comboBox.addItem(font);
                if (font.equals(control.getValue())){
                    isSelects = true;
                    comboBox.setSelectedItem(font);
                }
            }
            comboBox.addItem("选择字体文件");

            if (!isSelects) {
                comboBox.addItem("文件：" + new File((String) control.getValue()).getName());
                comboBox.setSelectedItem("文件：" + new File((String) control.getValue()).getName());
            }
        });

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
                                control.setValue(f.getAbsolutePath());
                            }

                            @Override
                            public void error() {

                            }
                        }, "ttf");
                    }else {
                        control.setValue(e.getItem());
                    }
                }
            }
        });
        panel.add(comboBox);

        return panel;
    }

    /**
     * 权限选择框
     * @return
     */
    public static JPanel getAuthorityComboBox(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        final JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(control.getTip());

        //初始化列表
        for (Map.Entry<String, Integer> entry: Permissions.AuthorityList.entrySet()){
            comboBox.addItem(entry.getKey());
        }

        comboBox.setSelectedItem(Permissions.getInstance().getAuthorityName(control.getValue()));

        control.setListener(control1 -> {
            comboBox.setSelectedItem(Permissions.getInstance().getAuthorityName(control1.getValue()));
        });

        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    control.setValue(Permissions.AuthorityList.get(e.getItem()));
                }
            }
        });

        panel.add(comboBox);
        return panel;
    }

    /**
     * 简单输入框
     * @param control
     * @return
     */
    public static JPanel EditSmallPlain(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        final JTextField textField = new JTextField();
        textField.setColumns(25);
        textField.setToolTipText(control.getTip());
        textField.setText(control.getValue().toString());

        control.setListener(control1 -> {
            textField.setText(control1.getValue().toString());
            panel.setBorder(BorderFactory.createTitledBorder(control.getName()));
            panel.requestFocus();
        });

        //保存事件
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER ){
                    //保存内容
                    control.setValue(textField.getText());
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
     * @param control
     * @return
     */
    public static JPanel getEditIntNum(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        final JTextField textField = new JTextField();
        textField.setColumns(10);
        textField.setToolTipText(control.getTip());
        textField.setDocument(new NumberTextField(9));

        int var = control.getValue();
        textField.setText(var + "");

        control.setListener(control1 -> {
            textField.setText(control1.getValue() + "");
            panel.setBorder(BorderFactory.createTitledBorder(control.getName()));
            panel.requestFocus();
        });

        //保存事件
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER ){
                    //保存内容
                    String text = textField.getText();
                    int var = 0;

                    try{
                        var = Integer.valueOf(text);
                        control.setValue(var);
                        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));
                        panel.requestFocus();
                    }catch (Exception e1){
                        panel.setBorder(BorderFactory.createTitledBorder("[输入错误]" + control.getName()));
                    }
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
     * Rcon选择
     * @param control
     * @return
     */
    public static JPanel getRconSelect(ConfControls.Control control){
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        final JButton button = new JButton("选择");
        panel.add(button);
        button.setToolTipText(control.getTip());

        RconConf rconConf = control.getValue();
        if (rconConf != null){
            button.setText(rconConf.getName());
        }

        control.setListener(control1 -> {
            RconConf conf = control.getValue();
            if (conf != null){
                button.setText(conf.getName());
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectRconFrame("选择Rcon", new SelectRconFrame.SelectListener() {
                    @Override
                    public void select(RconConf conf) {
                        control.setValue(conf);
                        button.setText(conf.getName());
                    }

                    @Override
                    public void clean() {
                        control.setValue(null);
                        button.setText("选择");
                    }
                });
            }
        });
        return panel;
    }


}