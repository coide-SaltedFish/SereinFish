package sereinfish.bot.entity.bot.menu.annotation;

import sereinfish.bot.permissions.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuItem {
    String name() default "指令名称";
    String usage() default "./";//命令格式
    String description() default "描述";
    int permission() default Permissions.NORMAL;//权限
}
