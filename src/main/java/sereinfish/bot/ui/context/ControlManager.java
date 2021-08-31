package sereinfish.bot.ui.context;

import sereinfish.bot.ui.context.entity.ConfControls;

import java.util.ArrayList;

public class ControlManager {
    private ArrayList<ConfControls.Control> controls = new ArrayList<>();
    private static ControlManager controlManager;

    private ControlManager(){

    }

    public static ControlManager getInstance(){
        if (controlManager == null){
            controlManager = new ControlManager();
        }
        return controlManager;
    }

    public void add(ConfControls.Control control){
        controls.add(control);
    }

    public ConfControls.Control getControl(long group, String groupName, String name){
        for (ConfControls.Control control:controls){
            if (control.getGroupConf().getGroup() == group && control.getName().equals(name) && control.getGroup().equals(groupName)){
                return control;
            }
        }
        return null;
    }

    public void updateControls(){
        for (ConfControls.Control control:controls){
            control.update();
        }
    }

    public void updateControl(long group, String groupName, String name){
        for (ConfControls.Control control:controls){
            if (control.getGroupConf().getGroup() == group && control.getName().equals(name) && control.getGroup().equals(groupName)){
                control.update();
            }
        }
    }
}
