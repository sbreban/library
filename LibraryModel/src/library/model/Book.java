package library.model;

import java.io.Serializable;

/**
 * Created by Sergiu on 03.04.2016.
 */
public class Book implements Serializable {

  private String author, title;
  private int id, available;

  public Book(int id, String author, String title, int available) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.available = available;
  }

  public String getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public int getId() {
    return id;
  }

  public int getAvailable() {
    return available;
  }

  public void setAvailable(int available) {
    this.available = available;
  }

  @Override
  public String toString() {
    return "Book [author=" + author + ", title=" + title + ", id=" + id
        + ", available=" + available + "]";
  }
}
