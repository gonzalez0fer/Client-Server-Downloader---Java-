import java.net.*;
import java.io.*;
import java.util.*;

public class ClientHandler {
  int portNumber1;
  int portNumber2;
  int portNumber3;
  final String ip = "127.0.0.1";
  final String ROOT_DIRECTORY = System.getProperty("user.dir");

  public ClientHandler(int portNumber1, int portNumber2, int portNumber3) {
    super();
    this.portNumber1 = portNumber1;
    this.portNumber2 = portNumber2;
    this.portNumber3 = portNumber3;
  }

  public void start() {
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromUser;

      System.out.println("Welcome user, would you like to connect to the server? Y/N");
      String init = stdIn.readLine();

      if (!init.trim().toLowerCase().equals("y")) {
        System.out.println("See you later.");
        System.exit(1);
      }

      while (true) {
        System.out.println();
        System.out.println("What do you want to do?");
        System.out.println("    1. Download Status");
        System.out.println("    2. Books List");
        System.out.println("    3. Request a Book");
        System.out.println("    4. Books Downloaded");
        System.out.println("    5. Quit");
        System.out.print("Choose an option(1-5): ");

        fromUser = stdIn.readLine();
        System.out.println();

        if (fromUser == null) continue;

        if (fromUser.equals("1")) {
          continue;
        } else if (fromUser.equals("2")) {
          getBookList();
        } else if (fromUser.equals("3")) {
          System.out.print("Indicate file name: ");
          downloadBook(stdIn.readLine());
        } else if (fromUser.equals("4")) {
          continue;
        } else if (fromUser.equals("5")) {
          break;
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

  private void getBookList() {
    try (
      Socket socket = new Socket(ip, portNumber1);
      InputStream is = socket.getInputStream();
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
    ) {
      out.println("2"); //Send option to the server
      System.out.println(in.readLine()); //Print out the response from the server
    } catch (UnknownHostException e) {
      System.err.println("Host with ip number " + ip + " is unknown.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't stablish I/O with host with ip number " + ip + ".");
      System.exit(1);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  private void downloadBook(String bookName) {
    try {
      new ClientThreadHandler(ip, portNumber1, bookName).start();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
