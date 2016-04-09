package library.persistence;

import library.persistence.repository.book.BookRepository;
import library.persistence.repository.book.mock.BookRepositoryMock;
import library.persistence.repository.user.UserRepository;
import library.persistence.repository.user.mock.UserRepositoryMock;

public class MockPersistence extends Persistence {

  public UserRepository createUserRepository() {
    System.out.println("Mock user repository created ");
    return new UserRepositoryMock();
  }

  @Override
  public BookRepository createBookRepository() {
    System.out.println("Mock book repository created ");
    return new BookRepositoryMock();
  }
}
