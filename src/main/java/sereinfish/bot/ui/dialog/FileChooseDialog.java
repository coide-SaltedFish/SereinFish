package sereinfish.bot.ui.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileChooseDialog {
    private JFileChooser fileChooser;

    /**
     *
     * @param title
     * @param des           描述
     * @param listener
     * @param ext           筛选
     */
    public FileChooseDialog(String title, String des,  FileChooseListener listener, String... ext){
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(des, ext);
        fileChooser.setFileFilter(fileNameExtensionFilter);

        int returnVal = fileChooser.showOpenDialog(null);

        if (listener != null){
            switch (returnVal){
                case JFileChooser.APPROVE_OPTION:
                    listener.option(fileChooser.getSelectedFile());
                    break;
                case JFileChooser.CANCEL_OPTION:
                    listener.cancel();
                    break;
                case JFileChooser.ERROR_OPTION:
                    listener.error();
                    break;
            }
        }
    }

    public interface FileChooseListener{
        public void cancel();
        public void option(File f);
        public void error();
    }
}
