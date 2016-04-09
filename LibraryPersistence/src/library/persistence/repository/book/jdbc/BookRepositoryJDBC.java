package library.persistence.repository.book.jdbc;

import library.model.Book;
import library.persistence.JDBCUtils;
import library.persistence.repository.book.BookRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergiu on 03.04.2016.
 */
public class BookRepositoryJDBC implements BookRepository {
  @Override
  public List<Book> getAvailableBooks() {
    System.out.println("Load available books");
    Connection connection = JDBCUtils.getConnection();
    List<Book> availableBooks = new ArrayList<>();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from books where available>0");
      ResultSet result=preparedStatement.executeQuery();
      while (result.next()) {
        availableBooks.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
      }
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return availableBooks;
  }

  @Override
  public List<Book> getUserBooks(int userId) {
    System.out.println("Load user's books");
    Connection connection = JDBCUtils.getConnection();
    List<Book> usersBooks = new ArrayList<>();
    try {
      String sql = "select * from books b " +
          "inner join user_book ub on ub.book_id=b.id " +
          "inner join users u on u.id=ub.user_id " +
          "where u.id="+userId;
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      ResultSet result=preparedStatement.executeQuery();
      while (result.next()) {
        usersBooks.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
      }
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return usersBooks;
  }

  @Override
  public List<Book> searchBooks(String key) {
    System.out.println("Search books");
    Connection connection = JDBCUtils.getConnection();
    List<Book> foundBooks = new ArrayList<>();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from books where title like '%"+key+"%'");
      ResultSet result=preparedStatement.executeQuery();
      while (result.next()) {
        foundBooks.add(new Book(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4)));
      }
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return foundBooks;
  }

  @Override
  public int borrowBook(int userId, int bookId) {
    System.out.println("Borrow book");
    Connection connection = JDBCUtils.getConnection();
    int quantity = 0;
    try {
      String changeQuantity = "update books set available=available-1 where id=?";
      PreparedStatement changeQuantityStatement = connection.prepareStatement(changeQuantity);
      changeQuantityStatement.setInt(1, bookId);
      changeQuantityStatement.executeUpdate();
      String borrow = "insert into user_book values(?,?)";
      PreparedStatement borrowStatement = connection.prepareStatement(borrow);
      borrowStatement.setInt(1, userId);
      borrowStatement.setInt(2, bookId);
      borrowStatement.executeUpdate();
      PreparedStatement availableQuantityStatement = connection.prepareStatement("select available from books where id=?");
      availableQuantityStatement.setInt(1, bookId);
      ResultSet resultSet = availableQuantityStatement.executeQuery();
      if (resultSet.next()) {
        quantity = resultSet.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return quantity;
  }

  @Override
  public Book returnBook(int userId, int bookId) {
    System.out.println("Return book");
    Connection connection = JDBCUtils.getConnection();
    Book returned = null;
    try {
      String changeQuantity = "update books set available=available+1 where id=?";
      PreparedStatement changeQuantityStatement = connection.prepareStatement(changeQuantity);
      changeQuantityStatement.setInt(1, bookId);
      changeQuantityStatement.executeUpdate();
      String returnBook = "delete from user_book where book_id=? and user_id=?";
      PreparedStatement returnStatement = connection.prepareStatement(returnBook);
      returnStatement.setInt(1, bookId);
      returnStatement.setInt(2, userId);
      returnStatement.executeUpdate();
      PreparedStatement selectReturnedBook = connection.prepareStatement("select * from books where id=?");
      selectReturnedBook.setInt(1, bookId);
      ResultSet resultSet = selectReturnedBook.executeQuery();
      if (resultSet.next()) {
        returned = new Book(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4));
      }
    } catch (SQLException e) {
      System.out.println("Error DB "+e);
    }
    return returned;
  }
}
