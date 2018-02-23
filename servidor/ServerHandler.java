import java.net.*;
import java.io.*;
import java.util.*;

public class ServerHandler {
  int portNumber;

  public ServerHandler(int portNumber) {
    super();
    this.portNumber = portNumber;
  }

  public void start() {
    boolean listening = true;
    int i = 1;
    try (ServerSocket server = new ServerSocket(portNumber)) {
      System.out.println("Listening on port " + portNumber);
      while (listening) {
        new ServerThreadHandler(server.accept(), i).start();
      }
    } catch (IOException e) {
      System.err.println("Can't listen on port number " + portNumber + ".");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
