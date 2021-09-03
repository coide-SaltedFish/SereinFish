package sereinfish.bot.ui.list.cellRenderer;

import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.job.entity.JobType;
import sereinfish.bot.myYuq.MyYuQ;
import sereinfish.bot.ui.list.CellManager;

import javax.swing.*;
import java.awt.*;

public class JobListCellRenderer implements ListCellRenderer {
    private CellManager cellManager;

    public JobListCellRenderer(CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        MyJob myJob = (MyJob) value;
        String id = myJob.getId();

        if (cellManager.exist(id)){
            JPanel jPanel = cellManager.get(id);
            //选中颜色设置
            jPanel.setBackground(SystemColor.control);

            if(isSelected) {
                jPanel.setBackground(SystemColor.textHighlight);
            }else {
                jPanel.setBackground(SystemColor.control);
            }
            return jPanel;
        }

        JPanel jPanel = new JPanel(new BorderLayout());
        //名字
        JLabel label_name = new JLabel(myJob.getName());
        label_name.setOpaque(false);
        label_name.setFont(new Font("微软雅黑", Font.BOLD, 22));
        jPanel.add(label_name, BorderLayout.CENTER);
        //类型
        JLabel label_type = new JLabel("类型：" + JobType.getName(myJob.getType()));
        label_type.setOpaque(false);
        //时间
        JLabel label_time = new JLabel("时间：" + myJob.getAtTime());
        label_time.setOpaque(false);

        JPanel panel_type = new JPanel(new FlowLayout());
        panel_type.setOpaque(false);
        panel_type.add(label_type);
        panel_type.add(label_time);
        jPanel.add(panel_type, BorderLayout.NORTH);

        //id
        JLabel label_id = new JLabel("ID：" + myJob.getId());
        label_id.setOpaque(false);
        jPanel.add(label_id, BorderLayout.SOUTH);

        //选中颜色设置
        jPanel.setBackground(SystemColor.control);

        if(isSelected) {
            jPanel.setBackground(SystemColor.textHighlight);
        }else {
            jPanel.setBackground(SystemColor.control);
        }

        cellManager.add(id,jPanel);
        return jPanel;
    }
}
