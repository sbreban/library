package library.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Sergiu on 04.04.2016.
 */
public class JDBCUtils {

  private static Connection instance=null;

  private static Connection getNewConnection(){
    String driver=System.getProperty("driver");
    String url=System.getProperty("url");
    String user=System.getProperty("username");
    String pass=System.getProperty("password");
    Connection con=null;
    try {
      Class.forName(driver);
      con= DriverManager.getConnection(url,user,pass);
    } catch (ClassNotFoundException e) {
      System.out.println("Error loading driver "+e);
    } catch (SQLException e) {
      System.out.println("Error getting connection "+e);
    }
    return con;
  }

  public static Connection getConnection(){
    try {
      if (instance==null || instance.isClosed())
        instance=getNewConnection();

    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return instance;
  }
}
