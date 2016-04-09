package library.network.dto;

import java.io.Serializable;

/**
 * Created by Sergiu on 04.04.2016.
 */
public class UserBookDTO implements Serializable {
  private int userId;
  private int bookId;

  public UserBookDTO(int userId, int bookId) {
    this.userId = userId;
    this.bookId = bookId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }
}
