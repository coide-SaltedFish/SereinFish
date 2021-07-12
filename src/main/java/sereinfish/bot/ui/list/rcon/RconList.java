package sereinfish.bot.ui.list.rcon;

import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.RconListCellRenderer;
import sereinfish.bot.ui.list.model.RconListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * rcon列表
 */

@Getter
@Setter
public class RconList extends JPanel {
    private RconManager.Conf conf;
    private JList<RconConf> list;
    private RconListModel model;

    public RconList(){
        setLayout(new BorderLayout());
        list = new JList<>();
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    /**
     * 加载列表
     */
    public void load(){
        conf = RconManager.readConf();
        if (model == null){
            list.setModel(new RconListModel(conf.getRconConfs()));
        }else {
            model.setRconConfs(conf.getRconConfs());
        }
        list.setCellRenderer(new RconListCellRenderer(new CellManager()));
        //更新控件
        list.validate();
        list.repaint();

        removeAll();
        if (list.getModel().getSize() == 0){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.add(new JLabel("无数据"));
            add(panel, BorderLayout.CENTER);
        }else {
            add(new JScrollPane(list), BorderLayout.CENTER);
        }
        validate();
        repaint();
    }

    /**
     * 设置列表监听事件
     * @param mouseAdapter
     */
    public void setListListener(MouseAdapter mouseAdapter){
        list.addMouseListener(mouseAdapter);
    }
}
