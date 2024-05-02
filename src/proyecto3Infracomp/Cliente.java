package proyecto3Infracomp;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

public class Cliente {
    private Socket socket;
    private Cipher cipher;
    private Signature signature;
    private SecretKey sharedKey;
    private Mac mac;
    private DataOutputStream out;
    private DataInputStream in;

    public Cliente(String host, int port, SecretKey key, PublicKey publicKey, SecretKey sharedKey) throws Exception {
        this.socket = new Socket(host, port);
        this.sharedKey = sharedKey;
        SecureRandom secureRandom = new SecureRandom();
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        this.signature = Signature.getInstance("SHA256withRSA");
        this.signature.initVerify(publicKey);
        this.mac = Mac.getInstance("HmacSHA256");
        this.mac.init(sharedKey);
    }

    public void enviarConsulta(int consulta) throws Exception {
        out.writeUTF(Integer.toString(consulta));
        System.out.println("Cliente envió la consulta: " + consulta);
        String respuesta = in.readUTF();
        System.out.println("Respuesta del servidor: " + respuesta);
    }

    public void recibirRespuesta(byte[] firma, byte[] respuesta) throws Exception {
        long startTime = System.nanoTime();
        boolean firmaValida = signature.verify(firma);
        long endTime = System.nanoTime();
        System.out.println("Tiempo para verificar la firma: " + (endTime - startTime) + " nanosegundos");

        if (!firmaValida) {
            throw new SecurityException("La firma no es válida");
        }
        startTime = System.nanoTime();
        signature.update(respuesta);
        endTime = System.nanoTime();
        System.out.println("Tiempo para actualizar la firma: " + (endTime - startTime) + " nanosegundos");
    }

    public BigInteger calcularGy(BigInteger g, BigInteger p) {
        long startTime = System.nanoTime();
        BigInteger y = new BigInteger(1024, new SecureRandom());
        long endTime = System.nanoTime();
        long tiempoTranscurrido = endTime - startTime;
        System.out.println("Tiempo transcurrido en calcularGy: " + tiempoTranscurrido + " nanosegundos");
        return g.modPow(y, p);
    }
}

