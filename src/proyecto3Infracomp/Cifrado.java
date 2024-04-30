package proyecto3Infracomp;
import java.util.*;
import java.security.*;
import javax.crypto.*;


public class Cifrado {
	    private Cipher cipher;

	    public Cifrado(SecretKey key) throws Exception {
	        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        this.cipher.init(Cipher.ENCRYPT_MODE, key);
	    }

	    public byte[] cifrar(byte[] data) throws Exception {
	        return cipher.doFinal(data);
	    }
	}