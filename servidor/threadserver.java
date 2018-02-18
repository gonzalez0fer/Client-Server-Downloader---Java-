import java.net.*;
import java.io.*;
import java.util.*;

class LibraryHandler extends Thread {
  private Socket socket;
  private int threadNumber;
  private final String FILES_DIRECTORY = System.getProperty("user.dir") + File.separator + "books";

  public LibraryHandler(Socket socket, int counter) {
    super("LibraryHandlerThread");
    this.socket = socket;
    this.threadNumber = counter;
  }

  @Override
  public void run() {
    try (
      OutputStream os = socket.getOutputStream();
      PrintWriter out = new PrintWriter(os, true);
      BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    ) {
      String inputLine, outputLine;

      outputLine = "Connected to the server.";
      out.println(outputLine);

      while ((inputLine = in.readLine()) != null) {
        String[] parts = inputLine.split(";");
        String fileName = "";
        String option = "";

        if (parts.length > 1) {
          fileName = parts[1];
        }
        option = parts[0];

        if (option.equals("2")) {
          out.println(getFiles());
        } else if (option.equals("3")) {
          out.println(transferFiles(fileName, os));
        } else if (option.equals("5")) {
          System.out.println("Closing thread " + threadNumber + ".");
          out.println("Bye");
          break;
        } else {
          out.println("Option not recognized.");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getFiles() {
    File dir = new File(FILES_DIRECTORY);
    File[] files = dir.listFiles();

    String fileNames = "";
    for (int i = 0; i < files.length; i++) {
      fileNames += files[i].getName() + ";";
    }

    return "List of files in directory: " + fileNames;
  }

  private String transferFiles(String fileName, OutputStream os) {
    try {
      File file = new File(FILES_DIRECTORY + File.separator + fileName);

      DataOutputStream dos = new DataOutputStream(os);
      dos.writeUTF(file.getName());
      dos.writeLong((new byte[(int) file.length()]).length);

      InputStream is = new FileInputStream(file);

      System.out.print("File transfer progress bar: ");
      int bytesRead;
      byte[] buffer = new byte[8192]; // or 4096, or more
      while ((bytesRead = is.read(buffer)) > 0) {
        os.write(buffer, 0, bytesRead);
        System.out.print("|"); //Reading progress indicator
        sleep(500);
      }
      os.flush();
      is.close();
      System.out.println();
    } catch (FileNotFoundException e) {
      return "The file indicated was not found.";
    } catch (Exception e) {
      e.printStackTrace();
      return "There was an error transfering the file.";
    }
    return "File recieved.";
  }
}
