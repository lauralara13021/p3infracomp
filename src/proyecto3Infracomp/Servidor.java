package proyecto3Infracomp;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Servidor {
    private ServerSocket serverSocket;
    private Cipher cipher;
    public Servidor(int port, SecretKey key) throws Exception {
        this.serverSocket = new ServerSocket(port);
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        long startTime = System.nanoTime();
        this.cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        long endTime = System.nanoTime();
        System.out.println("Tiempo de inicialización del cifrado: " + (endTime - startTime) + " nanosegundos");
    }

    public void escuchar() throws Exception {
        Socket socket = serverSocket.accept();
        // recibir datos, descifrar, responder
    }
}
