import java.net.*;
import java.io.*;
import java.util.*;

class ServerThreadHandler extends Thread {
  private int portNumber;

  public ServerThreadHandler(int portNumber) {
    super("ServerThreadHandler");
    this.portNumber = portNumber;
  }

  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(portNumber)) {
      while (true) {
        new ActionsThreadHandler(server.accept()).start();
      }
    } catch (IOException e) {
      System.err.println("Can't listen on port number " + portNumber + ".");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
