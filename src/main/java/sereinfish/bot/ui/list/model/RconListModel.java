package sereinfish.bot.ui.list.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import sereinfish.bot.net.mc.rcon.RconConf;

import javax.swing.*;
import java.util.ArrayList;

@AllArgsConstructor
@Setter
public class RconListModel extends AbstractListModel {
    ArrayList<RconConf> rconConfs;

    @Override
    public int getSize() {
        return rconConfs.size();
    }

    @Override
    public Object getElementAt(int index) {
        return rconConfs.get(index);
    }
}
