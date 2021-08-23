package sereinfish.bot.ui.frame.database.insert;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.frame.EditFrame;
import sereinfish.bot.ui.layout.VFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 插入窗口
 */
public class InsertFrame<E> extends JFrame {
    private Class t;
    private JPanel contentPane;
    private InsertListener listener;
    private ArrayList<JTextPane> textPanes = new ArrayList<>();
    private E value;

    public InsertFrame(String title, Class t, InsertListener listener){
        super("插入_" + title);
        this.t = t;
        this.listener = listener;

        build();
    }

    public InsertFrame(String title, Class t, E value, InsertListener listener) throws HeadlessException {
        super("插入_" + title);
        this.t = t;
        this.listener = listener;
        this.value = value;

        build();
    }

    private void build(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 440, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        //解析部件
        VFlowLayout vFlowLayout = new VFlowLayout();
        vFlowLayout.setHorizontalFill(true);
        JPanel panel_edit = new JPanel(vFlowLayout);
        contentPane.add(new JScrollPane(panel_edit),BorderLayout.CENTER);

        for (Field field:t.getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                sereinfish.bot.database.dao.annotation.Field dField = field.getAnnotation(sereinfish.bot.database.dao.annotation.Field.class);
                JPanel panel = new JPanel(new BorderLayout());
                JPanel panel_e = new JPanel(new VFlowLayout());
                JTextPane textPane = new JTextPane();
                panel_e.add(new JScrollPane(textPane));
                //编辑框
                JButton btn_edit = new JButton("文本编辑器");
                btn_edit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        EditFrame editFrame = new EditFrame(dField.name(), new EditFrame.EditListener() {
                            @Override
                            public void save(EditFrame editFrame, String text) {
                                textPane.setText(text);
                                editFrame.close();
                            }

                            @Override
                            public void cancel(EditFrame editFrame) {
                                editFrame.close();
                            }
                        });
                        editFrame.setText(textPane.getText());
                        editFrame.setVisible(true);
                    }
                });

                JLabel label = new JLabel(dField.name());
                textPanes.add(textPane);
                panel.add(label,BorderLayout.WEST);
                panel.add(panel_e,BorderLayout.CENTER);
                panel.add(btn_edit, BorderLayout.EAST);

                panel_edit.add(panel);
            }
        }
        //赋值
        if (value != null){
            int i = 0;
            for (Field field:value.getClass().getDeclaredFields()){
                if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                    try {
                        textPanes.get(i).setText(field.get(value) + "");
                    } catch (IllegalAccessException e) {
                        field.setAccessible(true);
                        try {
                            textPanes.get(i).setText(field.get(value) + "");
                        } catch (IllegalAccessException illegalAccessException) {
                            SfLog.getInstance().e(this.getClass(),illegalAccessException);
                        }
                    }
                    i++;
                }
            }
        }

        //按钮
        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(panel_btn,BorderLayout.SOUTH);
        JButton btn_save = new JButton("确定");
        panel_btn.add(btn_save);
        btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                E value = null;
                try {
                    value = (E) t.newInstance();
                    //赋值
                    int i = 0;
                    for (Field field:value.getClass().getDeclaredFields()){
                        if (field.isAnnotationPresent(sereinfish.bot.database.dao.annotation.Field.class)){
                            String str = textPanes.get(i).getText();

                            try {
                               if (field.getType().isAssignableFrom(String.class)){
                                   field.set(value,str);
                               }else {
                                   field.set(value, MyYuQ.toClass(str,field.getType()));
                               }
                            } catch (IllegalAccessException e) {
                                field.setAccessible(true);
                                if (field.getType().isAssignableFrom(String.class)){
                                    field.set(value,str);
                                }else {
                                    field.set(value, MyYuQ.toClass(str,field.getType()));
                                }
                            }
                            i++;
                        }
                    }
                } catch (InstantiationException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                } catch (IllegalAccessException e) {
                    SfLog.getInstance().e(this.getClass(),e);
                }

                listener.save(InsertFrame.this,value);
            }
        });

        JButton btn_cancel = new JButton("取消");
        panel_btn.add(btn_cancel);
        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.cancel(InsertFrame.this);
            }
        });
    }

    public void close(){
        setVisible(false);
        dispose();
    }

    public interface InsertListener<E>{
        public void save(InsertFrame frame, E value);
        public void cancel(InsertFrame frame);
    }
}
