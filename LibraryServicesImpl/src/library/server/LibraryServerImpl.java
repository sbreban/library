package library.server;

import library.model.Book;
import library.model.User;
import library.persistence.Persistence;
import library.persistence.repository.book.BookRepository;
import library.persistence.repository.user.UserRepository;
import library.services.ILibraryClient;
import library.services.ILibraryServer;
import library.services.LibraryException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryServerImpl implements ILibraryServer {
  private UserRepository userRepository;
  private BookRepository bookRepository;
  private Map<Integer, ILibraryClient> loggedClients;

  public LibraryServerImpl() {
    userRepository= Persistence.getInstance().createUserRepository();
    bookRepository= Persistence.getInstance().createBookRepository();
    loggedClients=new ConcurrentHashMap<>();
  }

  public synchronized User login(String userName, String password, ILibraryClient client) throws LibraryException {
    User user = userRepository.verifyUser(userName, password);
    if (user != null){
      if(loggedClients.get(user.getId()) != null)
        throw new LibraryException("User already logged in.");
      loggedClients.put(user.getId(), client);
    } else
      throw new LibraryException("Authentication failed.");
    return user;
  }

  public synchronized void logout(int userId, ILibraryClient client) throws LibraryException {
    ILibraryClient localClient = loggedClients.remove(userId);
    if (localClient == null)
      throw new LibraryException("User "+userId+" is not logged in.");
  }

  @Override
  public List<Book> getAvailableBooks() throws LibraryException {
    return bookRepository.getAvailableBooks();
  }

  @Override
  public List<Book> getUserBooks(int userId) throws LibraryException {
    return bookRepository.getUserBooks(userId);
  }

  @Override
  public List<Book> searchBooks(String key) throws LibraryException {
    return bookRepository.searchBooks(key);
  }

  @Override
  public void borrowBook(int userId, int bookId) throws LibraryException {
    int newQuantity = bookRepository.borrowBook(userId, bookId);
    for (ILibraryClient libraryClient : loggedClients.values()) {
      libraryClient.bookUpdated(bookId, newQuantity);
    }
  }

  @Override
  public void returnBook(int userId, int bookId) throws LibraryException {
    Book returned = bookRepository.returnBook(userId, bookId);
    for (ILibraryClient libraryClient : loggedClients.values()) {
      libraryClient.bookReturned(returned.getId(), returned.getAuthor(), returned.getTitle());
    }
  }

  private boolean isLogged(User u){
    return loggedClients.get(u.getUserName())!=null;
  }
}
