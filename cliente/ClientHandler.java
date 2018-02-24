import java.net.*;
import java.io.*;
import java.util.*;

public class ClientHandler {
  int portNumber1;
  int portNumber2;
  int portNumber3;
  String clientName = "Someone";
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

      System.out.print("Welcome user, please tell me your username: ");
      clientName = stdIn.readLine();

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
          getBookList(portNumber1);
          getBookList(portNumber2);
          getBookList(portNumber3);
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

  private void getBookList(int portNumber) {
    try (
      Socket socket = new Socket(ip, portNumber);
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      String bookList = "";

      pw.println("getBooks"); //Send option to the server
      bookList = br.readLine();

      System.out.println("Server with ip " + ip + " and port " + portNumber + ".");
      for (String book : bookList.split(";")) {
        System.out.println(book);
      }
    } catch (UnknownHostException e) {
      System.err.println("Host with ip number " + ip + " is unknown.");
    } catch (IOException e) {
      System.err.println("Couldn't stablish I/O with host with ip number " + 
        ip + " and port " + portNumber + ".");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void downloadBook(String bookName) {
    try {
      new ClientThreadHandler(
        ip, portNumber1, portNumber2, portNumber3, bookName, clientName
      ).start();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
