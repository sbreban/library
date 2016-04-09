package library.network.rpcprotocol;

import library.model.Book;
import library.model.User;
import library.network.dto.BookDTO;
import library.network.dto.BookQuantityDTO;
import library.network.dto.UserBookDTO;
import library.network.dto.UserDTO;
import library.services.ILibraryClient;
import library.services.ILibraryServer;
import library.services.LibraryException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LibraryServerRpcProxy implements ILibraryServer {
  private String host;
  private int port;

  private ILibraryClient client;

  private ObjectInputStream input;
  private ObjectOutputStream output;
  private Socket connection;

  private BlockingQueue<Response> qresponses;
  private volatile boolean finished;

  public LibraryServerRpcProxy(String host, int port) {
    this.host = host;
    this.port = port;
    qresponses=new LinkedBlockingQueue<>();
  }

  public User login(String userName, String password, ILibraryClient client) throws LibraryException {
    initializeConnection();
    UserDTO userDTO = new UserDTO(userName, password);
    Request request = new Request.Builder().type(RequestType.LOGIN).data(userDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    User user = (User)response.data();
    if (user != null && response.type() == ResponseType.OK) {
      this.client=client;
    }
    return user;
  }

  public void logout(int userId, ILibraryClient client) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.LOGOUT).data(userId).build();
    sendRequest(request);
    Response response = readResponse();
    closeConnection();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  @Override
  public List<Book> getAvailableBooks() throws LibraryException {
    Request request = new Request.Builder().type(RequestType.GET_AVAILABLE_BOOKS).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type()==ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    List<Book> allBooks = (List<Book>)response.data();
    return allBooks;
  }

  @Override
  public List<Book> getUserBooks(int userId) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.GET_USER_BOOKS).data(userId).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    return (List<Book>)response.data();
  }

  @Override
  public List<Book> searchBooks(String key) throws LibraryException {
    Request request = new Request.Builder().type(RequestType.SEARCH_BOOKS).data(key).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      closeConnection();
      throw new LibraryException(errorMessage);
    }
    return (List<Book>)response.data();
  }

  @Override
  public void borrowBook(int userId, int bookId) throws LibraryException {
    UserBookDTO userBookDTO = new UserBookDTO(userId, bookId);
    Request request = new Request.Builder().type(RequestType.BORROW_BOOK).data(userBookDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  @Override
  public void returnBook(int userId, int bookId) throws LibraryException {
    UserBookDTO userBookDTO = new UserBookDTO(userId, bookId);
    Request request = new Request.Builder().type(RequestType.RETURN_BOOK).data(userBookDTO).build();
    sendRequest(request);
    Response response = readResponse();
    if (response.type() == ResponseType.ERROR){
      String errorMessage = response.data().toString();
      throw new LibraryException(errorMessage);
    }
  }

  private void closeConnection() {
    finished=true;
    try {
      input.close();
      output.close();
      connection.close();
      client=null;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void sendRequest(Request request)throws LibraryException {
    try {
      output.writeObject(request);
      output.flush();
    } catch (IOException e) {
      throw new LibraryException("Error sending object "+e);
    }

  }

  private Response readResponse() throws LibraryException {
    Response response=null;
    try{
      response=qresponses.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return response;
  }
  private void initializeConnection() throws LibraryException {
    try {
      connection=new Socket(host,port);
      output=new ObjectOutputStream(connection.getOutputStream());
      output.flush();
      input=new ObjectInputStream(connection.getInputStream());
      finished=false;
      startReader();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private void startReader(){
    Thread tw=new Thread(new ReaderThread());
    tw.start();
  }


  private void handleUpdate(Response response){
    if (response.type() == ResponseType.BORROW_BOOK) {
      try {
        BookQuantityDTO bookQuantityDTO = (BookQuantityDTO)response.data();
        client.bookUpdated(bookQuantityDTO.getBookId(), bookQuantityDTO.getNewQuantity());
      } catch (LibraryException exception) {

      }
    }
    if (response.type() == ResponseType.RETURN_BOOK) {
      try {
        BookDTO bookDTO = (BookDTO)response.data();
        client.bookReturned(bookDTO.getId(), bookDTO.getAuthor(), bookDTO.getTitle());
      } catch (LibraryException exception) {

      }
    }
  }

  private boolean isUpdate(Response response){
    return response.type() == ResponseType.BORROW_BOOK || response.type() == ResponseType.RETURN_BOOK;
  }

  private class ReaderThread implements Runnable{
    public void run() {
      while(!finished){
        try {
          Object response=input.readObject();
          System.out.println("response received "+response);
          if (isUpdate((Response)response)){
            handleUpdate((Response)response);
          }else{
            try {
              qresponses.put((Response)response);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        } catch (IOException e) {
          System.out.println("Reading error "+e);
        } catch (ClassNotFoundException e) {
          System.out.println("Reading error "+e);
        }
      }
    }
  }
}
