package P3infra;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


public class Cliente extends Thread {

    static Random aleatorio = new Random();
    private static final String valid_char = String.join("", 
    	    "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 
    	    "abcdefghijklmnopqrstuvwxyz", 
    	    "0123456789"
    	);

    private Long reto;
    private String login;
    private String contrasenia;
    public Cliente() {
        SecureRandom random = new SecureRandom();
        this.reto = random.nextLong(10000);
        this.contrasenia = generateRandomString(8);

    }
    
    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(valid_char.length());
            char randomChar = valid_char.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    
    }
    public String verificarReto(byte[] retoCifrado, CifradoAsimetrico cifrador, PublicKey llavePublica) {

        byte[] descifrado = cifrador.descifrarLlave(llavePublica, retoCifrado);

        String retoDescifrado = new String(descifrado, StandardCharsets.UTF_8);
        String retoString = String.valueOf(this.reto);

        if (retoDescifrado.equals(retoString)) {
            return "OK";
        } else {
            return "ERROR";
        }
    }

    public String verificarDH(byte[] infoDHCifrado, BigInteger p, BigInteger g, BigInteger gx, byte[] iv,
            PublicKey llavePublica, CifradoAsimetrico cifrador) {
        byte[] infoDescifrada = cifrador.descifrar(llavePublica, retoCifrado);
        String cadenaOriginal = new String(descifrado, StandardCharsets.UTF_8);
        System.out.println("Cadena original: " + cadenaOriginal);
        String[] data = cadenaOriginal.split("$");
        // Comparación p
        if (!data[0].equals(p.toString())) {
            return "ERROR";
        }
        if (!data[1].equals(g.toString())) {
            return "ERROR";
        }
        if (!data[2].equals(gx.toString())) {
            return "ERROR";
        }
        return "OK";
    }



    public Long getReto() {
        return this.reto;
    }

    public String getLogin() {
        return this.login;
    }

    public String getContrasenia() {
        return this.contrasenia;
    }

  
}