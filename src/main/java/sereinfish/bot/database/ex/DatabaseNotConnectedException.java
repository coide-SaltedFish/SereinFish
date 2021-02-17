package sereinfish.bot.database.ex;

public class DatabaseNotConnectedException extends Throwable{
    public DatabaseNotConnectedException(String m){
        super(m);
    }
}
