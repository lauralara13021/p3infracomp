package proyecto3Infracomp;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Servidor {
    private ServerSocket serverSocket;
    private Cipher cipher;
    private Signature signature; // Para generar la firma
    private SecretKey sharedKey; 

    public Servidor(int port, SecretKey key, PrivateKey privateKey) throws Exception {
        this.serverSocket = new ServerSocket(port);
        this.sharedKey = sharedKey;
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        this.signature = Signature.getInstance("SHA256withRSA");
        this.signature.initSign(privateKey); // Inicializar la firma con la clave privada del servidor
    }

    public void escuchar() throws Exception {
        Socket socket = serverSocket.accept();
        // recibir datos, descifrar, responder
    }

    public void responderConsulta(byte[] consulta, byte[] hmac) throws Exception {
        // Verificar el código de autenticación
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(sharedKey); 
        byte[] hmacVerificado = mac.doFinal(consulta);
        if (!Arrays.equals(hmac, hmacVerificado)) {
            throw new SecurityException("El código de autenticación no es válido");
        }
        // Descifrar la consulta
        byte[] decrypted = cipher.doFinal(consulta);
        // Procesar la consulta y generar la respuesta...
        byte[] respuesta = null;
        // Firmar la respuesta
        signature.update(respuesta);
        byte[] firma = signature.sign();
        // Enviar la firma y la respuesta al cliente
    }}
