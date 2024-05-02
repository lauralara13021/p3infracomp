package proyecto3Infracomp;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class SecureClient1 {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey encryptionKey;
    private IvParameterSpec iv;
    private PrivateKey privateKey;

    public SecureClient1(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void sendQuery(String query) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        byte[] encryptedQuery, signature, decryptedResponse;

        // Verificar la firma
        long startVerification = System.nanoTime();
        encryptedQuery = encryptAES(query, encryptionKey, iv);
        signature = signData(encryptedQuery, privateKey);
        long endVerification = System.nanoTime();
        long verificationTime = endVerification - startVerification;
        System.out.println("Tiempo de verificación de firma: " + verificationTime + " nanosegundos");

        // Calcular Gy
        long startCalculation = System.nanoTime();
        // Calcula Gy aquí AUN NO ESTÁ
        long endCalculation = System.nanoTime();
        long calculationTime = endCalculation - startCalculation;
        System.out.println("Tiempo de cálculo de Gy: " + calculationTime + " nanosegundos");

        // Cifrar la consulta
        long startEncryption = System.nanoTime();
        encryptedQuery = encryptAES(query, encryptionKey, iv);
        long endEncryption = System.nanoTime();
        long encryptionTime = endEncryption - startEncryption;
        System.out.println("Tiempo de cifrado de consulta: " + encryptionTime + " nanosegundos");
    }
    private static byte[] encryptAES(String data, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data.getBytes());
    }

    private static byte[] signData(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static void main(String[] args) {
        try {
            SecureClient client = new SecureClient("localhost", 8000);

            // Envío de consultas y medición de tiempo
            long startQueryTime = System.nanoTime();
            client.sendQuery("10");
            long endQueryTime = System.nanoTime();
            long totalQueryTime = endQueryTime - startQueryTime;
            System.out.println("Tiempo total de envío y procesamiento de consulta en el cliente: " + totalQueryTime + " nanosegundos");

            client.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}