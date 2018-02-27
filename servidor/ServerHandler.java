import java.net.*;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerHandler {
  int portNumber;
  Thread mainThread = new Thread();
  private final String ROOT_DIRECTORY = System.getProperty("user.dir");
  private final String TEMP_DIRECTORY = ROOT_DIRECTORY + File.separator + "temp";

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
          booksDownloaded();
        } else if (fromUser.equals("2")) {
          clientsDownload();
        } else if (fromUser.equals("3")) {
          fullInfo();
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

//#region Method that prints books downloaded from this server
  private void booksDownloaded() {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader(TEMP_DIRECTORY + File.separator + "downloads.json"));
      JSONObject rootObj = (JSONObject)obj;

      System.out.println("These are the books that have been downloaded from the server:");
      for(Iterator iterator = rootObj.keySet().iterator(); iterator.hasNext();) {
        String key = (String) iterator.next();
        System.out.println("\t" + key);
      }
    } catch (FileNotFoundException e) {
      System.out.println("Nothing has been downloaded from the server.");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
//#endregion

//#region Method that prints clients that downloaded from this server
  private void clientsDownload() {
    JSONParser parser = new JSONParser();
    HashSet<String> clientSet = new HashSet<String>();
    try {
      Object obj = parser.parse(new FileReader(TEMP_DIRECTORY + File.separator + "downloads.json"));
      JSONObject rootObj = (JSONObject)obj;

      System.out.println("These are the clients that have downloaded files from the server:");
      for(Iterator iterator = rootObj.keySet().iterator(); iterator.hasNext();) {
        String book = (String) iterator.next();
        JSONObject bookObj = (JSONObject) rootObj.get(book);
        for (Iterator iterator2 = bookObj.keySet().iterator(); iterator2.hasNext();) {
          String client = (String) iterator2.next();
          clientSet.add(client);
        }
      }
      clientSet.forEach((c) -> System.out.println("\t" + c));
    } catch (FileNotFoundException e) {
      System.out.println("Nothing has been downloaded from the server.");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
//#endregion

//#region Method that prints books downloaded, clients that did it and number of times
  private void fullInfo() {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader(TEMP_DIRECTORY + File.separator + "downloads.json"));
      JSONObject rootObj = (JSONObject)obj;

      System.out.println("This is the full downloads info:");
      for(Iterator iterator = rootObj.keySet().iterator(); iterator.hasNext();) {
        String book = (String) iterator.next();
        JSONObject bookObj = (JSONObject) rootObj.get(book);
        System.out.println("\t" + book);
        for (Iterator iterator2 = bookObj.keySet().iterator(); iterator2.hasNext();) {
          String client = (String) iterator2.next();
          long count = (long)bookObj.get(client);
          System.out.println("\t\t" + client + ": " + count);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Nothing has been downloaded from the server.");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
//#endregion
}
