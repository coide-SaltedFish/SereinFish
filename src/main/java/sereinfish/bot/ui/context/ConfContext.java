package sereinfish.bot.ui.context;

import sereinfish.bot.authority.AuthorityManagement;
import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlType;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.net.mc.rcon.Rcon;
import sereinfish.bot.net.mc.rcon.RconConf;
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
import java.util.Map;

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
        }else if(control.getType() == GroupControlType.Edit_IntNum){
            //整数输入框
            component = ConfContext.getEditIntNum(conf, control);
        }else if (control.getType() == GroupControlType.SelectRcon){
            component = ConfContext.getRconSelect(conf, control);
        }else if (control.getType() == GroupControlType.Authority_ComboBox){
            component = ConfContext.getAuthorityComboBox(conf, control);
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
        boolean isSelect = false;
        for (String font:fonts){
            if (font.equals(control.getValue())){
                isSelect = true;
            }
            comboBox.addItem(font);
        }
        comboBox.addItem("选择字体文件");

        if (!isSelect){
            comboBox.addItem("文件：" + new File((String) control.getValue()).getName());
        }
        comboBox.setSelectedItem("文件：" + new File((String) control.getValue()).getName());

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
                                GroupConfManager.getInstance().put(conf);
                            }

                            @Override
                            public void error() {

                            }
                        }, "ttf");
                    }else {
                        control.setValue(e.getItem());
                        GroupConfManager.getInstance().put(conf);
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
    public static JPanel getAuthorityComboBox(GroupConf conf, GroupConf.Control control){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        JComboBox comboBox = new JComboBox();
        comboBox.setToolTipText(control.getTip());

        //初始化列表
        for (Map.Entry<String, Integer> entry: AuthorityManagement.AuthorityList.entrySet()){
            comboBox.addItem(entry.getKey());
        }
        int var = AuthorityManagement.NORMAL;
        if (control.getValue() instanceof Integer){
            var = (int) control.getValue();
        }else {
            var = Double.valueOf(control.getValue().toString()).intValue();
        }


        comboBox.setSelectedItem(AuthorityManagement.getInstance().getAuthorityName(var));

        //设置点击事件
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    control.setValue(AuthorityManagement.AuthorityList.get(e.getItem()));
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
        textField.setColumns(25);
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

        JTextField textField = new JTextField();
        textField.setColumns(10);
        textField.setToolTipText(control.getTip());
        textField.setDocument(new NumberTextField(9));

        Object object = control.getValue();
        try{
            if (object instanceof Double){
                double dVar = Double.valueOf((Double) object);
                int var = (int) dVar;
                textField.setText(var + "");
            }else if(object instanceof Integer){
                int var = (int) object;
                textField.setText(var + "");
            }
        }catch (Exception e){
            panel.setBorder(BorderFactory.createTitledBorder("[数据源错误]" + control.getName()));
            textField.setText(0 + "");
        }

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
                        GroupConfManager.getInstance().put(conf);
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
     * @param groupConf
     * @param control
     * @return
     */
    public static JPanel getRconSelect(GroupConf groupConf, GroupConf.Control control){
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(control.getName()));

        JButton button = new JButton("选择");
        panel.add(button);
        button.setToolTipText(control.getTip());
        if (control.getValue() != null){
            RconConf rconConf = MyYuQ.toClass((String) control.getValue(), RconConf.class);
            if (rconConf != null){
                button.setText(rconConf.getName());
            }
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectRconFrame("选择Rcon", new SelectRconFrame.SelectListener() {
                    @Override
                    public void select(RconConf conf) {
                        control.setValue(MyYuQ.toJson(conf, RconConf.class));
                        GroupConfManager.getInstance().put(groupConf);
                        button.setText(conf.getName());
                    }

                    @Override
                    public void clean() {
                        control.setValue("");
                        GroupConfManager.getInstance().put(groupConf);
                        button.setText("选择");
                    }
                });
            }
        });
        return panel;
    }


}