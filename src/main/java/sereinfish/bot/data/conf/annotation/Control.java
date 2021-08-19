package sereinfish.bot.data.conf.annotation;

import sereinfish.bot.data.conf.ControlType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Control {
    String group();//控件所在组
    String name();//控件名称
    ControlType type();//控件类型
    String tip() default "";//控件tip
}
