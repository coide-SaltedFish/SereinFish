package sereinfish.bot.database.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据表字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    String name();//字段名字
    String type();//字段类型
    int size() default -1;//字段长度
    boolean isNotNull();
    boolean isChar() default false;//是否是字符类型
    String default_() default "NULL";
}
