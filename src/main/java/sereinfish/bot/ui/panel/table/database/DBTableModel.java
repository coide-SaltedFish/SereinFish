package sereinfish.bot.ui.panel.table.database;

import sereinfish.bot.mlog.SfLog;

import javax.persistence.Column;
import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DBTableModel<E> extends AbstractTableModel{

    String[] title;
    List<E> datas;
    Class t;

    /**
     * 初始化数据
     */
    public DBTableModel(Class t, List<E> datas) {
        this.t = t;
        this.datas = datas;
        title = getTitle();
    }

    @Override
    public Class getColumnClass(int column) {
        Class returnValue;
        if ((column >= 0) && (column < getColumnCount())) {
            returnValue = getValueAt(0, column).getClass();
        } else {
            returnValue = Object.class;
        }
        return returnValue;
    }

    public E getRows(int rows){
        return datas.get(rows);
    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setData(List<E> datas) {
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
        if (!(rowIndex < datas.size())){
            return null;
        }

        if (datas.size() <= 0){
            return null;
        }
        E reply = datas.get(rowIndex);
        int i = 0;
        for (Field field:reply.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(Column.class)){
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

    /**
     * 得到标题
     * @return
     */
    private String[] getTitle(){
        ArrayList<String> list = new ArrayList<>();
        for (Field field:t.getDeclaredFields()){
            if (field.isAnnotationPresent(Column.class)){
                list.add(field.getName());
            }
        }
        return list.toArray(new String[0]);
    }
}