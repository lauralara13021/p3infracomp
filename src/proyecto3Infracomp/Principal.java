package proyecto3Infracomp;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;

public class Principal {
    public static void main(String[] args) {
        try {
            // Generar llaves
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Crear firma
            Firma firma = new Firma(privateKey);

            // Crear llave simétrica
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

            // Crear cifrado
            Cifrado cifrado = new Cifrado(secretKey);

            // Crear servidor
            Servidor servidor = new Servidor(8080, secretKey);

            // Crear cliente
            Cliente cliente = new Cliente("localhost", 8080, secretKey);

            // Iniciar comunicación
            cliente.enviarConsulta(123);
            servidor.escuchar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
