package sereinfish.bot.ui.frame;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.DataBaseManager;
import sereinfish.bot.database.entity.DataBase;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.DataBaseListCellRenderer;
import sereinfish.bot.ui.list.model.DataBaseListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectDataBaseFrame extends JFrame {
    private JPanel contentPane;
    private JList dataBaseList;
    private SelectDataBaseListener listener;

    public SelectDataBaseFrame(SelectDataBaseListener listener){
        this.listener = listener;
        setTitle("选择数据库");
        build();
    }

    private void build(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 370, 441);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //按钮
        JButton btn_reFlash = new JButton("刷新");
        contentPane.add(btn_reFlash,BorderLayout.NORTH);
        btn_reFlash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadList();
            }
        });

        //列表
        dataBaseList = new JList();
        contentPane.add(new JScrollPane(dataBaseList),BorderLayout.CENTER);

        //按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(btnPanel,BorderLayout.SOUTH);
        JButton btn_select = new JButton("选择");
        btn_select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dataBaseList.isSelectionEmpty()){
                    listener.select(SelectDataBaseFrame.this,(DataBase) dataBaseList.getSelectedValue());
                }
            }
        });
        btn_select.setEnabled(false);
        JButton btn_cancel = new JButton("取消");
        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.cancel(SelectDataBaseFrame.this);
            }
        });

        JButton btn_close = new JButton("清除");
        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.close(SelectDataBaseFrame.this);
            }
        });

        btnPanel.add(btn_select);
        btnPanel.add(btn_close);
        btnPanel.add(btn_cancel);

        //列表监听
        dataBaseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btn_select.setEnabled(!dataBaseList.isSelectionEmpty());
            }
        });

        loadList();
        setVisible(true);
    }

    private void loadList(){
        dataBaseList.setModel(new DataBaseListModel(DataBaseManager.getInstance().getDataBases()));
        dataBaseList.setCellRenderer(new DataBaseListCellRenderer(new CellManager()));
        dataBaseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void close(){
        setVisible(false);
        dispose();
    }

    /**
     * 监听器
     */
    public interface SelectDataBaseListener{
        public void select(SelectDataBaseFrame frame, DataBase dataBase);
        public void cancel(SelectDataBaseFrame frame);
        public void close(SelectDataBaseFrame frame);
    }
}
