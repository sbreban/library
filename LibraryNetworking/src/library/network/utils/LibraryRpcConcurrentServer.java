package library.network.utils;

import library.network.rpcprotocol.LibraryClientRpcWorker;
import library.services.ILibraryServer;

import java.net.Socket;

/**
 * Created by grigo on 2/25/16.
 */
public class LibraryRpcConcurrentServer extends AbsConcurrentServer {

  private ILibraryServer chatServer;

  public LibraryRpcConcurrentServer(int port, ILibraryServer chatServer) {
    super(port);
    this.chatServer = chatServer;
    System.out.println("Chat- LibraryRpcConcurrentServer");
  }

  @Override
  protected Thread createWorker(Socket client) {
    LibraryClientRpcWorker worker=new LibraryClientRpcWorker(chatServer, client);
    Thread tw=new Thread(worker);
    return tw;
  }
}
