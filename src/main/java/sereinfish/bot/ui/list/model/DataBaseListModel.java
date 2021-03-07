package sereinfish.bot.ui.list.model;

import sereinfish.bot.database.entity.DataBase;

import javax.swing.*;
import java.util.ArrayList;

public class DataBaseListModel extends AbstractListModel {
    ArrayList<DataBase> dataBases;

    public DataBaseListModel(ArrayList<DataBase> dataBases) {
        this.dataBases = dataBases;
    }

    @Override
    public int getSize() {
        return dataBases.size();
    }

    @Override
    public Object getElementAt(int index) {
        return dataBases.get(index);
    }
}
