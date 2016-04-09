package library.persistence.repository.book;

import library.model.Book;

import java.util.List;

/**
 * Created by Sergiu on 03.04.2016.
 */
public interface BookRepository {
  List<Book> getAvailableBooks();
  List<Book> getUserBooks(int userId);
  List<Book> searchBooks(String key);
  int borrowBook(int userId, int bookId);
  Book returnBook(int userId, int bookId);
}
