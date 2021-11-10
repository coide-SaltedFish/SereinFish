package sereinfish.bot.ui.panel.global.table;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DBTableModel extends AbstractTableModel {

    String[] title;
    ArrayList<ArrayList<String>> datas = new ArrayList<>();

    /**
     * 初始化数据
     */
    public DBTableModel(String[] title, ArrayList<ArrayList<String>> datas) {
        this.title = title;
        this.datas = datas;
    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setData(ArrayList<ArrayList<String>> datas) {
        this.datas = datas;
    }

    @Override
    public int getRowCount() {
        return datas.size();
    }

    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return datas.get(rowIndex).get(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }
}