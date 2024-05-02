package proyecto3Infracomp;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Mac;

public class Principal {
    public static void main(String[] args) {
        try {
            // Generador de llaves 
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            Firma firma = new Firma(privateKey);
            
            // Crear llave simétrica
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();


            // Crear servidor
            Servidor servidor = new Servidor(8080, secretKey, privateKey);
            System.out.println("No se  crea el servidor ");

            // Iniciar comunicación con diferentes números de clientes delegados
            ejecutarPruebas(servidor, secretKey, publicKey, privateKey, 4);
            ejecutarPruebas(servidor, secretKey, publicKey, privateKey, 16);
            ejecutarPruebas(servidor, secretKey, publicKey, privateKey, 32);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void ejecutarPruebas(Servidor servidor, SecretKey secretKey, PublicKey publicKey, PrivateKey privateKey, int numClientes) throws Exception {
        // Crear clientes
        Cliente[] clientes = new Cliente[numClientes];
        for (int i = 0; i < numClientes; i++) {
            clientes[i] = new Cliente("localhost", 8080, secretKey, publicKey, secretKey);
            System.out.println("Cliente " + i + " creado.");
        }
        // Iniciar comunicación con los clientes
        for (Cliente cliente : clientes) {
            cliente.enviarConsulta(1234);
            System.out.println("Consulta enviada por el cliente.");
        }

        // Iniciar escucha en el servidor
        servidor.escuchar();
        System.out.println("El servidor está escuchando.");
    }
}
