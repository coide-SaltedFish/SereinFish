package sereinfish.bot.ui.list.rcon;

import com.lowagie.text.Jpeg;
import lombok.Getter;
import lombok.Setter;
import sereinfish.bot.net.mc.rcon.RconConf;
import sereinfish.bot.net.mc.rcon.RconManager;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.RconListCellRenderer;
import sereinfish.bot.ui.list.model.RconListModel;

import javax.swing.*;
import java.awt.*;

/**
 * rcon列表
 */

@Getter
@Setter
public class RconList extends JPanel {
    private RconManager.Conf conf;
    private JList<RconConf> list;

    public RconList(RconManager.Conf conf){
        this.conf = conf;
        setLayout(new BorderLayout());
        list = new JList<>();
        add(list, BorderLayout.CENTER);
    }

    /**
     * 加载列表
     */
    public void load(){
        list.setModel(new RconListModel(conf.rconConfs));
        list.setCellRenderer(new RconListCellRenderer(new CellManager()));
        if (list.getModel().getSize() == 0){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.add(new JLabel("无数据"));
            add(panel, BorderLayout.CENTER);
        }else {
            add(list, BorderLayout.CENTER);
        }
    }

    /**
     * 得到选中项配置
     * @return
     */
    public RconConf getSelectConf(){
        int index = list.getSelectedIndex();
        return list.getModel().getElementAt(index);
    }
}
