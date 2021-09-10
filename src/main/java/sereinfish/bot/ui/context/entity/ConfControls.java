package sereinfish.bot.ui.context.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sereinfish.bot.data.conf.ControlType;
import sereinfish.bot.data.conf.annotation.Control;
import sereinfish.bot.data.conf.entity.GroupConf;
import sereinfish.bot.mlog.SfLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class ConfControls {
    GroupConf groupConf;
    Map<String, ArrayList<Control>> confControlMap = new LinkedHashMap<>();

    public ConfControls(GroupConf groupConf){
        this.groupConf = groupConf;
        analysis();
    }

    /**
     * 解析配置，生成控件列表
     */
    private void analysis(){
        //遍历参数列表
        for (Field field:groupConf.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(sereinfish.bot.data.conf.annotation.Control.class)){
                sereinfish.bot.data.conf.annotation.Control control = field.getAnnotation(sereinfish.bot.data.conf.annotation.Control.class);
                if (!confControlMap.containsKey(control.group())){
                    confControlMap.put(control.group(), new ArrayList<>());
                }
                confControlMap.get(control.group()).add(new Control(null, field, groupConf, control.group(), control.name(), control.type(), control.tip()));
            }
        }
    }

    /**
     * 单个控件
     */
    @AllArgsConstructor
    @Getter
    public static class Control{

        ChangeValueListener listener;//监听器

        Field field;
        GroupConf groupConf;
        String group = "";//所属组
        String name = "";//名称
        ControlType type;
        String tip;

        public void setValue(Object value){
            try {
                field.set(groupConf, value);

            } catch (IllegalAccessException e) {
                field.setAccessible(true);
                try {
                    field.set(groupConf, value);
                } catch (IllegalAccessException illegalAccessException) {
                    SfLog.getInstance().e(this.getClass(), illegalAccessException);
                }
            }
            groupConf.save();
            if (listener != null){
                listener.change(this);
            }
        }

        public <T> T getValue(){
            try {
                return (T) field.get(groupConf);
            } catch (IllegalAccessException e) {
                field.setAccessible(true);
                try {
                    return (T) field.get(groupConf);
                } catch (IllegalAccessException illegalAccessException) {
                    SfLog.getInstance().e(this.getClass(), e);
                }
                SfLog.getInstance().e(this.getClass(), e);
            }
            return null;
        }

        public void setListener(ChangeValueListener listener) {
            this.listener = listener;
        }

        public void update(){
            if (listener != null){
                listener.change(this);
            }
        }

        /**
         * 值变化监听器
         */
        public interface ChangeValueListener{
            public void change(Control control);
        }
    }
}
