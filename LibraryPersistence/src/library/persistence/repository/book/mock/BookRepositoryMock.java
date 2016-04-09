package library.persistence.repository.book.mock;

import library.model.Book;
import library.persistence.repository.book.BookRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergiu on 03.04.2016.
 */
public class BookRepositoryMock implements BookRepository {

  private List<Book> allBooks;

  public BookRepositoryMock() {
    allBooks = new ArrayList<>();
    populateBooks();
  }

  private void populateBooks() {
    Book book1 = new Book(1,"Sagan, Carl","Cosmos",2);
    Book book2 = new Book(2,"Burgess, Anthony","A Clockwork Orange",1);
    Book book3 = new Book(3,"Bukowski, Charles","Ham on Rye",1);
    Book book4 = new Book(4,"Wilde, Oscar","The Picture of Dorian Gray",4);
    Book book5 = new Book(5,"Baudelaire, Charles","Les Fleurs du Mal",2);

    allBooks.add(book1);
    allBooks.add(book2);
    allBooks.add(book3);
    allBooks.add(book4);
    allBooks.add(book5);
  }

  @Override
  public List<Book> getAvailableBooks() {
    return allBooks;
  }

  @Override
  public List<Book> getUserBooks(int userId) {
    List<Book> userBooks = new ArrayList<>();
    userBooks.add(allBooks.get(1));
    userBooks.add(allBooks.get(3));
    return userBooks;
  }

  @Override
  public List<Book> searchBooks(String key) {
    return null;
  }

  @Override
  public int borrowBook(int userId, int bookId) {
    return 0;
  }

  @Override
  public Book returnBook(int userId, int bookId) {
    return null;
  }
}
