package sereinfish.bot.ui.panel;

import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.ui.context.ConfContext;
import sereinfish.bot.ui.context.entity.ConfControls;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 群配置面板
 */
public class GroupConfPanel extends JPanel {

    private GroupConf conf;
    private JPanel contentPane;

    public GroupConfPanel(GroupConf conf){
        this.conf = conf;
        contentPane = new JPanel();
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(contentPane,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(scrollBar.getMaximum() / 4);
        add(scrollPane);

        build();
    }

    /**
     * 解析绘制面板
     */
    private void build(){
        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        contentPane.setLayout(vFlowLayout);

        ConfControls confControls = new ConfControls(conf);

        for (Map.Entry<String, ArrayList<ConfControls.Control>> entry:confControls.getConfControlMap().entrySet()){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(BorderFactory.createTitledBorder(entry.getKey()));
            //解析组件
            for (ConfControls.Control control:entry.getValue()){
                Component component = ConfContext.getContext(control);
                if(component != null){
                    panel.add(component);
                }
            }
            contentPane.add(panel);
        }
    }
}
