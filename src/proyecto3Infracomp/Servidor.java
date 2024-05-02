package proyecto3Infracomp;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

public class Servidor {
    private ServerSocket serverSocket;
    private Cipher cipher;
    private Signature signature;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey sharedKey;
    private PrivateKey privateKey;

    public Servidor(int port, SecretKey key, PrivateKey privateKey) throws Exception {
        this.sharedKey = key;
        this.serverSocket = new ServerSocket(port);
        this.privateKey = privateKey;
        Socket socket = serverSocket.accept();
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        this.signature = Signature.getInstance("SHA256withRSA");
        this.signature.initSign(privateKey);
    }

    public void recibirReto() throws Exception {
        String mensaje = recibirConsulta();
        if (mensaje.equals("SECURE INIT")) {
            String R = calcularR(mensaje);
            enviarRespuesta(R);
        }
    }

    public void generarGPGx() throws Exception {
        BigInteger G = new BigInteger(1024, new SecureRandom());
        BigInteger P = new BigInteger(1024, new SecureRandom());
        BigInteger Gx = G.modPow(new BigInteger(1024, new SecureRandom()), P);
        String GP_Gx = cifrar(G.toString() + "," + P.toString() + "," + Gx.toString());
        enviarRespuesta(GP_Gx);
    }

    public void enviarRespuesta(String respuesta) throws Exception {
        out.writeUTF(respuesta);
    }

    public String calcularR(String reto) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
        byte[] encrypted = cipher.doFinal(reto.getBytes());
        return new String(encrypted);
    }

    public String cifrar(String texto) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
        byte[] encrypted = cipher.doFinal(texto.getBytes());
        return new String(encrypted);
    }

    public String recibirConsulta() throws Exception {
        return in.readUTF();
    }

    public void escuchar() throws Exception {
        String mensaje = recibirConsulta();
        int consulta = Integer.parseInt(mensaje);
        int respuesta = consulta - 1;
        enviarRespuesta(Integer.toString(respuesta));

        // Recibir el reto del cliente y responder
        recibirReto();

        // Generar GPGx y enviar al cliente
        generarGPGx();
    }

    public void enviarRespuesta(String firma, String respuesta) throws Exception {
        out.writeUTF(firma + "," + respuesta);
    }

    public void responderConsulta(byte[] consulta, byte[] hmac) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(sharedKey);
        byte[] hmacVerificado = mac.doFinal(consulta);
        if (!Arrays.equals(hmac, hmacVerificado)) {
            throw new SecurityException("El código de autenticación no es válido");
        }
        byte[] decrypted = cipher.doFinal(consulta);
        byte[] respuesta = null;
        signature.update(respuesta);
        byte[] firma = signature.sign();
    }
}
