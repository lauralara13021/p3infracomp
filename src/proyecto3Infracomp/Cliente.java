package proyecto3Infracomp;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cliente {
    private Socket socket;
    private Cipher cipher;
    private Signature signature; // Para verificar la firma
    private SecretKey sharedKey;

    public Cliente(String host, int port, SecretKey key, PublicKey publicKey, SecretKey sharedKey) throws Exception {
        this.socket = new Socket(host, port);
        this.sharedKey = sharedKey;
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        this.signature = Signature.getInstance("SHA256withRSA");
        this.signature.initVerify(publicKey); // Inicializar la verificación de firma con la clave pública del servidor
    }

    public void enviarConsulta(int consulta) throws Exception {
        // Cifrar la consulta
        byte[] encrypted = cipher.doFinal(Integer.toString(consulta).getBytes());
        // Generar el código de autenticación
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(sharedKey);
        byte[] hmac = mac.doFinal(encrypted);
        // enviar encrypted y hmac al servidor
    }

    public void recibirRespuesta(byte[] firma, byte[] respuesta) throws Exception {
        // Verificar la firma
        signature.update(respuesta);
        boolean firmaValida = signature.verify(firma);
        if (!firmaValida) {
            throw new SecurityException("La firma no es válida");
    }
}}