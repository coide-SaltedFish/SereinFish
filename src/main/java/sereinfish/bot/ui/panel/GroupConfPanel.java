package sereinfish.bot.ui.panel;

import sereinfish.bot.entity.conf.GroupConf;
import sereinfish.bot.entity.conf.GroupConfManager;
import sereinfish.bot.entity.conf.GroupControlType;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 群配置面板
 */
public class GroupConfPanel extends JPanel {
    private GroupConf conf;

    public GroupConfPanel(GroupConf conf){
        this.conf = conf;
        build();
    }

    /**
     * 解析绘制面板
     */
    private void build(){
        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        setLayout(vFlowLayout);

        for (Map.Entry<String, ArrayList<GroupConf.Control>> entry:conf.getConfMaps().entrySet()){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(BorderFactory.createTitledBorder(entry.getKey()));
            //解析组件
            for (GroupConf.Control control:entry.getValue()){
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

                    //TODO:文本编辑界面

                    panel.add(button);
                }
            }
            add(panel);
        }
    }
}
