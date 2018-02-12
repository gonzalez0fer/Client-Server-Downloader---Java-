import java.net.*;
import java.io.*;
import java.util.*;


public class cliente{
  Socket cliente;
  int puerto = 9000;
  String ip = "127.0.0.1";
  BufferedReader entrada, teclado;
  PrintStream salida;

  public void iniciar(){
    System.out.println("Bienvenido usuario, desea conectarse? s/n");
    Scanner scan = new Scanner(System.in);
    String s = scan.next();
    if (s.equals("s")){

      try{
       cliente = new Socket(ip, puerto);
       System.out.println("Bienvenido usuario, para conectarse oprima a, para salir 0");
       System.out.println("1) ESTADO_DESCARGAS");
       System.out.println("2) LISTA_LIBROS");
       System.out.println("3) SOLICITUD_LIBRO");
       System.out.println("4) LIBROS_DESCARGADOSXSERVIDOR");
       entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
       teclado = new BufferedReader(new InputStreamReader(System.in));
       String tec = teclado.readLine();
       salida = new PrintStream(cliente.getOutputStream());

       if (tec.equals("1")) {
         /*FUNCIONALIDAD DE ESTADO ACTUAL DE DESCARGAS*/
         System.out.println("1) ESTADO_DESCARGAS");
       }else if (tec.equals("2")) {
         salida.println("LISTA_LIBROS");
       }else if (tec.equals("3")) {
         System.out.println("Cual libro desea descargar?");
         String libro = scan.next();
         salida.println(libro);

       }else if (tec.equals("4")) {
         /*FUNCIONALIDAD DE LIBROS DESCARGADOS*/
         System.out.println("4) LIBROS_DESCARGADOSXSERVIDOR");

       }


       String msg = entrada.readLine();
       System.out.println(msg);

       entrada.close();
       salida.close();
       teclado.close();
       cliente.close();
    }catch(Exception e){};
  } else {
    System.out.println("Hasta luego");
  }
 }
}
