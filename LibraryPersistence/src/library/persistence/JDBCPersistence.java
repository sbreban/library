package library.persistence;

import library.persistence.repository.book.BookRepository;
import library.persistence.repository.book.jdbc.BookRepositoryJDBC;
import library.persistence.repository.user.UserRepository;
import library.persistence.repository.user.jdbc.UserRepositoryJDBC;

public class JDBCPersistence extends Persistence {

  public UserRepository createUserRepository() {
    System.out.println("JDBC user repository created ");
    return new UserRepositoryJDBC();
  }

  @Override
  public BookRepository createBookRepository() {
    System.out.println("JDBC book repository created ");
    return new BookRepositoryJDBC();
  }
}
