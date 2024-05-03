package proyecto3Infracomp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SecureClient {
	private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey encryptionKey;
    private IvParameterSpec iv;
    private PrivateKey privateKey;
    private SecretKey hmacKey;

	 public SecureClient(String host, int port) throws IOException {
	     socket = new Socket(host, port);
	     in = new DataInputStream(socket.getInputStream());
	     out = new DataOutputStream(socket.getOutputStream());
	 }

	 public void stop() throws IOException {
	     in.close();
	     out.close();
	     socket.close();
	 }

	 public void sendQuery(String query) throws IOException {
	     out.writeUTF(query);
	     String response = in.readUTF();
	     System.out.println("Respuesta recibida: " + response);
	     
	 }

	 public static void main(String[] args) {
	     try {
	         SecureClient client = new SecureClient("localhost", 6000);
	         client.sendQuery("10");
	         client.stop();
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	 }
	}