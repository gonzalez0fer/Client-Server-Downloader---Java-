import java.net.*;
import java.io.*;
import java.util.*;

public class ServerHandler {
  int portNumber;
  Thread mainThread = new Thread();

  public ServerHandler(int portNumber) {
    super();
    this.portNumber = portNumber;
  }

  public void start() {
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromUser;

      mainThread = new ServerThreadHandler(portNumber);
      mainThread.start();

      while (true) {
        System.out.println();
        System.out.println("What do you want to do?");
        System.out.println("    1. Books downloaded");
        System.out.println("    2. Clients that downloaded");
        System.out.println("    3. Number of downloads per book by Client");
        System.out.println("    4. Current downloads");
        System.out.print("Choose an option(1-4): ");

        fromUser = stdIn.readLine();
        System.out.println();

        if (fromUser == null) continue;

        if (fromUser.equals("1")) {
          continue;
        } else if (fromUser.equals("2")) {
          continue;
        } else if (fromUser.equals("3")) {
          continue;
        } else if (fromUser.equals("4")) {
          continue;
        } else {
          System.out.println("Choose a valid option.");
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
