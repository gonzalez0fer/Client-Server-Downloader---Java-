import java.net.*;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class ActionsThreadHandler extends Thread {
  private Socket socket;
  private final String ROOT_DIRECTORY = System.getProperty("user.dir");
  private final String FILES_DIRECTORY = ROOT_DIRECTORY + File.separator + "books";
  private final String TEMP_DIRECTORY = ROOT_DIRECTORY + File.separator + "temp";

  public ActionsThreadHandler(Socket socket) {
    super("ActionsThreadHandler");
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
      String inputLine, fileName, clientName;
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
          clientName = br.readLine();
          transferFiles(fileName, fileSize, offset, clientName, os);
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
  private void transferFiles(
    String fileName, 
    long fileSize, 
    long offset, 
    String clientName, 
    OutputStream os
  ) {
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
      writeJson(fileName, clientName);
    } catch (FileNotFoundException e) {
      System.out.println("File " + fileName + "was not found");
    } catch (SocketException e) {
      System.out.println("Connection with '" + "' lost");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
//#endregion

//#region Write JSON file with downloads
  private void writeJson(String bookName, String clientName) {
    String path = TEMP_DIRECTORY + File.separator + "downloads.json";
    JSONParser parser = new JSONParser();
    JSONObject rootObj = new JSONObject();
    JSONObject bookObj = new JSONObject();
    long count = 0;

    //Try to read the JSON file in case it already exists.
    try (FileReader reader = new FileReader(path)) {
      rootObj = (JSONObject) parser.parse(reader);
    } catch (FileNotFoundException e) {
      //
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Check if the book was already downloaded and if it wasn't then add its key
    //to the JSON file.
    if (rootObj.containsKey(bookName)) 
    {
      bookObj = (JSONObject) rootObj.get(bookName);
      if (bookObj.containsKey(clientName)) {
        count = (long) bookObj.get(clientName);
        count++;
        bookObj.put(clientName, count);
      } else {
        bookObj.put(clientName, 1);
      }
    } else {
      bookObj.put(clientName, 1);
      rootObj.put(bookName, bookObj);
    }

    //Writes the JSON object to the downloads.json file
    try (FileWriter writer = new FileWriter(path)) {
      writer.write(rootObj.toJSONString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
//#endregion
}
