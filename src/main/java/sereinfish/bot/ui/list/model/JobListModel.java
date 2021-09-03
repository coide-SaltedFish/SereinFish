package sereinfish.bot.ui.list.model;

import sereinfish.bot.job.MyJob;

import javax.swing.*;
import java.util.ArrayList;

public class JobListModel extends AbstractListModel {
    ArrayList<MyJob> myJobs;

    public JobListModel(ArrayList<MyJob> myJobs) {
        this.myJobs = myJobs;
    }

    @Override
    public int getSize() {
        return myJobs.size();
    }

    @Override
    public Object getElementAt(int index) {
        return myJobs.get(index);
    }
}
