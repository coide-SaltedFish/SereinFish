package sereinfish.bot.ui.frame;

import sereinfish.bot.ui.panel.MyEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditFrame extends JFrame {
    private JPanel contentPane;
    private MyEditorPanel editorPane;
    private EditListener editListener;
    private JButton btn_save,btn_cancel;

    public EditFrame(String name,EditListener listener){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 784, 441);
        setLocationRelativeTo(null);

        this.editListener = listener;

        setTitle("文本编辑器-" + name);
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        editorPane = new MyEditorPanel(MyEditorPanel.MODE_RAIN);
        contentPane.add(editorPane,BorderLayout.CENTER);

        btn_save = new JButton("保存");
        btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.save(EditFrame.this,editorPane.getText());
            }
        });
        btn_cancel = new JButton("取消");
        btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.cancel(EditFrame.this);
            }
        });

        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel_btn.add(btn_save);
        panel_btn.add(btn_cancel);
        contentPane.add(panel_btn,BorderLayout.SOUTH);

        //设置窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });
    }

    /**
     * 窗口监听事件
     */
    public interface EditListener{
        public void save(EditFrame editFrame, String text);//保存
        public void cancel(EditFrame editFrame);
    }

    public String getText(){
        return editorPane.getText();
    }

    public void setText(String s){
        editorPane.setText(s);
    }

    public void close(){
        setVisible(false);
        dispose();
    }
}
