package library.client.gui;

import library.model.Book;
import library.model.User;
import library.services.ILibraryClient;
import library.services.ILibraryServer;
import library.services.LibraryException;

import javax.swing.table.TableModel;
import java.util.List;

public class LibraryClientController implements ILibraryClient {

  private BooksTableModel availableBooksTableModel;
  private BooksTableModel yourBooksTableModel;
  private ILibraryServer libraryServer;
  private User user;

  public LibraryClientController(ILibraryServer libraryServer) {
    this.libraryServer = libraryServer;
    availableBooksTableModel = new BooksTableModel(true);
    yourBooksTableModel = new BooksTableModel(false);
  }

  public void login(String userName, String password) throws LibraryException {
    User user = libraryServer.login(userName, password, this);
    this.user = user;
    loadAvailableBooks();
    List<Book> userBooks = libraryServer.getUserBooks(user.getId());
    yourBooksTableModel.setBooks(userBooks);
  }

  public void logout() {
    try {
      libraryServer.logout(user.getId(), this);
    } catch (LibraryException e) {
      System.out.println("Logout error "+e);
    }
  }

  public TableModel getAvailableBooksTableModel(){
    return availableBooksTableModel;
  }

  public void loadAvailableBooks() throws LibraryException {
    List<Book> availableBooks = libraryServer.getAvailableBooks();
    availableBooksTableModel.setBooks(availableBooks);
  }

  public BooksTableModel getYourBooksTableModel() {
    return yourBooksTableModel;
  }

  public void borrowBook(int bookId) throws LibraryException {
    libraryServer.borrowBook(user.getId(), bookId);
  }

  public void returnBook(int bookId) throws LibraryException {
    libraryServer.returnBook(user.getId(), bookId);
  }

  public void searchBooks(String key) throws LibraryException {
    List<Book> foundBooks = libraryServer.searchBooks(key);
    availableBooksTableModel.setBooks(foundBooks);
  }

  @Override
  public void bookUpdated(int bookId, int newQuantity) throws LibraryException {
    if (newQuantity == 0) {
      availableBooksTableModel.removeById(bookId);
    } else {
      availableBooksTableModel.getById(bookId).setAvailable(newQuantity);
      availableBooksTableModel.fireTableDataChanged();
    }
  }

  @Override
  public void bookReturned(int bookId, String author, String title) throws LibraryException {
    Book returnedBook = availableBooksTableModel.getById(bookId);
    if (returnedBook == null) {
      returnedBook = new Book(bookId, author, title, 1);
      availableBooksTableModel.addBook(returnedBook);
    } else {
      returnedBook.setAvailable(returnedBook.getAvailable()+1);
      availableBooksTableModel.fireTableDataChanged();
    }
  }
}
