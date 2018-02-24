import java.net.*;
import java.io.*;
import java.util.*;

class ServerThreadHandler extends Thread {
  private Socket socket;
  private final String FILES_DIRECTORY = System.getProperty("user.dir") + File.separator + "books";

  public ServerThreadHandler(Socket socket) {
    super("ServerThreadHandler");
    this.socket = socket;
  }

  @Override
  public void run() {
    try (
      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      DataInputStream dis = new DataInputStream(is);
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      DataOutputStream dos = new DataOutputStream(os);
      PrintWriter pw = new PrintWriter(os, true);
    ) {
      String inputLine, fileName;
      long fileSize;

      while ((inputLine = br.readLine()) != null) {
        if (inputLine.equals("getBooks")) {
          pw.println(getFiles());
        } else if (inputLine.equals("getFileSize")) {
          fileName = br.readLine();
          dos.writeLong(getFileSize(fileName));
        } else if (inputLine.equals("getFile")) {
          fileName = br.readLine();
          fileSize = Long.parseLong(br.readLine());
          transferFiles(fileName, fileSize, os);
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

    return fileNames;
  }

  private long getFileSize(String fileName) {
    try {
      File file = new File(FILES_DIRECTORY + File.separator + fileName);
      return (new byte[(int) file.length()]).length;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  private void transferFiles(String fileName, long fileSize, OutputStream os) {
    try {
      File file = new File(FILES_DIRECTORY + File.separator + fileName);
      InputStream is = new FileInputStream(file);

      System.out.print("File transfer progress bar: ");
      int bytesRead;
      byte[] buffer = new byte[8192]; // or 4096, or more
      while ((bytesRead = is.read(buffer)) > 0) {
        os.write(buffer, 0, bytesRead);
        System.out.print("|"); //Reading progress indicator
        sleep(500);
      }
      is.close();
      os.flush();
    } catch (FileNotFoundException e) {
      System.out.println("File " + fileName + "was not found");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
