package library.network.dto;

import java.io.Serializable;

/**
 * Created by Sergiu on 04.04.2016.
 */
public class BookQuantityDTO implements Serializable {
  private int bookId;
  private int newQuantity;

  public BookQuantityDTO(int bookId, int newQuantity) {
    this.bookId = bookId;
    this.newQuantity = newQuantity;
  }

  public int getNewQuantity() {
    return newQuantity;
  }

  public void setNewQuantity(int newQuantity) {
    this.newQuantity = newQuantity;
  }

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }
}
