import library.network.utils.AbstractServer;
import library.network.utils.LibraryRpcConcurrentServer;
import library.network.utils.ServerException;
import library.server.LibraryServerImpl;
import library.services.ILibraryServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartRpcServer {
  public static void main(String[] args) {
    try {
      Properties serverProps = new Properties(System.getProperties());

      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream inputStream = new FileInputStream("config.xml");
      Document document = builder.parse(inputStream);
      document.getDocumentElement().normalize();
      NodeList nodeList = document.getElementsByTagName("config");
      Node node = nodeList.item(0);
      Element element = (Element) node;
      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Element childElement = (Element) childNodes.item(i);
          String tagName = childElement.getTagName();
          String tagValue = childElement.getChildNodes().item(0).getTextContent();
          serverProps.setProperty(tagName, tagValue);
        }
      }

      System.setProperties(serverProps);
    } catch (ParserConfigurationException | IOException | SAXException e) {
      e.printStackTrace();
    }

    ILibraryServer libraryServer=new LibraryServerImpl();
    AbstractServer server=new LibraryRpcConcurrentServer(55555, libraryServer);
    try {
      server.start();
    } catch (ServerException e) {
      System.out.println(e.getMessage());
    }
  }
}
