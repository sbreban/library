package library.services;

import library.model.Book;
import library.model.User;

import java.util.List;

public interface ILibraryServer {
  User login(String userName, String password, ILibraryClient client) throws LibraryException;
  void logout(int userId, ILibraryClient client) throws LibraryException;
  List<Book> getAvailableBooks() throws LibraryException;
  List<Book> getUserBooks(int userId) throws LibraryException;
  List<Book> searchBooks(String key) throws LibraryException;
  void borrowBook(int userId, int bookId) throws LibraryException;
  void returnBook(int userId, int bookId) throws LibraryException;
}
