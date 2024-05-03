package P3infra;

import java.security.*;
import javax.crypto.*;

public class CifradoAsimetrico {



	    public CifradoAsimetrico() {

	    }

	    public byte[] cifrarLlave(Key llaveCifrado, String reto) {
	        byte[] retoCifrado;

	        try {
	            Cipher cifradorRSA = Cipher.getInstance("RSA");
	            String retoEnTexto = String.valueOf(reto);
	            byte[] retoEnBytes = retoEnTexto.getBytes();
	            
	            cifradorRSA.init(Cipher.ENCRYPT_MODE, llaveCifrado);
	            retoCifrado = cifradorRSA.doFinal(retoEnBytes);
	            return retoCifrado;

	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	            return null;
	        }
	    }

	    public byte[] descifrarLlave(Key llaveDescifrado, byte[] textoCifrado) {
	        byte[] textoDescifrado;
	        try {
	            Cipher cifradorRSA = Cipher.getInstance("RSA");
	            cifradorRSA.init(Cipher.DECRYPT_MODE, llaveDescifrado);
	            textoDescifrado = cifradorRSA.doFinal(textoCifrado);
	            return textoDescifrado;

	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	            return null;
	        }
	    }
	}
