package proyecto3Infracomp;




import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class SecureServer1 {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey encryptionKey;
    private IvParameterSpec iv;
    private PublicKey publicKey;
    private SecretKey hmacKey;

    public SecureServer1(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public void start() throws IOException {
        System.out.println("Esperando cliente...");
        socket = serverSocket.accept();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Cliente conectado.");
    }
    public void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }
    public void processQueries() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        try {
            String query = in.readUTF();
            byte[] encryptedQuery = Base64.getDecoder().decode(query);
            byte[] decryptedQuery, hmac, signature;

            // Generar la firma
            long startSignature = System.nanoTime();
            // Genera la firma aquí
            long endSignature = System.nanoTime();
            long signatureTime = endSignature - startSignature;
            System.out.println("Tiempo de generación de firma: " + signatureTime + " nanosegundos");

            // Descifrar la consulta
            long startDecryption = System.nanoTime();
            decryptedQuery = decryptAES(encryptedQuery, encryptionKey, iv);
            long endDecryption = System.nanoTime();
            long decryptionTime = endDecryption - startDecryption;
            System.out.println("Tiempo de descifrado de consulta: " + decryptionTime + " nanosegundos");

            // Verificar el código de autenticación
            long startHmac = System.nanoTime();
            hmac = generateHMAC(decryptedQuery, hmacKey);
            long endHmac = System.nanoTime();
            long hmacTime = endHmac - startHmac;
            System.out.println("Tiempo de verificación de HMAC: " + hmacTime + " nanosegundos");

            // Envío de datos y otras partes del código...
        } catch (EOFException e) {
            System.err.println("Se alcanzó el final del archivo de entrada (EOF) de manera inesperada. La conexión pudo haber sido cerrada por el cliente.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void generateEncryptionKeyAndIv() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        encryptionKey = keyGen.generateKey();

        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        iv = new IvParameterSpec(ivBytes);
    }

    private static byte[] decryptAES(byte[] encryptedData, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }

    private static byte[] generateHMAC(byte[] data, SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        return mac.doFinal(data);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {
            SecureServer server = new SecureServer(8000);
            // Generar la clave de cifrado y el vector de inicialización
            server.generateEncryptionKeyAndIv();
            server.start();
            // Mide el tiempo de procesamiento 
            long startProcessingTime = System.nanoTime();
            server.processQueries();
            long endProcessingTime = System.nanoTime();
            long totalProcessingTime = endProcessingTime - startProcessingTime;
            System.out.println("Tiempo total de procesamiento de consultas en el servidor: " + totalProcessingTime + " nanosegundos");

            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}