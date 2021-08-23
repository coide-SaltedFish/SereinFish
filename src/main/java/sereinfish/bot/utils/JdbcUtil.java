package sereinfish.bot.utils;

import sereinfish.bot.mlog.SfLog;

import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcUtil {

    /**
     * 为方便单测，设一个开关
     */
    public static boolean IS_DB_CONNECTION_AUTOCLOSE = true;

    /**
     * 是否关闭日志打印以便提高性能
     */
    public static boolean isClosePrintRealSql = false;

    /**
     * 打印真实 SQL 执行语句
     *
     * @param sql    SQL 语句，可以带有 ? 的占位符
     * @param params 插入到 SQL 中的参数，可单个可多个可不填
     * @return 实际 sql 语句
     */
    public static String printRealSql(String sql, Object[] params) {
        if (sql == null || sql.equals(""))
            throw new IllegalArgumentException("SQL 语句不能为空！");

        if (isClosePrintRealSql)
            return null;

        if (params == null || params.length == 0) // 完整的 SQL 无须填充
            return sql;

        if (!match(sql, params))
            SfLog.getInstance().d(JdbcUtil.class, "SQL 语句中的占位符与值参数（个数上）不匹配。SQL：{0}，\nparams:{1}\n"
                    + sql + "\n" + Arrays.toString(params));

        if (sql.endsWith("?"))
            sql += " ";

        String[] arr = sql.split("\\?");

        for (int i = 0; i < arr.length - 1; i++) {
            Object value = params[i];
            String inSql;

            if (value instanceof Date) // 只考虑了字符串、布尔、数字和日期类型的转换
                inSql = "'" + value + "'";
            else if (value instanceof String)
                inSql = "'" + value + "'";
            else if (value instanceof Boolean)
                inSql = (Boolean) value ? "1" : "0";
            else
                inSql = value.toString();// number

            arr[i] = arr[i] + inSql;
        }

        return String.join(" ", arr).trim();
    }

    /**
     * ? 和参数的实际个数是否匹配
     *
     * @param sql    SQL 语句，可以带有 ? 的占位符
     * @param params 插入到 SQL 中的参数，可单个可多个可不填
     * @return true 表示为 ? 和参数的实际个数匹配
     */
    private static boolean match(String sql, Object[] params) {
        if (params == null || params.length == 0)
            return true; // 没有参数，完整输出

        Matcher m = Pattern.compile("(\\?)").matcher(sql);
        int count = 0;
        while (m.find())
            count++;

        return count == params.length;
    }

    /**
     * 简单格式化 SQL，当前对 SELECT 语句有效
     *
     * @param sql SELECT 语句
     * @return 美化后的 SQL
     */
    public static String formatSql(String sql) {
        String separator = System.getProperty("line.separator");
        sql = '\t' + sql;
        sql = sql.replaceAll("(?i)SELECT\\s+", "SELECT "); // 统一大写
        sql = sql.replaceAll("\\s+(?i)FROM", separator + "\tFROM");
        sql = sql.replaceAll("\\s+(?i)WHERE", separator + "\tWHERE");
        sql = sql.replaceAll("\\s+(?i)GROUP BY", separator + "\tGROUP BY");
        sql = sql.replaceAll("\\s+(?i)ORDER BY", separator + "\tORDER BY");
        sql = sql.replaceAll("\\s+(?i)LIMIT", separator + "\tLIMIT");
        sql = sql.replaceAll("\\s+(?i)DESC", " DESC");
        sql = sql.replaceAll("\\s+(?i)ASC", " ASC");

        return sql;
    }

}
