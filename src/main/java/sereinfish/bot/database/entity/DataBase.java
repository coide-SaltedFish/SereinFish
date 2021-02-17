package sereinfish.bot.database.entity;

import sereinfish.bot.database.DataBaseConfig;
import sereinfish.bot.database.ex.IllegalModeException;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.log.SfLog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static sereinfish.bot.database.DataBaseConfig.*;

/**
 * 数据库类
 */
public class DataBase {
    private SfLog log = SfLog.getInstance();
    private DataBaseConfig dataBaseConfig;

    private Connection connection;
    private Statement statement;

    public DataBase(DataBaseConfig dataBaseConfig) throws SQLException, ClassNotFoundException, IllegalModeException {
        this.dataBaseConfig = dataBaseConfig;
        switch (dataBaseConfig.getState()){
            case SQL_SERVER:
                linkSQLServer();
                break;
            case MY_SQL:
                linkMySQL();
                break;
            case SQLITE:
                linkSqlite();
                break;
            default:
                throw new IllegalModeException("错误的数据库连接模式：" + dataBaseConfig.getState());
        }
    }

    /**
     * 连接到SQL Server
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void linkSQLServer() throws ClassNotFoundException, SQLException {
        log.d(this.getClass(),"加载数据库驱动");
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        log.d(this.getClass(),"完成");

        //数据库连接
        log.d(this.getClass(),"连接到数据库");
        //jdbc:sqlserver://localhost:1433;DatabaseName=name"
        connection = DriverManager.getConnection("jdbc:sqlserver://" + dataBaseConfig.getIp() + ":" + dataBaseConfig.getPort() +
                ";DatabaseName=" + dataBaseConfig.getBaseName(),dataBaseConfig.getAccount(), dataBaseConfig.getPassword());
        statement = connection.createStatement();
        log.d(this.getClass(),"成功");
    }

    /**
     * 连接到MySQL
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void linkMySQL() throws ClassNotFoundException, SQLException {
        log.d(this.getClass(),"加载数据库驱动");
        Class.forName("com.mysql.cj.jdbc.Driver");
        log.d(this.getClass(),"完成");

        //数据库连接
        log.d(this.getClass(),"连接到数据库");
        //jdbc:sqlserver://localhost:1433;DatabaseName=name"
        connection = DriverManager.getConnection("jdbc:mysql://" + dataBaseConfig.getIp() + ":" + dataBaseConfig.getPort() + "/" + dataBaseConfig.getBaseName() +
                "?allowPublicKeyRetrieval=true&serverTimezone=UTC", dataBaseConfig.getAccount(), dataBaseConfig.getPassword());
        statement = connection.createStatement();
        log.d(this.getClass(),"成功");
    }

    /**
     * 连接到sqlite
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void linkSqlite() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        log.d(this.getClass(),"成功加载sqlite数据库驱动");

        File dataBaseFile = new File(FileHandle.dataBasePath,dataBaseConfig.getBaseName() + ".db");//生成数据库路径
        if (!dataBaseFile.getParentFile().exists()){
            dataBaseFile.getParentFile().mkdirs();
        }
        String url = "jdbc:sqlite:" + dataBaseFile;
        connection = DriverManager.getConnection(url);
        log.d(this.getClass(),"sqlite数据库连接完成");
        statement = connection.createStatement();
    }

    /**
     * 数据库关闭
     */
    public void close() throws SQLException {
        statement.close();
        connection.close();
    }

    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
