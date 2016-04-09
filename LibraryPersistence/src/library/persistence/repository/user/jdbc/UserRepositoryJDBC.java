package library.persistence.repository.user.jdbc;

import library.model.User;
import library.persistence.JDBCUtils;
import library.persistence.repository.user.UserRepository;

import java.sql.*;

public class UserRepositoryJDBC implements UserRepository {

  public User verifyUser(String userName, String password) {
    System.out.println("Jdbc verify user");
    Connection connection = JDBCUtils.getConnection();
    User user = null;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from users where user_name=? and password=?");
      preparedStatement.setString(1,userName);
      preparedStatement.setString(2,password);
      ResultSet result=preparedStatement.executeQuery();
      if (result.next()) {
        user = new User(result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
      }
      System.out.println("Verify user "+userName);
      return user;
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return user;
  }
}
