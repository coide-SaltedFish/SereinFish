package sereinfish.bot.ui.panel.job;

import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.job.JobSFManager;
import sereinfish.bot.job.MyJob;
import sereinfish.bot.job.conf.JobConf;
import sereinfish.bot.job.ex.MessageJobIllegalException;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.dialog.TipDialog;
import sereinfish.bot.ui.frame.MainFrame;
import sereinfish.bot.ui.frame.job.InsertJobFrame;
import sereinfish.bot.ui.list.CellManager;
import sereinfish.bot.ui.list.cellRenderer.JobListCellRenderer;
import sereinfish.bot.ui.list.model.JobListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * 定时任务面板
 */
public class JobPanel extends JPanel {
    private GroupConf conf;
    private JobConf jobConf;

    private JList<MyJob> jobList;//任务列表

    public JobPanel(GroupConf conf) {
        this.conf = conf;
        try {
            jobConf = JobConf.getGroupJobs(conf.getGroup());
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "定时任务配置读取失败：" + conf.getGroup(),e);
            jobConf = new JobConf(conf.getGroup());
        }

        build();//界面生成
    }

    /**
     * 生成面板界面
     */
    private void build(){
        setLayout(new BorderLayout());

        //已注册任务列表
        JPanel panel_list = new JPanel(new BorderLayout());
        jobList = new JList<>();
        panel_list.add(jobList, BorderLayout.CENTER);

        add(new JScrollPane(panel_list), BorderLayout.CENTER);
        //按钮
        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btn_add = new JButton("添加");
        JButton btn_delete = new JButton("删除");
        JButton btn_change = new JButton("修改");

        btn_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InsertJobFrame(conf.getGroup(), new InsertJobFrame.InsertListener() {
                    @Override
                    public void save(InsertJobFrame frame, MyJob myJob) {

                        //添加到job管理器
                        try {
                            JobSFManager.getInstance().add(conf.getGroup(), myJob);
                            //添加到配置
                            jobConf.addJob(myJob);
                        } catch (JobSFManager.JobNotFindException jobNotFindException) {
                            SfLog.getInstance().e(this.getClass(), jobNotFindException);
                            new TipDialog(MainFrame.getMainFrame(),"错误",jobNotFindException.getMessage(),true);
                        } catch (MessageJobIllegalException messageJobIllegalException) {
                            SfLog.getInstance().e(this.getClass(), messageJobIllegalException);
                            new TipDialog(MainFrame.getMainFrame(),"错误", messageJobIllegalException.getMessage(),true);
                        }
                        loadList();
                        frame.setVisible(false);
                        frame.dispose();
                    }

                    @Override
                    public void cancel(InsertJobFrame frame) {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }).setVisible(true);
            }
        });

        btn_delete.addActionListener(e->{
                    int reCode = JOptionPane.showOptionDialog(MainFrame.getMainFrame(),"将会删除选中计划任务，是否继续","警告",JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE, null,new String[]{"确定","取消"},null);
                    if (reCode == 0) {
                        //得到job信息
                        MyJob myJob = jobList.getSelectedValue();
                        //注册取消
                        JobSFManager.getInstance().delete(conf.getGroup(), myJob.getId());
                        SfLog.getInstance().w(this.getClass(), "定时任务注册取消：" + myJob.getName());
                        //配置删除
                        jobConf.deleteJob(myJob.getId());
                        SfLog.getInstance().w(this.getClass(), "定时任务配置删除：" + myJob.getName());
                        loadList();
                    }
        });

        panel_btn.add(btn_add);
        panel_btn.add(btn_delete);
        //TODO:panel_btn.add(btn_change);
        add(panel_btn, BorderLayout.SOUTH);

        loadList();
    }

    /**
     * 列表加载
     */
    private void loadList(){
        jobList.setModel(new JobListModel(JobSFManager.getInstance().getGroupJobList(conf.getGroup())));
        jobList.setCellRenderer(new JobListCellRenderer(new CellManager()));
        jobList.setSelectedIndex(0);
        jobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
