import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.*;
import javax.crypto.*;

public class Cliente {
    private Socket socket;
    private Cipher cipher;
    private Signature signature; // Para verificar la firma
    private SecretKey sharedKey;
    private Mac mac; // Para generar el código de autenticación
    private DataOutputStream out;
    
    public Cliente(String host, int port, SecretKey key, PublicKey publicKey, SecretKey sharedKey) throws Exception {
        this.socket = new Socket(host, port);
        this.sharedKey = sharedKey;
        SecureRandom secureRandom = new SecureRandom();
        this.out = new DataOutputStream(socket.getOutputStream()); // Inicializar DataOutputStream
        // Inicializar otros atributos...
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        this.signature = Signature.getInstance("SHA256withRSA");
        this.signature.initVerify(publicKey); // Inicializar la verificación de firma con la clave pública del servidor
        this.mac = Mac.getInstance("HmacSHA256");
        this.mac.init(sharedKey); // Inicializar el generador de código de autenticación con la clave compartida
    }
    public void enviarConsulta(String consulta) throws Exception {
        // Cifrar la consulta
    	out.writeUTF("SECURE INIT");
        long startTime = System.nanoTime();
        byte[] encrypted = cipher.doFinal(consulta.getBytes());
        byte[] hmac = mac.doFinal(encrypted);
        long endTime = System.nanoTime();
        System.out.println("Tiempo para cifrar la consulta: " + (endTime - startTime) + " nanosegundos");

        // Generar el código de autenticación
        startTime = System.nanoTime();
        out.writeUTF(Base64.getEncoder().encodeToString(encrypted) + "," + Base64.getEncoder().encodeToString(hmac));
        System.out.println("Tiempo para generar el código de autenticación: " + (endTime - startTime) + " nanosegundos");
        endTime = System.nanoTime();

    }
    public void recibirRespuesta(byte[] firma, byte[] respuesta) throws Exception {
        // Verificar la firma
        boolean firmaValida = signature.verify(firma);
        if (!firmaValida) {
            throw new SecurityException("La firma no es válida");
        }
        long startTime = System.nanoTime();
        signature.update(respuesta);

        long endTime = System.nanoTime();
        System.out.println("Tiempo para verificar la firma: " + (endTime - startTime) + " nanosegundos");

        if (!firmaValida) {
            throw new SecurityException("La firma no es válida");
        }
        // Procesar la respuesta...
    }
    // Método para calcular Gy
    public BigInteger calcularGy(BigInteger g, BigInteger p) {
        BigInteger y = new BigInteger(1024, new SecureRandom()); // Generar un número aleatorio y
        return g.modPow(y, p); // Calcular g^y mod p
    }
    public void iniciarSeguro(String reto) throws Exception {
        // Enviar "SECURE INIT" y el reto al servidor
        enviarConsulta(reto);
    }
    
    public boolean verificar(String R) throws Exception {
        // Aquí debes implementar la lógica para verificar R
        // Este es solo un ejemplo y podrías necesitar ajustarlo según tus necesidades específicas
        return R.equals("OK");
    }
    
    public void verificarR(String R) throws Exception {
        // Verificar R
        boolean esValido = verificar(R);

        // Enviar "OK" o "ERROR" al servidor
        if (esValido) {
            enviarConsulta("OK");
        } else {
            enviarConsulta("ERROR");
        }
    }
}
