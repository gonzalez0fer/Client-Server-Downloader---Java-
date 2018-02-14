import java.net.*;
import java.io.*;
import java.util.*;

class LibraryHandler extends Thread {
  private Socket socket;
  private int threadNumber;

  public LibraryHandler(Socket socket, int counter) {
    super("LibraryHandlerThread");
    this.socket = socket;
    this.threadNumber = counter;
  }

  @Override
  public void run() {
    try (
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    ) {
      String inputLine, outputLine;

      outputLine = "connected";
      out.println(outputLine);

      while ((inputLine = in.readLine()) != null) {
        if (inputLine.equals("2")) {
          File dir = new File(
            System.getProperty("user.dir") + File.separator + "books");
          File[] files = dir.listFiles();

          String sFiles = "";
          for (int i = 0; i < files.length; i++) {
            sFiles += files[i].getName() + ";";
          }
          out.println(sFiles);
        } else if (inputLine.equals("5")) {
          System.out.println("Closing thread " + threadNumber);
          out.println("Bye");
          break;
        } else {
          out.println("option not recognized");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}