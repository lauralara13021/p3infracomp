package proyecto3Infracomp;

//Importar las bibliotecas necesarias
import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class SecureServer {
 private ServerSocket serverSocket;
 private Socket socket;
 private DataInputStream in;
 private DataOutputStream out;

 // Añadir campos para la clave de cifrado y el vector de inicialización
 private SecretKey encryptionKey;
 private IvParameterSpec iv;

 public SecureServer(int port) throws IOException {
     serverSocket = new ServerSocket(port);
 }

 public void start() throws IOException {
     System.out.println("Esperando cliente...");
     socket = serverSocket.accept();
     in = new DataInputStream(socket.getInputStream());
     out = new DataOutputStream(socket.getOutputStream());
     System.out.println("Cliente conectado.");
 }

 public void stop() throws IOException {
     in.close();
     out.close();
     socket.close();
     serverSocket.close();
 }

 public void processQueries() throws IOException {
     String query = in.readUTF();
     while (!query.equals("exit")) {
         System.out.println("Consulta recibida: " + query);
         int number = Integer.parseInt(query);
         out.writeUTF(String.valueOf(number - 1));
         query = in.readUTF();
     }
 }

 // Método para generar la clave de cifrado y el vector de inicialización
 public void generateEncryptionKeyAndIv() throws NoSuchAlgorithmException {
     KeyGenerator keyGen = KeyGenerator.getInstance("AES");
     keyGen.init(256);
     encryptionKey = keyGen.generateKey();

     SecureRandom random = new SecureRandom();
     byte[] ivBytes = new byte[16];
     random.nextBytes(ivBytes);
     iv = new IvParameterSpec(ivBytes);
 }

 public static void main(String[] args) {
     try {
         SecureServer server = new SecureServer(6000);
         server.start();
         server.processQueries();
         server.stop();
     } catch (IOException e) {
         e.printStackTrace();
     }
 }
}


