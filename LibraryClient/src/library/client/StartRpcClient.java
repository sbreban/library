package library.client;

import library.client.gui.LibraryClientController;
import library.client.gui.LoginWindow;
import library.network.rpcprotocol.LibraryServerRpcProxy;
import library.services.ILibraryServer;

public class StartRpcClient {
  public static void main(String[] args) {
    ILibraryServer server=new LibraryServerRpcProxy("localhost", 55555);
    LibraryClientController libraryClientController=new LibraryClientController(server);

    LoginWindow loginWindow=new LoginWindow("Library", libraryClientController);
    loginWindow.setSize(200,200);
    loginWindow.setLocation(600,300);
    loginWindow.setVisible(true);
  }
}
