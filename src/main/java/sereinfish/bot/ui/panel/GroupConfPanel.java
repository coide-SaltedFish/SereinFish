package sereinfish.bot.ui.panel;

import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.entity.conf.*;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.frame.database.select.SelectDataBaseFrame;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 群配置面板
 */
public class GroupConfPanel extends JPanel {
    public static final int NOW_V = 5;

    private GroupConf conf;
    private JPanel contentPane;

    public GroupConfPanel(GroupConf conf){
        this.conf = conf;
        contentPane = new JPanel();
        setLayout(new BorderLayout());
        add(new JScrollPane(contentPane));

        if (conf.getV() < NOW_V){
            conf.update();
            conf.setV(NOW_V);
        }
        build();
    }

    /**
     * 解析绘制面板
     */
    private void build(){
        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        contentPane.setLayout(vFlowLayout);
        //启用
        contentPane.add(enablePanel());

        //数据库选择框
        contentPane.add(comboBoxPanel());

        for (Map.Entry<String, Map<GroupControlId, GroupConf.Control>> entry:conf.getConfMaps().entrySet()){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(BorderFactory.createTitledBorder(entry.getKey()));
            //解析组件
            for (Map.Entry<GroupControlId, GroupConf.Control> entry1:entry.getValue().entrySet()){
                GroupConf.Control control = entry1.getValue();
                //如果是单选框
                if (control.getType() == GroupControlType.CheckBox){
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
                    panel.add(checkBox);
                }else if (control.getType() == GroupControlType.Edit){
                    //如果是编辑框
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
                    panel.add(button);
                }else if (control.getType() == GroupControlType.Button){
                    //按钮
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
                    panel.add(button);
                }
            }
            contentPane.add(panel);
        }
    }

    public JPanel enablePanel(){
        JPanel enablePanle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox("启用");
        checkBox.setToolTipText("启用此群");//设置提示
        checkBox.setSelected((Boolean) conf.isEnable());//设置值
        //设置监听
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                conf.setEnable(checkBox.isSelected());
                GroupConfManager.getInstance().put(conf);
            }
        });
        enablePanle.add(checkBox);
        return enablePanle;
    }

    /**
     * 下拉框面板
     * @return
     */
    public JPanel comboBoxPanel(){
        JPanel comboBox_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btn_dataBase = new JButton("选择数据库");
        if (conf.getDataBaseConfig() != null){
            btn_dataBase.setText("数据库：" + conf.getDataBaseConfig().getBaseName());
        }

        comboBox_panel.add(btn_dataBase);
        btn_dataBase.setToolTipText("点击选择此群数据库");

        btn_dataBase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectDataBaseFrame(new SelectDataBaseFrame.SelectDataBaseListener() {
                    @Override
                    public void select(SelectDataBaseFrame frame, DataBase dataBase) {
                        conf.setDataBaseConfig(dataBase.getDataBaseConfig());
                        GroupConfManager.getInstance().put(conf);
                        if (conf.getDataBase() != null){
                            btn_dataBase.setText("数据库：" + conf.getDataBaseConfig().getBaseName());
                        }else {
                            btn_dataBase.setText("选择数据库");
                        }
                        frame.close();
                    }

                    @Override
                    public void cancel(SelectDataBaseFrame frame) {
                        frame.close();
                    }

                    @Override
                    public void close(SelectDataBaseFrame frame){
                        conf.setDataBaseConfig(null);
                        if (conf.getDataBase() != null){
                            btn_dataBase.setText("数据库：" + conf.getDataBaseConfig().getBaseName());
                        }else {
                            btn_dataBase.setText("选择数据库");
                        }
                        frame.close();
                    }
                });
            }
        });

        JButton btn_rcon = new JButton("RCON");
        comboBox_panel.add(btn_rcon);
        btn_rcon.setToolTipText("点击选择此群RCON");


        return comboBox_panel;
    }
}
