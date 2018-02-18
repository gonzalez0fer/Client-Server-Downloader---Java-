import java.net.*;
import java.io.*;
import java.util.*;

public class servidor {
  int portNumber;

  public servidor(int portNumber) {
    super();
    this.portNumber = portNumber;
  }

  public void iniciar() {
    boolean listening = true;
    int i = 1;
    try (ServerSocket server = new ServerSocket(portNumber)) {
      System.out.println("Listening on port " + portNumber);
      while (listening) {
        new LibraryHandler(server.accept(), i).start();
        System.out.println("Thread " + i + " open.");
        System.out.println("Client with IP " + server.getInetAddress().getHostName() + " connected.");
      }
    } catch (IOException e) {
      System.err.println("Can't listen on port number " + portNumber + ".");
      System.exit(-1);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      //e.printStackTrace();
      System.exit(-1);
    }
  }
}
