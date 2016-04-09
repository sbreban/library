package library.client.gui;

import library.model.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BooksTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;
  private List<Book> books;
  private String[] columns;

  public BooksTableModel(boolean showAvailable) {
    this.books = new ArrayList<>();
    if (showAvailable) {
      columns = new String[]{"ID","Author", "Name", "Available"};
    } else {
      columns = new String[]{"ID","Author", "Name"};
    }
  }

  @Override
  public String getColumnName(int column) {
    return columns[column];
  }

  public int getRowCount() {
    return books.size();
  }

  public int getColumnCount() {
    return columns.length;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    switch (columnIndex){
      case 0: return books.get(rowIndex).getId();
      case 1: return books.get(rowIndex).getAuthor();
      case 2: return books.get(rowIndex).getTitle();
      case 3: return books.get(rowIndex).getAvailable();
    }
    return null;
  }

  public void setBooks(List<Book> books) {
    this.books = books;
    fireTableDataChanged();
  }

  public void addBook(Book book) {
    this.books.add(book);
    fireTableDataChanged();
  }

  public Book get(int index) {
    return books.get(index);
  }

  public Book getById(int bookId) {
    for (Book book : books) {
      if (book.getId() == bookId) {
        return book;
      }
    }
    return null;
  }

  public void removeById(int bookId) {
    Book toRemove = null;
    for (Book book : books) {
      if (book.getId() == bookId) {
        toRemove = book;
        break;
      }
    }
    if (toRemove != null) {
      books.remove(toRemove);
    }
    fireTableDataChanged();
  }

}
