package proyecto3Infracomp;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Servidor {
    private ServerSocket serverSocket;
    private Cipher cipher;
    private Signature signature; // Para generar la firma
    

    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey sharedKey; 
    private PrivateKey privateKey; // Nueva variable para la clave privada

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
        this.signature.initSign(privateKey); // Inicializar la firma con la clave privada del servidor
    }


    public void recibirReto() throws Exception {
        // Recibir el mensaje "SECURE INIT" y el reto del cliente
        String mensaje = recibirConsulta();

        // Verificar si el mensaje es "SECURE INIT"
        if (mensaje.equals("SECURE INIT")) {
            // Calcular R = C(K_w-, Reto)
            String R = calcularR(mensaje);

            // Enviar R al cliente
            enviarRespuesta(R);
        }
    }
    
    public void generarGPGx() throws Exception {
        // Generar G, P y G^x
        BigInteger G = new BigInteger(1024, new SecureRandom());
        BigInteger P = new BigInteger(1024, new SecureRandom());
        BigInteger Gx = G.modPow(new BigInteger(1024, new SecureRandom()), P);

        // Cifrar G, P y G^x con la clave K_w-
        String GP_Gx = cifrar(G.toString() + "," + P.toString() + "," + Gx.toString());

        // Enviar G, P, G^x y GP_Gx cifrado al cliente
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
        // Recibir datos del cliente
        String mensaje = recibirConsulta();
        if (mensaje.equals("SECURE INIT")) {
            enviarRespuesta("OK");
        } else {
            throw new Exception("Mensaje inesperado del cliente");
        }

        // Recibir la consulta cifrada y el HMAC
        String datosCifradosYHMAC = recibirConsulta();
        String[] partes = datosCifradosYHMAC.split(",");
        byte[] consultaCifrada = Base64.getDecoder().decode(partes[0]);
        byte[] hmac = Base64.getDecoder().decode(partes[1]);

        // Verificar HMAC
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(sharedKey);
        byte[] hmacVerificado = mac.doFinal(consultaCifrada);
        if (!Arrays.equals(hmac, hmacVerificado)) {
            throw new SecurityException("El código de autenticación no es válido");
        }

        // Descifrar la consulta
        byte[] decrypted = cipher.doFinal(consultaCifrada);

        // Procesar la consulta y generar la respuesta...
        byte[] respuesta = "Respuesta del servidor".getBytes();

        Firma firmaInstancia = new Firma(privateKey);
        byte[] firma = firmaInstancia.firmar(respuesta);

        // Enviar la respuesta y firma al cliente
        enviarRespuesta(Base64.getEncoder().encodeToString(firma), Base64.getEncoder().encodeToString(respuesta));
    }

    
    public void enviarRespuesta(String firma, String respuesta) throws Exception {
        out.writeUTF(firma + "," + respuesta);
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
