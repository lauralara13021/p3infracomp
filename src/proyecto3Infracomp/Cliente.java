package proyecto3Infracomp;
import java.net.Socket;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cliente {
    private Socket socket;
    private Cipher cipher;

    public Cliente(String host, int port, SecretKey key) throws Exception {
        this.socket = new Socket(host, port);
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        long startTime = System.nanoTime();
        this.cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        long endTime = System.nanoTime();
        System.out.println("Tiempo de inicialización del cifrado: " + (endTime - startTime) + " nanosegundos");
    }
    public void enviarConsulta(int consulta) throws Exception {
        byte[] encrypted = cipher.doFinal(Integer.toString(consulta).getBytes());
        // enviar encrypted al servidor
    }
}