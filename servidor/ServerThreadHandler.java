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
      long fileSize, offset;

      while ((inputLine = br.readLine()) != null) {
        if (inputLine.equals("getBooks")) {
          pw.println(getFiles());
        } else if (inputLine.equals("getFileSize")) {
          fileName = br.readLine();
          dos.writeLong(getFileSize(fileName));
        } else if (inputLine.equals("getFile")) {
          fileName = br.readLine();
          fileSize = Long.parseLong(br.readLine());
          offset = Long.parseLong(br.readLine());
          transferFiles(fileName, fileSize, offset, os);
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

//#region Get list of files method
  private String getFiles() {
    File dir = new File(FILES_DIRECTORY);
    File[] files = dir.listFiles();

    String fileNames = "";
    for (int i = 0; i < files.length; i++) {
      fileNames += files[i].getName() + ";";
    }

    return fileNames;
  }
//#endregion

//#region Get file size method
  private long getFileSize(String fileName) {
    try {
      File file = new File(FILES_DIRECTORY + File.separator + fileName);
      return (new byte[(int) file.length()]).length;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
//#endregion

//#region Transfer file method
  private void transferFiles(String fileName, long fileSize, long offset, OutputStream os) {
    try {
      File file = new File(FILES_DIRECTORY + File.separator + fileName);
      InputStream is = new FileInputStream(file);

      int bytesRead;
      byte[] buffer = new byte[8192]; // or 4096, or more

      if (offset > 0) {
        is.skip(fileSize - offset);
        fileSize = offset;
      } //Skip number of bytes sent before

      while ((bytesRead = is.read(buffer)) >= 0) {
        os.write(buffer, 0, bytesRead);
        sleep(500);
      }
      is.close();
      os.close();
    } catch (FileNotFoundException e) {
      System.out.println("File " + fileName + "was not found");
    } catch (SocketException e) {
      System.out.println("Connection with '" + "' lost");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
//#endregion
}
