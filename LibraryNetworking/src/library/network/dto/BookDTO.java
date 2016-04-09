package library.network.dto;

import java.io.Serializable;

/**
 * Created by Sergiu on 05.04.2016.
 */
public class BookDTO implements Serializable {
  private int id;
  private String author, title;

  public BookDTO(int id, String author, String title) {
    this.id = id;
    this.author = author;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
