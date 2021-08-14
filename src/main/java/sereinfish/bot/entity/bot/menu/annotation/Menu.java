package sereinfish.bot.entity.bot.menu.annotation;

import sereinfish.bot.authority.AuthorityManagement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {
    Type type() default Type.ALL;
    String name() default "菜单列表分支";
    int permissions() default AuthorityManagement.NORMAL;//权限

    public enum Type{
        GROUP,
        PRIVATE,
        ALL
    }
}