package sereinfish.bot.ui.panel.table.database;

import sereinfish.bot.mlog.SfLog;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DBTableModel<E> extends AbstractTableModel{

    String[] title;
    ArrayList<E> datas = new ArrayList<>();

    /**
     * 初始化数据
     */
    public DBTableModel(String[] title, ArrayList<E> datas) {
        this.title = title;
        this.datas = datas;
    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setData(ArrayList<E> datas) {
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
        E reply = datas.get(rowIndex);
        int i = 0;
        for (Field field:reply.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                if (i == columnIndex){
                    try {
                        return field.get(reply);
                    } catch (IllegalAccessException e) {
                        field.setAccessible(true);
                        try {
                            return field.get(reply);
                        } catch (IllegalAccessException illegalAccessException) {
                            SfLog.getInstance().e(this.getClass(),e);
                            return null;
                        }
                    }
                }
                i++;
            }
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }
}
