package sereinfish.bot.ui.panel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MyEditorPanel extends JTextPane {
    public static final String keyWords[] = {"<qq>","<name>","<Rain:Image:{*}.*>"};

    public MyEditorPanel() {
    }

    public class SyntaxHighlighter implements DocumentListener{

        @Override
        public void insertUpdate(DocumentEvent e) {

        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }
    }
}
