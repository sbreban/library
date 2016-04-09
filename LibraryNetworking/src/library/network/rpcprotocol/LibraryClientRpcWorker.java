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

/**
 * Created by grigo on 12/15/15.
 */
public class LibraryClientRpcWorker implements Runnable, ILibraryClient {
  private ILibraryServer server;
  private Socket connection;

  private ObjectInputStream input;
  private ObjectOutputStream output;
  private volatile boolean connected;

  public LibraryClientRpcWorker(ILibraryServer server, Socket connection) {
    this.server = server;
    this.connection = connection;
    try{
      output=new ObjectOutputStream(connection.getOutputStream());
      output.flush();
      input=new ObjectInputStream(connection.getInputStream());
      connected=true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    while(connected){
      try {
        Object request = input.readObject();
        Response response = handleRequest((Request)request);
        if (response!=null){
          sendResponse(response);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    try {
      input.close();
      output.close();
      connection.close();
    } catch (IOException e) {
      System.out.println("Error "+e);
    }
  }

  @Override
  public void bookUpdated(int bookId, int newQuantity) throws LibraryException {
    BookQuantityDTO bookQuantityDTO = new BookQuantityDTO(bookId, newQuantity);
    Response response = new Response.Builder().type(ResponseType.BORROW_BOOK).data(bookQuantityDTO).build();
    try {
      sendResponse(response);
    } catch (IOException e) {
      throw new LibraryException("Sending error: "+e);
    }
  }

  @Override
  public void bookReturned(int bookId, String author, String title) throws LibraryException {
    BookDTO bookDTO = new BookDTO(bookId, author, title);
    Response response = new Response.Builder().type(ResponseType.RETURN_BOOK).data(bookDTO).build();
    try {
      sendResponse(response);
    } catch (IOException e) {
      throw new LibraryException("Sending error: "+e);
    }
  }

  private Response handleRequest(Request request){
    Response response = null;
    if (request.type() == RequestType.LOGIN){
      System.out.println("Login request ...");
      UserDTO userDTO=(UserDTO)request.data();
      try {
        User user = server.login(userDTO.getUserName(), userDTO.getPassword(), this);
        return new Response.Builder().type(ResponseType.OK).data(user).build();
      } catch (LibraryException e) {
        connected = false;
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.LOGOUT){
      System.out.println("Logout request");
      int userId=(int)request.data();
      try {
        server.logout(userId, this);
        connected = false;
        return new Response.Builder().type(ResponseType.OK).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.GET_AVAILABLE_BOOKS) {
      try {
        List<Book> allBooks = server.getAvailableBooks();
        return new Response.Builder().type(ResponseType.GET_AVAILABLE_BOOKS).data(allBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.GET_USER_BOOKS) {
      try {
        int userId = (int)request.data();
        List<Book> userBooks = server.getUserBooks(userId);
        return new Response.Builder().type(ResponseType.GET_USER_BOOKS).data(userBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.SEARCH_BOOKS) {
      try {
        String key = (String)request.data();
        List<Book> foundBooks = server.searchBooks(key);
        return new Response.Builder().type(ResponseType.GET_USER_BOOKS).data(foundBooks).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.BORROW_BOOK) {
      try {
        UserBookDTO userBookDTO = (UserBookDTO)request.data();
        server.borrowBook(userBookDTO.getUserId(), userBookDTO.getBookId());
        return new Response.Builder().type(ResponseType.OK).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    if (request.type() == RequestType.RETURN_BOOK) {
      try {
        UserBookDTO userBookDTO = (UserBookDTO)request.data();
        server.returnBook(userBookDTO.getUserId(), userBookDTO.getBookId());
        return new Response.Builder().type(ResponseType.OK).build();
      } catch (LibraryException e) {
        return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
      }
    }
    return response;
  }

  private void sendResponse(Response response) throws IOException{
    System.out.println("sending response "+response);
    output.writeObject(response);
    output.flush();
  }
}
