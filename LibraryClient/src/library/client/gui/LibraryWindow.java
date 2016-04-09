package library.client.gui;

import library.model.Book;
import library.services.LibraryException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LibraryWindow extends JFrame{
  private JTable availableBooks;
  private JTable yourBooks;
  private TextField searchTextField;
  private LibraryClientController libraryClientController;

  public LibraryWindow(String title, LibraryClientController libraryClientController){
    super(title);
    this.libraryClientController = libraryClientController;
    JPanel panel=new JPanel(new GridLayout(1,2));
    panel.add(createAllBooks());
    panel.add(createYourBooks());
    getContentPane().add(panel);
    addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        close();
      }
    });
  }

  private void close(){
    libraryClientController.logout();
  }

  private JPanel createAllBooks(){
    JPanel jPanel=new JPanel(new BorderLayout());
    jPanel.setBorder(BorderFactory.createTitledBorder("Available books"));

    availableBooks=new JTable(libraryClientController.getAvailableBooksTableModel());
    JScrollPane scroll=new JScrollPane(availableBooks);
    jPanel.add(scroll, BorderLayout.CENTER);

    JPanel southPanel = new JPanel(new GridLayout(2,1));
    jPanel.add(southPanel, BorderLayout.SOUTH);

    JPanel searchPanel = new JPanel(new GridLayout(1,3));
    southPanel.add(searchPanel);

    searchTextField = new TextField();
    searchPanel.add(searchTextField);

    Button searchButton = new Button("Search");
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          libraryClientController.searchBooks(searchTextField.getText());
        } catch (LibraryException exception) {
          JOptionPane.showMessageDialog(LibraryWindow.this, exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    searchPanel.add(searchButton);

    Button clearButton = new Button("Clear");
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          clearSearch();
        } catch (LibraryException exception) {
          JOptionPane.showMessageDialog(LibraryWindow.this, exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    searchPanel.add(clearButton);

    Button borrowButton = new Button("Borrow book");
    borrowButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int row = availableBooks.getSelectedRow();
        int alreadyBorrowed = yourBooks.getRowCount();
        if (alreadyBorrowed >= 3) {
          JOptionPane.showMessageDialog(LibraryWindow.this, "Already borrowed 3 books!", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
          if (row >= 0) {
            BooksTableModel availableBooksTableModel = (BooksTableModel) availableBooks.getModel();
            Book selectedBook = availableBooksTableModel.get(availableBooks.convertRowIndexToModel(row));
            BooksTableModel yourBooksTableModel = (BooksTableModel) yourBooks.getModel();
            if (yourBooksTableModel.getById(selectedBook.getId()) != null) {
              JOptionPane.showMessageDialog(LibraryWindow.this, "You already have this book!", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {
              selectedBook.setAvailable(selectedBook.getAvailable() - 1);
              try {
                libraryClientController.borrowBook(selectedBook.getId());
                BooksTableModel yoursBooksTableModel = (BooksTableModel) yourBooks.getModel();
                yoursBooksTableModel.addBook(selectedBook);
                clearSearch();
              } catch (LibraryException exception) {
                JOptionPane.showMessageDialog(LibraryWindow.this, exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
              }
            }
          }
        }
      }
    });
    southPanel.add(borrowButton);

    return jPanel;
  }

  private void clearSearch() throws LibraryException {
    searchTextField.setText("");
    libraryClientController.loadAvailableBooks();
  }

  private JPanel createYourBooks(){
    JPanel jPanel=new JPanel(new BorderLayout());
    yourBooks=new JTable(libraryClientController.getYourBooksTableModel());
    JScrollPane scroll=new JScrollPane(yourBooks);
    jPanel.add(scroll, BorderLayout.CENTER);
    jPanel.setBorder(BorderFactory.createTitledBorder("Your books"));
    Button returnButton = new Button("Return book");
    returnButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int row = yourBooks.getSelectedRow();
        if (row >= 0) {
          BooksTableModel yourBooksTableModel = (BooksTableModel) yourBooks.getModel();
          Book selectedBook = yourBooksTableModel.get(yourBooks.convertRowIndexToModel(row));
          try {
            libraryClientController.returnBook(selectedBook.getId());
            BooksTableModel yoursBooksTableModel = (BooksTableModel) yourBooks.getModel();
            yoursBooksTableModel.removeById(selectedBook.getId());
            clearSearch();
          } catch (LibraryException exception) {
            JOptionPane.showMessageDialog(LibraryWindow.this, exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
    jPanel.add(returnButton, BorderLayout.SOUTH);
    return jPanel;
  }

}
