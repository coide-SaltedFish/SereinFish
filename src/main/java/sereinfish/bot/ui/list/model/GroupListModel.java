package sereinfish.bot.ui.list.model;

import com.icecreamqaq.yuq.entity.Group;

import javax.swing.*;
import java.util.ArrayList;

/**
 * 群组model
 */
public class GroupListModel extends AbstractListModel {
    ArrayList<Group> groups;

    public GroupListModel(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int getSize() {
        return groups.size();
    }

    @Override
    public Object getElementAt(int index) {
        return groups.get(index);
    }

    public void setDate(ArrayList<Group> groups){
        this.groups = groups;
    }
}
